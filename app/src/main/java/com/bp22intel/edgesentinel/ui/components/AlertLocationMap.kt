/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this software, in whole or in
 * part, is strictly prohibited without prior written permission from
 * BP22 Intel.
 */

package com.bp22intel.edgesentinel.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bp22intel.edgesentinel.ui.theme.Surface
import com.bp22intel.edgesentinel.ui.theme.TextPrimary
import com.bp22intel.edgesentinel.ui.theme.TextSecondary
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

/**
 * Compact map card showing the estimated location of a detected threat.
 *
 * Displays a dark-themed OpenStreetMap centered on the tower/threat location
 * with an accuracy radius circle and distance label.
 *
 * @param latitude    Estimated latitude of the threat source
 * @param longitude   Estimated longitude of the threat source
 * @param accuracyM   Estimated accuracy in meters (shown as a circle)
 * @param label       Label for the marker (e.g., "Fake BTS — CID 245249855")
 * @param userLat     User's current latitude (optional, shows user dot + distance)
 * @param userLng     User's current longitude (optional)
 */
@Composable
fun AlertLocationMap(
    latitude: Double,
    longitude: Double,
    accuracyM: Double = 500.0,
    label: String = "Estimated Location",
    userLat: Double? = null,
    userLng: Double? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val cartoDark = remember {
        XYTileSource(
            "CartoDB Dark Matter",
            0, 19, 256, ".png",
            arrayOf(
                "https://a.basemaps.cartocdn.com/dark_all/",
                "https://b.basemaps.cartocdn.com/dark_all/",
                "https://c.basemaps.cartocdn.com/dark_all/"
            )
        )
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(cartoDark)
            setMultiTouchControls(true)
            setBuiltInZoomControls(false)
            setBackgroundColor(0xFF1a1a2e.toInt())
            minZoomLevel = 5.0
            maxZoomLevel = 19.0
        }
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = "EdgeSentinel/2.0"
    }

    LaunchedEffect(latitude, longitude, userLat, userLng) {
        mapView.overlays.clear()

        val threatPoint = GeoPoint(latitude, longitude)

        // Accuracy radius circle
        val circle = Polygon(mapView).apply {
            points = Polygon.pointsAsCircle(threatPoint, accuracyM)
            fillPaint.apply {
                color = 0x20EF4444.toInt()
                style = Paint.Style.FILL
            }
            outlinePaint.apply {
                color = 0xAAEF4444.toInt()
                style = Paint.Style.STROKE
                strokeWidth = 2f
            }
            title = "Estimated accuracy: ${accuracyM.toInt()}m"
        }
        mapView.overlays.add(circle)

        // Threat marker (red dot with pulsing ring)
        val threatBitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888).also { bmp ->
            val c = Canvas(bmp)
            // Outer glow
            c.drawCircle(24f, 24f, 24f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0x60EF4444.toInt()
                style = Paint.Style.FILL
            })
            // Inner fill
            c.drawCircle(24f, 24f, 14f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFFEF4444.toInt()
                style = Paint.Style.FILL
            })
            // White border
            c.drawCircle(24f, 24f, 14f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFFFFFFFF.toInt()
                style = Paint.Style.STROKE
                strokeWidth = 2f
            })
            // "!" in center
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFFFFFFFF.toInt()
                textSize = 18f
                textAlign = Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            c.drawText("!", 24f, 30f, textPaint)
        }

        val threatMarker = Marker(mapView).apply {
            position = threatPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            title = label
            icon = BitmapDrawable(context.resources, threatBitmap)
        }
        mapView.overlays.add(threatMarker)

        // User location (blue dot) if available
        if (userLat != null && userLng != null) {
            val userBitmap = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888).also { bmp ->
                val c = Canvas(bmp)
                c.drawCircle(14f, 14f, 14f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0x554488FF.toInt()
                    style = Paint.Style.FILL
                })
                c.drawCircle(14f, 14f, 8f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFF4488FF.toInt()
                    style = Paint.Style.FILL
                })
                c.drawCircle(14f, 14f, 8f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFFFFFFFF.toInt()
                    style = Paint.Style.STROKE
                    strokeWidth = 2f
                })
            }
            val userMarker = Marker(mapView).apply {
                position = GeoPoint(userLat, userLng)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                title = "Your Location"
                icon = BitmapDrawable(context.resources, userBitmap)
            }
            mapView.overlays.add(userMarker)
        }

        // Zoom to show the threat and accuracy circle
        val zoomLevel = when {
            accuracyM > 2000 -> 13.0
            accuracyM > 1000 -> 14.0
            accuracyM > 500 -> 15.0
            accuracyM > 200 -> 16.0
            else -> 17.0
        }
        mapView.controller.setZoom(zoomLevel)
        mapView.controller.setCenter(threatPoint)
        mapView.invalidate()
    }

    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose { mapView.onPause() }
    }

    // Distance label
    val distanceText = if (userLat != null && userLng != null) {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(userLat, userLng, latitude, longitude, results)
        val distM = results[0]
        when {
            distM < 100 -> "${distM.toInt()}m from you"
            distM < 1000 -> "${distM.toInt()}m away"
            else -> "%.1f km away".format(distM / 1000f)
        }
    } else null

    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Estimated Location",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            if (distanceText != null) {
                Text(
                    text = distanceText,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Text(
                text = "Accuracy: ±${accuracyM.toInt()}m",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Map
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AndroidView(
                    factory = { mapView },
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )

                // OSM attribution
                Text(
                    text = "© OpenStreetMap · © CARTO",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                )
            }
        }
    }
}
