# Student Secure VPN

A complete Kotlin Android Studio project for a policy-compliant VPN client template.

## Included

- Material Design 3 XML UI
- Splash screen, login, guest mode
- Home connect/disconnect UI with status, timer, retry, data counters
- Free/Premium server selection
- Android `VpnService` declaration and lifecycle cleanup
- Official WireGuard Android tunnel backend integration
- DNS presets: Cloudflare, Google, Quad9
- Kill Switch preference with Android lockdown guidance
- Auto-connect on Wi-Fi preference placeholder
- Notifications and foreground service for VPN status
- Runtime VPN and notification permission handling
- MVVM with ViewModel + LiveData + Kotlin Coroutines
- Room settings/server database
- Retrofit API client
- EncryptedSharedPreferences for sensitive values
- Light/Dark theme and tablet-friendly dimensions

## Policy and security notes

This project intentionally does not include real VPN credentials or endpoints. Populate `wireGuardConfig` only from legitimate servers you own or are authorized to use. Do not use this app to bypass telecom billing, evade lawful access controls, or circumvent network restrictions.

Replace `BuildConfig.API_BASE_URL` in `app/build.gradle.kts` with your authorized backend. The sample seed servers are UI placeholders and will show a safe error until real WireGuard profiles are supplied.

## Build

Open the folder in the latest stable Android Studio and sync Gradle. The project uses Kotlin, AndroidX, Material Components, Room, Retrofit, Security Crypto, and the official WireGuard tunnel library.
