name: Build Android TV APK

on:
  workflow_dispatch:

jobs:
  build:

    runs-on: ubuntu-latest

    steps:

      - name: Checkout
        uses: actions/checkout@v4


      - name: Setup Flutter
        uses: subosito/flutter-action@v2
        with:
          channel: master


      - name: Flutter info
        run: |
          flutter --version
          dart --version


      - name: Get dependencies
        run: |
          cd simple_live_tv_app
          flutter pub get


      - name: Fix Android signing
        run: |
          python3 - <<'EOF'
          from pathlib import Path

          p = Path("simple_live_tv_app/android/app/build.gradle.kts")

          s = p.read_text()

          s = s.replace(
          'keyAlias = keystoreProperties["keyAlias"] as String',
          'keyAlias = keystoreProperties["keyAlias"] as String?'
          )

          s = s.replace(
          'keyPassword = keystoreProperties["keyPassword"] as String',
          'keyPassword = keystoreProperties["keyPassword"] as String?'
          )

          s = s.replace(
          'storePassword = keystoreProperties["storePassword"] as String',
          'storePassword = keystoreProperties["storePassword"] as String?'
          )

          s = s.replace(
          'isMinifyEnabled = true',
          'isMinifyEnabled = false'
          )

          s = s.replace(
          'isShrinkResources = true',
          'isShrinkResources = false'
          )

          p.write_text(s)

          EOF


      - name: Build APK
        run: |
          cd simple_live_tv_app
          flutter build apk --release


      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: simple-live-tv-release
          path: |
            simple_live_tv_app/build/app/outputs/flutter-apk/*.apk
