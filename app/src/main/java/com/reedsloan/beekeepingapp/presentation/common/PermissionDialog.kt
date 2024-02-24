package com.reedsloan.beekeepingapp.presentation.common

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.reedsloan.beekeepingapp.R
import com.reedsloan.beekeepingapp.presentation.common.data.PermissionRequest

@Composable
fun PermissionDialog(
    permissionRequest: PermissionRequest,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onGoToAppSettingsClick: () -> Unit
) {
    AlertDialog(onDismissRequest = {
        onDismiss()
    }, title = {
        Text(text = stringResource(R.string.permission_required))
    }, text = {
        Text(
            text = if (isPermanentlyDeclined) {
                permissionRequest.isPermanentlyDeniedMessage
            } else {
                permissionRequest.message
            }
        )
    },
        icon = {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        },
        confirmButton = {
            Button(onClick = {
                if (isPermanentlyDeclined) {
                    onGoToAppSettingsClick()
                } else {
                    onConfirm()
                }
            }) {
                Text(
                    text = if (isPermanentlyDeclined) {
                        stringResource(R.string.go_to_app_settings)
                    } else {
                        stringResource(R.string.confirm)
                    }
                )
            }
        }, dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text(text = stringResource(R.string.cancel))
            }
        })
}