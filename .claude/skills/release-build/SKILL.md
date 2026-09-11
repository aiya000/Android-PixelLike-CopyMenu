---
name: release-build
description: Build a signed release APK of this CopyMenu app for personal use (gradle release build, zipalign, sign with the Android debug keystore). Use when the user asks for a production or release build, or before the `release-install` skill.
---

# release-build

Build the release APK and sign it so it can be installed on the user's own device.

## Environment

- gradle and `apksigner` both need a JDK. There is no `java` on `PATH`, and `JAVA_HOME` alone is not enough for
  `apksigner` -- it also needs `java` on `PATH`:

    ```bash
    export JAVA_HOME="$(mise where java@17.0.2)"
    export PATH="$JAVA_HOME/bin:$PATH"
    ```

- Build tools live in `~/Android/Sdk/build-tools/<version>/` (`zipalign`, `apksigner`). Use the newest installed
  version
- Outside the sandbox `$TMPDIR` is empty. Give log files an **absolute** path inside the session scratchpad
  directory

## Signing

The project has no `keystore.properties` and `app/build.gradle.kts` declares no `signingConfig`, so gradle
produces an **unsigned** release APK. Sign it with the Android debug keystore, which is what the user uses for
personal builds:

- keystore: `~/.android/debug.keystore`
- alias: `androiddebugkey`, store and key password: `android`

Future updates must be signed with the **same** key, otherwise `adb install -r` fails with
`INSTALL_FAILED_UPDATE_INCOMPATIBLE`.

## Behavior

Run everything in the background with a scratchpad log (this is slower than a debug build):

```bash
export JAVA_HOME="$(mise where java@17.0.2)"
export PATH="$JAVA_HOME/bin:$PATH"
OUT=app/build/outputs/apk/release
BT=$HOME/Android/Sdk/build-tools/36.0.0
./gradlew :app:assembleRelease -q && echo BUILD_OK \
  && "$BT/zipalign" -f -p 4 "$OUT/app-release-unsigned.apk" "$OUT/app-release-aligned.apk" && echo ALIGN_OK \
  && "$BT/apksigner" sign --ks "$HOME/.android/debug.keystore" --ks-key-alias androiddebugkey --ks-pass pass:android --key-pass pass:android \
       --out "$OUT/app-release-signed.apk" "$OUT/app-release-aligned.apk" && echo SIGN_OK
```

Afterwards verify the signature and report the path:

```bash
"$BT/apksigner" verify --print-certs "$OUT/app-release-signed.apk"
```

```
app/build/outputs/apk/release/app-release-signed.apk
```

## Notes

- `isMinifyEnabled` is `false`, so there is no R8 step and `proguard-rules.pro` is effectively empty.
  Turn minification on only together with keep rules for the Compose runtime
- Do not install automatically; that is the `release-install` skill
