package com.reedsloan.beekeepingapp.presentation.common.data

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

sealed class PermissionRequest(
    val permission: String,
    val message: String,
    val isPermanentlyDeniedMessage: String = ""
) {
    data object CameraPermissionRequest : PermissionRequest(
        permission = "android.permission.CAMERA",
        message = "Camera permission is required to take photos of your hives.",
        isPermanentlyDeniedMessage = "Camera permission is required to take photos of your hives. Please enable it in your device settings."
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    data object StoragePermissionRequestAPI33 : PermissionRequest(
        permission = Manifest.permission.READ_MEDIA_IMAGES,
        message = "Storage permission is required to select photos of your hives.",
        isPermanentlyDeniedMessage = "Storage permission is required to select photos of your hives. Please enable it in your device settings."
    )

    data object NotificationPermissionRequest : PermissionRequest(
        permission = Manifest.permission.ACCESS_NOTIFICATION_POLICY,
        message = "Notification permission is required to receive notifications about your hives.",
        isPermanentlyDeniedMessage = "Notification permission is required to receive notifications about your hives. Please enable it in your device settings."
    )

}
