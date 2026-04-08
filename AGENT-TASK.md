# Edge Sentinel — Android App Build Task

## What You're Building
A modern Android app called **Edge Sentinel** — a cellular threat detection and alerting tool forked conceptually from SnoopSnitch. This is a fresh Kotlin/Jetpack Compose build that will later integrate ported native code from SnoopSnitch's DIAG layer.

## Reference Material
- **Concept brief:** `../PROJECT.md` — read this first for full context
- **Upstream SnoopSnitch source:** `../upstream-snoopsnitch/` — reference for detection logic, DO NOT copy Java files wholesale

## Requirements

### Project Setup
- Package: `com.bp22intel.edgesentinel`
- Min SDK 26, Target SDK 35
- Kotlin, Jetpack Compose, Material 3
- Hilt for DI, Room for local DB, WorkManager for periodic scans
- GPLv3 license header in all source files
- Build with Gradle (Kotlin DSL)

### App Architecture (Clean Architecture)
```
com.bp22intel.edgesentinel/
├── data/
│   ├── local/          # Room database, DAOs
│   ├── sensor/         # Cell info collectors, telephony monitors
│   └── repository/     # Repository implementations
├── domain/
│   ├── model/          # Threat, CellTower, Alert, DetectionResult
│   ├── repository/     # Repository interfaces
│   └── usecase/        # DetectThreats, AnalyzeCell, GetAlerts
├── detection/
│   ├── engine/         # ThreatDetectionEngine (orchestrates all detectors)
│   ├── detectors/      # Individual detector classes:
│   │   ├── FakeBtsDetector.kt
│   │   ├── NetworkDowngradeDetector.kt
│   │   ├── SilentSmsDetector.kt
│   │   ├── TrackingPatternDetector.kt
│   │   └── CipherModeDetector.kt (stub — requires root)
│   └── scoring/        # Threat scoring model (port ImsiCatcher scoring from upstream)
├── service/
│   ├── MonitoringService.kt       # Foreground service for continuous monitoring
│   └── ScanWorker.kt             # WorkManager periodic scan
├── ui/
│   ├── theme/          # Dark theme, operational aesthetic
│   ├── dashboard/      # Main threat dashboard screen
│   ├── alerts/         # Alert list + detail screens
│   ├── cellinfo/       # Current cell tower info display
│   ├── settings/       # Settings screen
│   └── components/     # Reusable composables (ThreatIndicator, AlertCard, etc.)
└── di/                 # Hilt modules
```

### Detection Layer (Non-Root — Priority)

Use Android's public telephony APIs. The detection engine should:

1. **Cell Info Monitoring** — Poll `TelephonyManager.getAllCellInfo()` periodically
   - Track cell tower changes (CID, LAC/TAC, MCC, MNC)
   - Monitor neighbor cell list for anomalies
   - Detect unexpected cell reselection patterns

2. **Network Downgrade Detection** — Use `TelephonyCallback.onDisplayInfoChanged()`
   - Alert on 4G/5G → 2G/3G fallbacks
   - Track network type history to distinguish normal roaming from forced downgrades

3. **Signal Anomaly Detection**
   - Track signal strength over time via `CellSignalStrength`
   - Detect abnormally strong signals (potential nearby fake BTS)
   - Flag sudden signal changes not correlated with movement

4. **LAC/TAC Tracking**
   - Maintain a local database of known LAC/TAC for locations
   - Alert on unexpected LAC/TAC values at known locations

5. **Silent SMS Detection** (limited without root)
   - Monitor for Type-0 SMS via BroadcastReceiver where possible
   - Note: full detection requires DIAG access

6. **5G NR Monitoring** (Android 10+)
   - Use `CellInfoNr` / `CellSignalStrengthNr` APIs
   - Track NR connection state, NSA vs SA mode
   - Monitor NR neighbor cells

### Threat Scoring
Port the scoring concept from SnoopSnitch's `ImsiCatcher.java`:
- Multiple weighted indicators (a1, a2, k1, k2, c1-c5, t1, t3, t4, r1, r2, f1)
- Composite score → threat level mapping
- Confidence indicator per detection

### UI Design

**Theme:** Dark background (#0D1117 primary), accent colors for threat levels:
- GREEN (#10B981) — All clear
- YELLOW (#F59E0B) — Suspicious activity
- RED (#EF4444) — Active threat detected

**Dashboard Screen (Main):**
- Large threat level indicator at top (circular, color-coded)
- Current cell info card: tower ID, network type (LTE/NR/etc), signal strength, operator
- "Monitoring active" status with uptime counter
- Recent alerts list (last 10) with severity badges and timestamps
- FAB or button to force immediate scan

**Alert List Screen:**
- Chronological alert feed
- Filter by severity (all/yellow/red)
- Each card shows: timestamp, threat type, severity, one-line summary

**Alert Detail Screen:**
- Threat type and severity header
- What was detected (plain language)
- Confidence level (Low/Medium/High)
- Recommended action
- Expandable "Technical Details" section with raw data
- Export button (share as text/JSON)

**Cell Info Screen:**
- Current serving cell details
- Neighbor cell list
- Network type history graph (last 24h)
- Known cell database stats

**Settings Screen:**
- Monitoring toggle (on/off)
- Detection sensitivity: Low (fewer false positives) / Medium / High (more sensitive)
- Notification preferences: Sound, Vibration, Silent, Do Not Disturb override
- Advanced mode: Enable root-required features (greyed out if not rooted)
- Export all logs
- About / License (GPLv3 notice)

### Foreground Service
- Persistent notification showing current monitoring status and threat level
- Must survive app being swiped from recents
- Battery-conscious: configurable scan interval (default 30 seconds for active, 5 min for passive)

### Room Database Schema
```
cells: id, cid, lac_tac, mcc, mnc, signal_strength, network_type, latitude, longitude, first_seen, last_seen, times_seen
alerts: id, timestamp, threat_type, severity, confidence, summary, details_json, cell_id, acknowledged
scans: id, timestamp, cell_count, threat_level, duration_ms
settings: key, value
```

### What NOT to Build (Yet)
- Qualcomm DIAG integration (will be Phase 2 — native code port)
- Device-to-device mesh alerting (Phase 3)
- Any cloud/network features
- Map view
- User accounts or registration

## Build & Run
The app should compile and run. Use mock/simulated data for detection results where real telephony APIs aren't available (since this builds on a dev machine, not a phone). Include a "Demo Mode" toggle in settings that generates sample alerts for UI testing.

## Important
- Every Kotlin source file must have a GPLv3 header comment
- Use the upstream SnoopSnitch source in `../upstream-snoopsnitch/` as REFERENCE for detection algorithms, but write all new code in Kotlin
- The ImsiCatcher scoring coefficients (a1, a2, a4, a5, k1, k2, c1-c5, t1, t3, t4, r1, r2, f1) should be preserved — they represent years of research
- No Google Play Services dependencies
- No network calls of any kind — fully offline
