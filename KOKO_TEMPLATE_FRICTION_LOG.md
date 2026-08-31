# 📝 Koko (KMP Contest Starter Kit) Template — Comprehensive Friction Log

This document records the major developer friction points, root causes, debugging discoveries, and actionable template recommendations identified while building **HeroHabit** on top of the `https://github.com/KotlinFoundation/kmp-contest-starter-kit` starter kit.

---

## 🛑 Summary of Major Friction Areas

| # | Friction Point | Severity | Root Cause | Recommended Template Fix |
|---|---|---|---|---|
| **1** | **Xcode Gradle Run Script Failure** | **Critical** | Xcode executes script phases in an isolated shell lacking `$JAVA_HOME` and Homebrew in `$PATH`. | Explicitly export `JAVA_HOME` and `PATH` in the Xcode Run Script phase. |
| **2** | **Simulator Architecture Mismatch (`ios_x64`)** | **Critical** | Modern KMP only targets Apple Silicon (`iosSimulatorArm64`), but Xcode defaults to building `x86_64` for simulators. | Add `"EXCLUDED_ARCHS[sdk=iphonesimulator*]" = "x86_64";` and `ONLY_ACTIVE_ARCH = YES;` to `project.pbxproj`. |
| **3** | **Infinite SwiftPM Resolution Loops** | **High** | Subpackage `Package.swift` files used loose version ranges (`11.8.0...12.999.999`), causing Xcode to endlessly query GitHub tags. | Pin exact matching versions across all subpackage `Package.swift` manifests. |
| **4** | **Crashlytics Script Blocking Debug Builds** | **High** | Crashlytics dSYM upload script configured with 5 hardcoded `Input Files` that do not exist during debug/simulator builds. | Remove from default debug build phases or gate with `if [ "$CONFIGURATION" = "Release" ]`. |
| **5** | **Missing Shared Xcode Scheme** | **Medium** | Missing `xcshareddata/xcschemes/iosApp.xcscheme` caused ephemeral scheme generation and product name confusion. | Commit a shared `iosApp.xcscheme` in the starter kit repo. |
| **6** | **Camera / Photo Picker Simulator Crash** | **Medium** | `FileKit.openCameraPicker()` crashes on simulator without physical camera; missing photo library keys in `Info.plist`. | Wrap camera calls with photo picker fallbacks and populate `NSPhotoLibrary*` keys. |

---

## 🔍 Detailed Friction Breakdown & Solutions

### 1. Xcode Run Script Environment (`JAVA_HOME` & `$PATH`)
* **The Pain**: Developers clicking `Cmd + R` in Xcode experience silent build failures or `./gradlew` errors because Xcode's subshell does not inherit the developer's shell environment (`.zshrc` / `.bash_profile`).
* **Root Cause**: `/usr/libexec/java_home` and Homebrew paths (`/opt/homebrew/bin`) are not automatically exported in Xcode build phase subshells.
* **Template Solution**: In `iosApp.xcodeproj/project.pbxproj`, update the `embedAndSignAppleFrameworkForXcode` script to:
  ```sh
  export JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null || echo "/Library/Java/JavaVirtualMachines/zulu-26.jdk/Contents/Home")
  export PATH="$JAVA_HOME/bin:$PATH:/opt/homebrew/bin:/usr/local/bin"
  cd "$SRCROOT/.."
  ./gradlew :shared:embedAndSignAppleFrameworkForXcode
  ```

---

### 2. Simulator Architectures (`ios_x64` vs `iosSimulatorArm64`)
* **The Pain**: Running on newer iOS Simulators fails with:
  `Xcode requested target architectures that are not configured in your Gradle build: ios_x64 / Unknown iOS simulator arch: x86_64`
* **Root Cause**: Newer Xcode versions default to building universal simulator slices (`arm64` + `x86_64`), while the KMP Gradle project only defines the `iosSimulatorArm64` target.
* **Template Solution**: Add to `project.pbxproj` build settings:
  ```
  "EXCLUDED_ARCHS[sdk=iphonesimulator*]" = "x86_64";
  ONLY_ACTIVE_ARCH = YES;
  ```

---

### 3. Swift Package Manager (SPM) Version Range Deadlocks
* **The Pain**: Xcode hangs indefinitely on `Updating GoogleAppMeasurement...`, `Updating abseil...`, or `firebase-ios-sdk (fetching...)`.
* **Root Cause**:
  * Local subpackages in `KotlinMultiplatformLinkedPackage/subpackages/` specified loose version bounds:
    * `kmpauth_firebase`: `"11.8.0"..."12.999.999"`
    * `kmpauth_google`: `from: "9.1.0"`
  * When Xcode resolves a workspace containing local packages with version ranges, it attempts to query and fetch all remote GitHub tags across 18 dependencies on every single open.
* **Template Solution**: Pin all local subpackage dependencies to exact versions matching `Package.resolved` (e.g. `exact: "12.14.0"`, `exact: "9.2.0"`).
* **SPM Spinning Recovery Playbook**:
  1. *Expose hidden CLI errors*:
     ```bash
     killall Xcode && pkill -f git-remote-http
     cd MobileApp/iosApp && xcodebuild -resolvePackageDependencies -project iosApp.xcodeproj -scheme iosApp
     ```
  2. *Check SSH rewrite hangs*: `git config --global --get-regexp "url.*insteadOf"` (unset if forcing SSH passphrase prompt).
  3. *3-Location Cache Wipe*:
     ```bash
     rm -rf ~/Library/Caches/org.swift.swiftpm ~/Library/org.swift.swiftpm ~/Library/Developer/Xcode/DerivedData/iosApp-*
     rm -rf MobileApp/iosApp/iosApp.xcodeproj/project.xcworkspace/xcshareddata/swiftpm/Package.resolved
     ```
  4. *Tune Git for heavy clones (disable HTTP/2 stalling)*:
     ```bash
     git config --global http.version HTTP/1.1
     git config --global http.postBuffer 524288000
     git config --global core.compression 0
     ```

---

### 4. Crashlytics Run Script Phase
* **The Pain**: Xcode displays `Run Script` errors and prevents the app from running during debug sessions.
* **Root Cause**:
  * The Crashlytics dSYM upload tool requires debug symbols that are never generated in `Debug` builds.
  * The build phase listed `${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}` as required input files; when missing, Xcode aborted the build.
* **Template Solution**: Either remove the script phase from the base starter kit or wrap it:
  ```sh
  if [ "$CONFIGURATION" = "Release" ]; then
      if [ -f "${BUILD_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run" ]; then
          "${BUILD_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run" || true
      fi
  fi
  ```

---

### 5. Camera & Media Permissions on iOS Simulator
* **The Pain**: Tapping "Take a Selfie" on the iOS Simulator crashed the app with no clear error message.
* **Root Cause**:
  * The iOS Simulator has no physical camera hardware; invoking native camera APIs directly triggers an unhandled platform exception.
  * `Info.plist` was missing `NSPhotoLibraryUsageDescription` and `NSPhotoLibraryAddUsageDescription`.
* **Template Solution**:
  * In `CelebrationScreen.kt`, wrap camera calls in `try-catch` with automatic fallback to the image/file picker.
  * Include clear permission strings in `Info.plist` out-of-the-box.
