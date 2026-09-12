plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// The release is signed with a personal key that is deliberately kept out of this
// repository. Its location and passwords are read from ~/.gradle/gradle.properties, so a
// checkout carries no secret. Where those properties are absent the release build still
// runs and produces an unsigned APK.
val releaseStoreFile = findProperty("AIYA000_STORE_FILE") as String?
val releaseStorePassword = findProperty("AIYA000_STORE_PASSWORD") as String?
val releaseKeyAlias = findProperty("AIYA000_KEY_ALIAS") as String?
val releaseKeyPassword = findProperty("AIYA000_KEY_PASSWORD") as String?

android {
    namespace = "io.github.aiya000.pixellikecopymenu"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.aiya000.pixellikecopymenu"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        if (releaseStoreFile != null) {
            create("release") {
                storeFile = file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword

                // v1 (JAR signing) is only needed below API 24 and minSdk is 26. v3 is
                // what carries proof-of-rotation, which is the only way to ever move to a
                // different key without asking everyone to reinstall.
                enableV1Signing = false
                enableV2Signing = true
                enableV3Signing = true
            }
        }
    }

    buildTypes {
        debug {
            // A different application id, so that the debug build and the release build
            // can be installed at the same time. Its launcher icon is orange
            // (src/debug/res) and its label says debug.
            applicationIdSuffix = ".debug"
        }

        release {
            // Null where the key is not configured, which leaves the APK unsigned.
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    sourceSets["main"].java.srcDirs("src/main/kotlin")
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
}
