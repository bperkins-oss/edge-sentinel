/*
 * Edge Sentinel — Native DIAG Helper (JNI)
 * Copyright (C) 2024 BP22 Intel
 *
 * Ported from SnoopSnitch diag-helper.c
 * Original Copyright (C) 2014 Security Research Labs (SRLabs)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * ---------------------------------------------------------------------------
 * Qualcomm DIAG interface for Android
 *
 * This JNI library provides direct access to /dev/diag on rooted Qualcomm
 * devices. The DIAG port exposes baseband processor messages including:
 *   - GSM/WCDMA/LTE radio resource (RR) signaling
 *   - Cipher Mode Commands (reveals encryption algorithm in use)
 *   - Protocol anomalies indicating IMSI catcher activity
 *
 * Architecture:
 *   Kotlin (DiagBridge.kt) --JNI--> this library --> /dev/diag (kernel)
 *
 * The library is safe to load on non-rooted devices — all functions check
 * for a valid file descriptor before attempting operations and return
 * error codes on failure rather than crashing.
 * ---------------------------------------------------------------------------
 */

#include <jni.h>
#include <android/log.h>

#include <errno.h>
#include <fcntl.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

#define LOG_TAG "DiagHelper"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

/* Qualcomm DIAG ioctl constants */
#define DIAG_IOCTL_SWITCH_LOGGING  7
#define MEMORY_DEVICE_MODE         2

/* Maximum buffer size for a single DIAG read (1 MB) */
#define DIAG_BUF_SIZE  1000000

/*
 * Qualcomm DIAG logging mode parameter structure.
 * Used by newer kernels (post MSM kernel commit ae92f0b2) where
 * DIAG_IOCTL_SWITCH_LOGGING takes a pointer instead of an int.
 */
struct diag_logging_mode_param_t {
    uint32_t req_mode;
    uint32_t peripheral_mask;
    uint8_t  mode_param;
} __attribute__((packed));

/* Userspace log type prefix for DIAG device framing */
#define USER_SPACE_LOG_TYPE  32

/* Per-session state: file descriptor for /dev/diag (-1 = closed) */
static int diag_fd = -1;

/* Read buffer — static to avoid repeated large allocations */
static char read_buf[DIAG_BUF_SIZE];

/* -----------------------------------------------------------------------
 * JNI function: nativeCheckDiagDevice
 *
 * Checks whether /dev/diag exists and is a character device.
 * Does NOT attempt to open it (no root required for this check).
 *
 * Returns: true if /dev/diag exists as a character device
 * ----------------------------------------------------------------------- */
JNIEXPORT jboolean JNICALL
Java_com_bp22intel_edgesentinel_native_DiagBridge_nativeCheckDiagDevice(
    JNIEnv *env __attribute__((unused)),
    jobject thiz __attribute__((unused)))
{
    struct stat st;
    if (stat("/dev/diag", &st) != 0) {
        LOGD("nativeCheckDiagDevice: /dev/diag not found: %s", strerror(errno));
        return JNI_FALSE;
    }
    if (!S_ISCHR(st.st_mode)) {
        LOGW("nativeCheckDiagDevice: /dev/diag exists but is not a character device");
        return JNI_FALSE;
    }
    LOGD("nativeCheckDiagDevice: /dev/diag is available");
    return JNI_TRUE;
}

/* -----------------------------------------------------------------------
 * JNI function: nativeOpenDiag
 *
 * Opens /dev/diag and switches the Qualcomm DIAG driver to memory
 * device mode so that userspace can read baseband messages.
 *
 * Tries two ioctl calling conventions for kernel compatibility:
 *   1. Pass MEMORY_DEVICE_MODE as an int (older kernels)
 *   2. Pass a pointer to diag_logging_mode_param_t (newer kernels)
 *
 * Returns: file descriptor (>= 0) on success, negative errno on failure
 * ----------------------------------------------------------------------- */
JNIEXPORT jint JNICALL
Java_com_bp22intel_edgesentinel_native_DiagBridge_nativeOpenDiag(
    JNIEnv *env __attribute__((unused)),
    jobject thiz __attribute__((unused)))
{
    if (diag_fd >= 0) {
        LOGW("nativeOpenDiag: already open (fd=%d)", diag_fd);
        return diag_fd;
    }

    LOGI("nativeOpenDiag: opening /dev/diag");

    diag_fd = open("/dev/diag", O_RDWR | O_CLOEXEC);
    if (diag_fd < 0) {
        LOGE("nativeOpenDiag: failed to open /dev/diag: %s", strerror(errno));
        return -errno;
    }

    /*
     * Switch the DIAG driver to memory device mode.
     * Try the legacy int-based ioctl first, then the struct-based variant.
     * See SnoopSnitch diag-helper.c for history on this dual approach.
     */
    int rv = ioctl(diag_fd, DIAG_IOCTL_SWITCH_LOGGING, MEMORY_DEVICE_MODE);
    if (rv < 0) {
        int saved_errno = errno;
        struct diag_logging_mode_param_t mode_param = {
            .req_mode = MEMORY_DEVICE_MODE,
            .peripheral_mask = 0,
            .mode_param = 1
        };
        rv = ioctl(diag_fd, DIAG_IOCTL_SWITCH_LOGGING, &mode_param);
        if (rv < 0) {
            LOGE("nativeOpenDiag: ioctl SWITCH_LOGGING failed: %s / %s",
                 strerror(saved_errno), strerror(errno));
            close(diag_fd);
            diag_fd = -1;
            return -errno;
        }
    }

    LOGI("nativeOpenDiag: /dev/diag opened successfully (fd=%d)", diag_fd);
    return diag_fd;
}

/* -----------------------------------------------------------------------
 * JNI function: nativeCloseDiag
 *
 * Closes the DIAG device file descriptor.
 * Safe to call even if the device was never opened.
 * ----------------------------------------------------------------------- */
JNIEXPORT void JNICALL
Java_com_bp22intel_edgesentinel_native_DiagBridge_nativeCloseDiag(
    JNIEnv *env __attribute__((unused)),
    jobject thiz __attribute__((unused)))
{
    if (diag_fd >= 0) {
        LOGI("nativeCloseDiag: closing fd=%d", diag_fd);
        close(diag_fd);
        diag_fd = -1;
    }
}

/* -----------------------------------------------------------------------
 * JNI function: nativeReadDiag
 *
 * Reads raw bytes from /dev/diag into a Java byte array.
 *
 * The Qualcomm DIAG device returns data in a device-specific framing:
 *   [type: uint32] [nelem: uint32] [len: uint32] [data...] ...
 * where type == USER_SPACE_LOG_TYPE (32) for userspace log messages.
 *
 * This function returns the raw device output. The Kotlin layer
 * (DiagMessageParser) handles de-framing and CRC validation.
 *
 * Returns: byte array with raw DIAG data, or null on error / not open
 * ----------------------------------------------------------------------- */
JNIEXPORT jbyteArray JNICALL
Java_com_bp22intel_edgesentinel_native_DiagBridge_nativeReadDiag(
    JNIEnv *env,
    jobject thiz __attribute__((unused)))
{
    if (diag_fd < 0) {
        return NULL;
    }

    ssize_t bytes_read = read(diag_fd, read_buf, DIAG_BUF_SIZE);

    /* Empty read (interrupted) — return empty array, not null */
    if (bytes_read == 0) {
        return (*env)->NewByteArray(env, 0);
    }

    if (bytes_read < 0) {
        LOGE("nativeReadDiag: read error: %s", strerror(errno));
        return NULL;
    }

    if (bytes_read == DIAG_BUF_SIZE) {
        LOGW("nativeReadDiag: read filled entire buffer — possible data loss");
    }

    jbyteArray result = (*env)->NewByteArray(env, (jsize)bytes_read);
    if (result == NULL) {
        return NULL;  /* OOM */
    }

    (*env)->SetByteArrayRegion(env, result, 0, (jsize)bytes_read, (jbyte *)read_buf);
    return result;
}

/* -----------------------------------------------------------------------
 * JNI function: nativeWriteDiag
 *
 * Writes a command to /dev/diag (e.g., log mask configuration).
 * The data should already be properly framed by the Kotlin layer.
 *
 * Returns: number of bytes written, or negative errno on error
 * ----------------------------------------------------------------------- */
JNIEXPORT jint JNICALL
Java_com_bp22intel_edgesentinel_native_DiagBridge_nativeWriteDiag(
    JNIEnv *env,
    jobject thiz __attribute__((unused)),
    jbyteArray data)
{
    if (diag_fd < 0) {
        return -EBADF;
    }

    jsize len = (*env)->GetArrayLength(env, data);
    if (len <= 0 || len > DIAG_BUF_SIZE) {
        return -EINVAL;
    }

    jbyte *buf = (*env)->GetByteArrayElements(env, data, NULL);
    if (buf == NULL) {
        return -ENOMEM;
    }

    ssize_t written = write(diag_fd, buf, (size_t)len);

    (*env)->ReleaseByteArrayElements(env, data, buf, JNI_ABORT);

    if (written < 0) {
        LOGE("nativeWriteDiag: write error: %s", strerror(errno));
        return -errno;
    }

    return (jint)written;
}

/* -----------------------------------------------------------------------
 * JNI function: nativeIsOpen
 *
 * Returns whether the DIAG device is currently open.
 * ----------------------------------------------------------------------- */
JNIEXPORT jboolean JNICALL
Java_com_bp22intel_edgesentinel_native_DiagBridge_nativeIsOpen(
    JNIEnv *env __attribute__((unused)),
    jobject thiz __attribute__((unused)))
{
    return diag_fd >= 0 ? JNI_TRUE : JNI_FALSE;
}
