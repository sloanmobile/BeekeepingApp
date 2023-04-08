package com.reedsloan.beekeepingapp.presentation.common.data

import android.Manifest

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

    object StoragePermissionRequest : PermissionRequest(
        permission = "android.permission.READ_EXTERNAL_STORAGE",
        message = "Storage permission is required to select photos of your hives.",
        isPermanentlyDeniedMessage = "Storage permission is required to select photos of your hives. Please enable it in your device settings."
    )
}
