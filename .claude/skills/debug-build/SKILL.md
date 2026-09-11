---
name: debug-build
description: Build the debug APK of this CopyMenu app with gradle. Use when the user asks for a debug build, or before installing a debug build with the `debug-install` skill.
---

# debug-build

Build the debug APK. The project has no product flavors, so there is a single debug variant.

## Environment

- gradle needs a JDK. There is no `java` on `PATH`; this machine keeps JDK 17 in mise:

    ```bash
    mise exec java@17.0.2 -- ./gradlew ...
    ```

- The SDK location comes from `local.properties` (`sdk.dir=$HOME/Android/Sdk`), which is not tracked by git.
  Recreate it if it is missing
- gradle writes to `~/.gradle` and downloads dependencies. This has worked **inside** the Bash sandbox on this
  machine, so try the plain command first and only fall back to `dangerouslyDisableSandbox: true` if it fails
- Outside the sandbox `$TMPDIR` is empty. Always give log files an **absolute** path inside the session
  scratchpad directory

## Behavior

1. Run the build in the background, logging to the scratchpad:

    ```bash
    mise exec java@17.0.2 -- ./gradlew :app:assembleDebug -q > <scratchpad>/debug-build.log 2>&1; echo "EXIT=$?" >> <scratchpad>/debug-build.log
    ```

    - The first build downloads Gradle 8.9 itself (about 130MB) and takes a few minutes; an incremental one
      takes seconds
    - Use `:app:compileDebugKotlin` instead when only a compile check is needed

2. When it finishes, check the log for `^e: `, `error:`, `FAILED` and the `EXIT=` line
3. Report the APK path:

    ```
    app/build/outputs/apk/debug/app-debug.apk
    ```

## Notes

- Debug and release share the application id `io.github.aiya000.pixellikecopymenu`, so they cannot be
  installed side by side
- Do not install automatically. Installing is the `debug-install` skill, run it only when the user asks
