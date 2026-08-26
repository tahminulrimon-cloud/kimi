# 17 Fd Regt Arty — Manpower App (Android, Milestone 1)

Native Android app for daily manpower distribution: a brief-view dashboard
for commanders and full personnel/attendance management for admins. Kotlin,
Jetpack Compose, Room (SQLCipher-encrypted), MVVM, offline-first — no
internet connection required to use the app.

This is **Milestone 1** of a larger spec. See "What's deferred" below for
what isn't built yet.

## Building

This sandbox that generated the project has no Android SDK, so the build
has not been run here. To build:

```
cd android-app
./gradlew assembleDebug      # unsigned debug APK
./gradlew assembleRelease    # release build (debug-signed for now — see note below)
```

Output: `app/build/outputs/apk/debug/app-debug.apk` (or `release/`).

A GitHub Actions workflow (`.github/workflows/android-build.yml`) builds
the debug APK on every push/PR touching `android-app/**` and uploads it as
a build artifact — that's the actual "does this compile" signal for this
project, since it can't be verified locally in this sandbox.

Requires: JDK 17, Android SDK (compileSdk 34, minSdk 26) — Android Studio
Hedgehog/Iguana or newer will fetch these automatically on first open.

## First run

Default admin account: `admin` / `admin` — the app forces a password
change on first login, per spec. 10 sample personnel records and today's
attendance for them are seeded automatically on first launch so the
dashboard has real numbers immediately (all clearly dummy data — replace
before real use).

## Architecture

- **MVVM**: `ui/<feature>/XxxViewModel.kt` exposes `StateFlow<UiState>`;
  Compose screens in the same package collect it.
- **Data**: Room entities/DAOs in `data/local/`, repositories in
  `data/repository/` are the only thing ViewModels talk to. The Room
  database is opened through `net.sqlcipher`'s `SupportFactory`, so the
  on-disk `.db` file is AES-encrypted.
- **DI**: Hilt. `di/DatabaseModule.kt` provides the database/DAOs;
  repositories, `SessionManager`, and `PasswordHasher` use constructor
  injection directly.
- **Auth**: `auth/PasswordHasher.kt` (BCrypt) + `auth/SessionManager.kt`
  (in-memory current user, 15-minute inactivity auto-logout unless
  "Remember Me" was checked at login).
- **Navigation**: `ui/navigation/NavGraph.kt`, a single `NavHost` in
  `MainActivity`. The graph starts at Login; there's no deep link into an
  authenticated screen, so route-level auth guards weren't needed for this
  milestone.
- **Theme**: `ui/theme/` implements "Artillery Dark" exactly per spec —
  see `Color.kt` for the hex values, `Shapes.kt` for the 12dp card corners.

## What's built (Milestone 1)

- Login (dark theme, Remember Me, forced first-login password change)
- 15-minute inactivity auto-logout
- Two-tier roles: Standard Officer (view + own daily entry) vs. Admin
  (full CRUD) — enforced in ViewModels and reflected in the UI
- Dashboard: animated count-up summary cards, sub-unit breakdown with
  manning percentage and a <70% critical-shortage flag, pull-content via
  reactive Flow (recomposes live as data changes)
- Personnel: searchable list, full profile view (all master-record
  fields), admin create/edit with every dropdown from spec section 7,
  date pickers, photo picker
- Daily attendance entry: By Individual / By Sub-Unit / By Status tabs,
  per-person edit dialog (status, duty assignment, location, remarks,
  max 100 chars)
- All dropdown vocabularies from spec section 7, exactly as listed

## What's deferred to later milestones

Called out explicitly rather than silently dropped:

- **Reports** (daily/weekly/monthly, PDF/CSV export, share sheet)
- **Admin Panel**: user management (add/edit/delete officer accounts,
  password reset), master-data editing (the dropdowns are compiled enums
  for now, not admin-editable — that needs a DB-backed lookup table),
  Excel import/export, audit log *viewer* (the log itself is already
  being written by every mutation, so this milestone's data will be there
  once the screen exists)
- Bulk multi-select attendance actions (mark several people at once)
- Biometric login, QR service-number scanner, voice notes, photo evidence
  attachment for sick/AWOL, 0730 daily reminder notification, AWOL alert
  notification, home-screen widget, weekly auto-backup
- Personnel Profile's "Service History" and "Documents" tabs (the tab
  bar exists; those two show a placeholder for now)
- Light theme toggle (dark is the only theme; the toggle setting itself
  is Admin Panel, which is deferred)

## Known limitations to fix before real deployment

- **SQLCipher passphrase** (`AppDatabase.PASSPHRASE`) is a build-time
  constant for now. Before handling real unit data, replace it with a
  key generated on first run and stored via Android Keystore-backed
  `EncryptedSharedPreferences`, so the passphrase isn't in source control.
- **Release signing**: `buildTypes.release` is currently debug-signed so
  `assembleRelease` succeeds without a keystore. Add a real keystore +
  signing config (via CI secrets, not committed to the repo) before
  distributing a release APK.
- Photo picker uses `ACTION_GET_CONTENT`; fine for Milestone 1 but the
  system Photo Picker (`ActivityResultContracts.PickVisualMedia`) avoids
  needing the `READ_MEDIA_IMAGES` permission at all on Android 13+ and is
  worth switching to.

## Distribution

Once a release APK is built and signed: share the APK file directly
(unit file server, QR code linking to a download, or a shared drive).
Installing needs "Install from Unknown Sources" enabled on the officer's
phone for the source app (Settings → Apps → Special access, or the
one-time prompt Android shows on install) — this app is not distributed
via the Play Store.
