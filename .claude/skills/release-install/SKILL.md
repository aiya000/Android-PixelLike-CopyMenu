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

4. Only where that fails with `INSTALL_FAILED_UPDATE_INCOMPATIBLE`, uninstall the installed copy and
   install again. Do not uninstall pre-emptively -- it also clears the app's data:

    ```bash
    adb uninstall io.github.aiya000.pixellikecopymenu
    adb install app/build/outputs/apk/release/app-release.apk
    ```

5. Report `Success` or the adb error verbatim

## Notes

- Debug and release have different application ids -- `io.github.aiya000.pixellikecopymenu.debug` and
  `io.github.aiya000.pixellikecopymenu` (`applicationIdSuffix` in `app/build.gradle.kts`) -- so both are
  installed at once and this never touches the debug build
- `INSTALL_FAILED_UPDATE_INCOMPATIBLE` means the installed copy was signed with a different key. The signing
  key has changed twice already: builds up to 2026-09-12 used the Android debug keystore, and the personal
  release key itself was regenerated on 2026-09-12 as well. Both times the fix was the uninstall of step 4
