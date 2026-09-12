---
name: release-build
description: Build the signed release APK of this CopyMenu app with gradle. Use when the user asks for a production or release build, or before the `release-install` skill.
---

# release-build

Build the release APK. Gradle signs it itself, so there is no separate signing step.

## Environment

- gradle needs a JDK. There is no `java` on `PATH`; this machine keeps JDK 17 in mise:

    ```bash
    mise exec java@17.0.2 -- ./gradlew ...
    ```

- The SDK location comes from `local.properties` (`sdk.dir=$HOME/Android/Sdk`), which is not tracked by git.
  Recreate it if it is missing
- Outside the sandbox `$TMPDIR` is empty. Always give log files an **absolute** path inside the session
  scratchpad directory
- The Bash sandbox is disabled by default on this machine. If it is ever turned back on, gradle fails with
  `gradle-8.9-bin.zip.lck (Read-only file system)` and needs `dangerouslyDisableSandbox: true`

## Signing

The signing key is personal and deliberately lives **outside this repository**, so a checkout carries no
secret. `app/build.gradle.kts` reads four properties from `~/.gradle/gradle.properties`:

```properties
AIYA000_STORE_FILE=<absolute path to the keystore>
AIYA000_STORE_PASSWORD=<password>
AIYA000_KEY_ALIAS=aiya000
AIYA000_KEY_PASSWORD=<the same password>
```

- `~/.gradle/gradle.properties` is read by **every** gradle build this user runs, and nothing needs to be
  configured per project. The same reach is why no password belongs on a command line or in any repository
- `AIYA000_STORE_FILE` has to be written out in full; gradle's `file()` does not expand `~`
- The keystore itself is kept in a **private** repository, and the passwords are deliberately **not** stored
  next to it -- they live only in the user's password manager and in `~/.gradle/gradle.properties`. Ask the
  user where the keystore is rather than guessing
- Where those properties are absent the build still succeeds and simply produces an unsigned APK. A release
  that is meant to be published must never be built that way
- **Never put a password on a command line or into this repository.** `keytool` has no `:env` or `:file`
  option, so creating or changing a key is done by the user in their own terminal, not through Claude Code

## Behavior

1. Run the build, logging to the scratchpad:

    ```bash
    mise exec java@17.0.2 -- ./gradlew :app:assembleRelease -q > <scratchpad>/release-build.log 2>&1; echo "EXIT=$?" >> <scratchpad>/release-build.log
    ```

2. Check the log for `^e: `, `error:`, `FAILED` and the `EXIT=` line

3. Verify the signature -- this needs no password:

    ```bash
    export JAVA_HOME="$(mise where java@17.0.2)"
    export PATH="$JAVA_HOME/bin:$PATH"
    "$HOME/Android/Sdk/build-tools/36.0.0/apksigner" verify -v --print-certs app/build/outputs/apk/release/app-release.apk
    ```

    Expected, and a mismatch means the wrong key was used:

    - v2 `true`, v3 `true`, v1 `false` (v1 is only needed below API 24; `minSdk` is 26)
    - `Signer #1 certificate DN: CN=aiya000, O=aiya000, C=JP`
    - `Signer #1 certificate SHA-256 digest: aa0139340997a7a4159adb278f22087226967442d17510245f6e34e2dcb7569b`
    - `Signer #1 key size (bits): 4096`

4. Report the APK path:

    ```
    app/build/outputs/apk/release/app-release.apk
    ```

## Notes

- Gradle zipaligns the APK as part of packaging. The old manual `zipalign` + `apksigner sign` steps, and the
  `app-release-unsigned.apk` / `app-release-aligned.apk` / `app-release-signed.apk` files they produced, are
  gone. Only `app-release.apk` is built now, and it is the only file to publish
- Before a release that is published, bump `versionCode` (and usually `versionName`) in
  `app/build.gradle.kts`. Android refuses to install an update whose `versionCode` did not increase
- `isMinifyEnabled` is `false`, so there is no R8 step and `proguard-rules.pro` is effectively empty.
  Turn minification on only together with keep rules for the Compose runtime
- Do not install automatically; that is the `release-install` skill
