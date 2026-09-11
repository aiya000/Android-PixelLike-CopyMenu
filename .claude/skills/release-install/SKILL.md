---
name: release-install
description: Install the signed release APK of this CopyMenu app on the connected device with adb. Use when the user asks to install or deploy the production / release build; build it first with the `release-build` skill if needed.
---

# release-install

Install the signed release APK on the device connected via adb.

## Environment

- The device is usually connected with **wireless adb**. The address (`<ip>:<port>`) changes between sessions and
  is not stored in the repository
- adb has reached the device from **inside** the Bash sandbox on this machine. Try the plain command first and
  only fall back to `dangerouslyDisableSandbox: true` if `adb devices` comes back empty

## Behavior

1. Make sure the signed APK exists and is fresh (see the `release-build` skill):

    ```
    app/build/outputs/apk/release/app-release-signed.apk
    ```

    If it is missing or older than the latest source change, run the `release-build` skill first

2. Check the device with `adb devices`; if none is listed, `adb connect <ip>:<port>` (ask the user for the
   address when it is not known from the conversation)
3. Install:

    ```bash
    adb install -r app/build/outputs/apk/release/app-release-signed.apk
    ```

4. Report `Success` or the adb error verbatim

## Notes

- Debug and release have different application ids -- `io.github.aiya000.pixellikecopymenu` and
  `io.github.aiya000.pixellikecopymenu.debug` (`applicationIdSuffix` in `app/build.gradle.kts`) -- so both
  are installed at once and this never touches the debug build
- Updating an earlier personal release build works only when it was signed with the same key
  (the Android debug keystore, see `release-build`)
