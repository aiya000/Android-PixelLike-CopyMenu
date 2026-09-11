---
name: debug-install
description: Install the built debug APK of this CopyMenu app on the connected device with adb. Use when the user asks to install or deploy the debug build; build it first with the `debug-build` skill if needed.
---

# debug-install

Install the debug APK on the device connected via adb.

## Environment

- The device is usually connected with **wireless adb**. The address (`<ip>:<port>`) changes between sessions and
  is not stored in the repository. It is shown on the device under
  設定 → 開発者向けオプション → ワイヤレスデバッグ
- adb has reached the device from **inside** the Bash sandbox on this machine. Try the plain command first and
  only fall back to `dangerouslyDisableSandbox: true` if `adb devices` comes back empty
- The app reads the clipboard, which Android only allows for the focused app, so it can only be verified by
  actually launching it on a device

## Behavior

1. Make sure the APK exists and is fresh:

    ```
    app/build/outputs/apk/debug/app-debug.apk
    ```

    If it is missing or older than the latest source change, run the `debug-build` skill first

2. Check the device:

    ```bash
    adb devices
    ```

    - If no device is listed, run `adb connect <ip>:<port>` when the address is known from the conversation,
      otherwise ask the user to enable wireless debugging and tell you the address
3. Install:

    ```bash
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    ```

4. Report `Success` or the adb error verbatim

## Notes

- To launch it: `adb shell am start -n io.github.aiya000.pixellikecopymenu/.MainActivity`
- If an older build with a different application id is still installed, uninstall it first
- On a foldable the device has several displays, and `screencap` without `-d` warns and picks an arbitrary one.
  List the display ids with `adb shell dumpsys SurfaceFlinger --display-id` and pass the active one:
  `adb exec-out screencap -p -d <display-id> > shot.png`
- Never install while the user has asked to wait ("インストールは待って") -- build only
