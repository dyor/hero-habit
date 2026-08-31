package com.dyor.habithero.util.file

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitCameraFacing
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openCameraPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType

actual suspend fun FileKit.openCameraPicker(): PlatformFile? {
    if (!UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
        // Camera unavailable (e.g. iOS Simulator) — fall back to photo library picker safely without crashing
        return FileKit.openFilePicker(type = FileKitType.Image)
    }
    return try {
        FileKit.openCameraPicker(cameraFacing = FileKitCameraFacing.Front)
    } catch (e: Throwable) {
        FileKit.openFilePicker(type = FileKitType.Image)
    }
}

actual fun isCameraCaptureSupported(): Boolean = UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)
