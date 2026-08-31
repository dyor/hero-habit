package com.dyor.habithero.util.file

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitCameraFacing
import io.github.vinceglb.filekit.dialogs.openCameraPicker

actual suspend fun FileKit.openCameraPicker(): PlatformFile? = FileKit.openCameraPicker(cameraFacing = FileKitCameraFacing.Front)

actual fun isCameraCaptureSupported(): Boolean = true
