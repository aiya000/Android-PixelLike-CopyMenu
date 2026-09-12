---
name: release-install
description: Install the signed release APK of this CopyMenu app on the connected device with adb. Use when the user asks to install or deploy the production / release build; build it first with the `release-build` skill if needed.
---

# release-install

Install the signed release APK on the device connected via adb.

## Environment

- The device is usually connected with **wireless adb**. The address (`<ip>:<port>`) changes between sessions
  and is not stored in the repository. It is shown on the device under
  設定 → 開発者向けオプション → ワイヤレスデバッグ
- The Bash sandbox is disabled by default on this machine. If it is ever turned back on, adb cannot reach the
  device (`Network is unreachable`) and needs `dangerouslyDisableSandbox: true`

## Behavior

1. Make sure the signed APK exists and is fresh (see the `release-build` skill):

    ```
    app/build/outputs/apk/release/app-release.apk
    ```

    If it is missing or older than the latest source change, run the `release-build` skill first

2. Check the device with `adb devices`; if none is listed, `adb connect <ip>:<port>` (ask the user for the
   address when it is not known from the conversation)

3. Install:

    ```bash
    adb install -r app/build/outputs/apk/release/app-release.apk
    ```

4. Report `Success` or the adb error verbatim

## Notes

- Debug and release have different application ids -- `io.github.aiya000.pixellikecopymenu.debug` and
  `io.github.aiya000.pixellikecopymenu` (`applicationIdSuffix` in `app/build.gradle.kts`) -- so both are
  installed at once and this never touches the debug build
- `INSTALL_FAILED_UPDATE_INCOMPATIBLE` means the installed copy was signed with a different key. Builds up to
  2026-09-12 were signed with the Android debug keystore; everything from then on uses the personal release
  key. Uninstall the old copy first -- `adb uninstall io.github.aiya000.pixellikecopymenu` -- which also
  clears its data
