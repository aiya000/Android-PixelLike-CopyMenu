# PixelLike CopyMenu

クリップボードの中身をすぐに編集・共有するための、小さな Android アプリ。

起動すると画面の左下にコピー済みのテキストが表示され、その隣の共有ボタンから OS の共有メニューを開ける。

## 機能

- 起動時に、クリップボードの現在のテキストをカードとして表示する。カードの高さは固定で、入りきらないテキストはカードの中でスクロールする
- クリップボードがテキストでない場合は、`テキストをコピーしていません` とトーストを出して終了する
- カードをタップすると編集パネルが開く。パネルの外側をタップすると編集を終了し、編集後のテキストをクリップボードにコピーする
    - テキスト欄の左上の丸い閉じるボタンは、編集を破棄して閉じる
- 共有ボタンをタップすると、`Intent.ACTION_SEND` で OS の共有メニューを開く
- メニューの外側をタップするとアプリを終了する

## 構成

- 言語: Kotlin
- UI: Jetpack Compose
- applicationId: `io.github.aiya000.pixellikecopymenu`
- minSdk 26 / targetSdk 35 / compileSdk 35

```
app/src/main/kotlin/io/github/aiya000/pixellikecopymenu/
├── MainActivity.kt     -- クリップボードの読み取りと画面の起動
├── CopyMenuScreen.kt   -- 左下のカードと共有ボタン
├── EditOverlay.kt      -- 編集パネル
├── Clipboard.kt        -- クリップボードと共有のヘルパー
└── Theme.kt            -- 配色
```

## ビルド

Android Studio は不要。Android SDK (platform 35, build-tools) と JDK 17 があればよい。

`local.properties` に SDK の場所を書く。

```properties
sdk.dir=/path/to/Android/Sdk
```

JDK 17 が `PATH` にある状態で、

```console
$ ./gradlew :app:assembleDebug
```

`app/build/outputs/apk/debug/app-debug.apk` ができる。

mise で JDK を管理している場合は、

```console
$ mise exec java@17 -- ./gradlew :app:assembleDebug
```

## インストール

```console
$ adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 補足

Android 10 (API 29) 以降、クリップボードはフォアグラウンドでウィンドウフォーカスを持つアプリしか読めない。
そのため `MainActivity` では `onCreate` ではなく `onWindowFocusChanged` でクリップボードを読んでいる。

## ライセンス

[MIT License](LICENSE)
