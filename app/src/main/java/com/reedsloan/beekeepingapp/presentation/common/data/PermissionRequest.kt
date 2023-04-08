package com.reedsloan.beekeepingapp.presentation.common.data

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

sealed class PermissionRequest(
    val permission: String,
    val message: String,
    val isPermanentlyDeniedMessage: String = ""
) {
    object CameraPermissionRequest : PermissionRequest(
        permission = "android.permission.CAMERA",
        message = "Camera permission is required to take photos of your hives.",
        isPermanentlyDeniedMessage = "Camera permission is required to take photos of your hives. Please enable it in your device settings."
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    object StoragePermissionRequestAPI33 : PermissionRequest(
        permission = Manifest.permission.READ_MEDIA_IMAGES,
        message = "Storage permission is required to select photos of your hives.",
        isPermanentlyDeniedMessage = "Storage permission is required to select photos of your hives. Please enable it in your device settings."
    )
}
