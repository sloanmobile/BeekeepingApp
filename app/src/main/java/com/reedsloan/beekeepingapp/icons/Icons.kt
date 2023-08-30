package com.reedsloan.beekeepingapp.icons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun rememberHive(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "hive",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(27.833f, 19.375f)
                quadToRelative(-0.375f, 0f, -0.75f, -0.229f)
                reflectiveQuadToRelative(-0.541f, -0.521f)
                lineToRelative(-2.125f, -3.5f)
                quadToRelative(-0.167f, -0.292f, -0.167f, -0.729f)
                quadToRelative(0f, -0.438f, 0.167f, -0.771f)
                lineToRelative(2.125f, -3.5f)
                quadToRelative(0.166f, -0.292f, 0.541f, -0.5f)
                quadToRelative(0.375f, -0.208f, 0.75f, -0.208f)
                horizontalLineToRelative(3.75f)
                quadToRelative(0.334f, 0f, 0.709f, 0.208f)
                quadToRelative(0.375f, 0.208f, 0.583f, 0.5f)
                lineToRelative(2.042f, 3.5f)
                quadToRelative(0.208f, 0.333f, 0.208f, 0.771f)
                quadToRelative(0f, 0.437f, -0.208f, 0.729f)
                lineToRelative(-2.042f, 3.5f)
                quadToRelative(-0.208f, 0.292f, -0.583f, 0.521f)
                quadToRelative(-0.375f, 0.229f, -0.709f, 0.229f)
                close()
                moveTo(18.208f, 25f)
                quadToRelative(-0.375f, 0f, -0.729f, -0.208f)
                quadToRelative(-0.354f, -0.209f, -0.562f, -0.542f)
                lineToRelative(-2.042f, -3.542f)
                quadToRelative(-0.208f, -0.291f, -0.208f, -0.708f)
                reflectiveQuadToRelative(0.208f, -0.75f)
                lineToRelative(2.042f, -3.542f)
                quadToRelative(0.208f, -0.291f, 0.562f, -0.5f)
                quadToRelative(0.354f, -0.208f, 0.729f, -0.208f)
                horizontalLineToRelative(3.667f)
                quadToRelative(0.375f, 0f, 0.729f, 0.208f)
                quadToRelative(0.354f, 0.209f, 0.563f, 0.5f)
                lineToRelative(2.083f, 3.542f)
                quadToRelative(0.167f, 0.333f, 0.167f, 0.75f)
                reflectiveQuadToRelative(-0.167f, 0.708f)
                lineToRelative(-2.083f, 3.542f)
                quadToRelative(-0.209f, 0.333f, -0.563f, 0.542f)
                quadToRelative(-0.354f, 0.208f, -0.729f, 0.208f)
                close()
                moveToRelative(0f, -11.25f)
                quadToRelative(-0.375f, 0f, -0.729f, -0.208f)
                quadToRelative(-0.354f, -0.209f, -0.562f, -0.5f)
                lineTo(14.833f, 9.5f)
                quadToRelative(-0.166f, -0.292f, -0.166f, -0.729f)
                quadToRelative(0f, -0.438f, 0.166f, -0.729f)
                lineToRelative(2.084f, -3.5f)
                quadToRelative(0.208f, -0.292f, 0.562f, -0.521f)
                quadToRelative(0.354f, -0.229f, 0.729f, -0.229f)
                horizontalLineToRelative(3.667f)
                quadToRelative(0.375f, 0f, 0.729f, 0.229f)
                reflectiveQuadToRelative(0.563f, 0.521f)
                lineToRelative(2.083f, 3.5f)
                quadToRelative(0.167f, 0.291f, 0.167f, 0.729f)
                quadToRelative(0f, 0.437f, -0.167f, 0.729f)
                lineToRelative(-2.083f, 3.542f)
                quadToRelative(-0.209f, 0.291f, -0.563f, 0.5f)
                quadToRelative(-0.354f, 0.208f, -0.729f, 0.208f)
                close()
                moveTo(8.5f, 19.375f)
                quadToRelative(-0.375f, 0f, -0.729f, -0.229f)
                reflectiveQuadToRelative(-0.563f, -0.521f)
                lineToRelative(-2.041f, -3.5f)
                quadToRelative(-0.209f, -0.333f, -0.209f, -0.75f)
                reflectiveQuadToRelative(0.209f, -0.75f)
                lineToRelative(2.041f, -3.5f)
                quadToRelative(0.209f, -0.292f, 0.563f, -0.5f)
                quadToRelative(0.354f, -0.208f, 0.729f, -0.208f)
                horizontalLineToRelative(3.75f)
                quadToRelative(0.375f, 0f, 0.729f, 0.208f)
                reflectiveQuadToRelative(0.563f, 0.5f)
                lineToRelative(2.083f, 3.5f)
                quadToRelative(0.167f, 0.333f, 0.167f, 0.771f)
                quadToRelative(0f, 0.437f, -0.167f, 0.729f)
                lineToRelative(-2.083f, 3.5f)
                quadToRelative(-0.209f, 0.292f, -0.563f, 0.521f)
                quadToRelative(-0.354f, 0.229f, -0.729f, 0.229f)
                close()
                moveToRelative(0f, 11.208f)
                quadToRelative(-0.375f, 0f, -0.729f, -0.208f)
                reflectiveQuadToRelative(-0.563f, -0.542f)
                lineToRelative(-2.041f, -3.5f)
                quadToRelative(-0.209f, -0.291f, -0.209f, -0.729f)
                quadToRelative(0f, -0.437f, 0.209f, -0.729f)
                lineToRelative(2.041f, -3.5f)
                quadToRelative(0.209f, -0.292f, 0.563f, -0.521f)
                quadToRelative(0.354f, -0.229f, 0.729f, -0.229f)
                horizontalLineToRelative(3.75f)
                quadToRelative(0.375f, 0f, 0.729f, 0.229f)
                reflectiveQuadToRelative(0.563f, 0.521f)
                lineToRelative(2.083f, 3.5f)
                quadToRelative(0.167f, 0.292f, 0.167f, 0.729f)
                quadToRelative(0f, 0.438f, -0.167f, 0.729f)
                lineToRelative(-2.083f, 3.5f)
                quadToRelative(-0.209f, 0.334f, -0.563f, 0.542f)
                quadToRelative(-0.354f, 0.208f, -0.729f, 0.208f)
                close()
                moveToRelative(9.708f, 5.625f)
                quadToRelative(-0.375f, 0f, -0.729f, -0.229f)
                reflectiveQuadToRelative(-0.562f, -0.521f)
                lineToRelative(-2.084f, -3.5f)
                quadToRelative(-0.166f, -0.291f, -0.166f, -0.729f)
                quadToRelative(0f, -0.437f, 0.166f, -0.729f)
                lineToRelative(2.084f, -3.542f)
                quadToRelative(0.208f, -0.291f, 0.562f, -0.52f)
                quadToRelative(0.354f, -0.23f, 0.729f, -0.23f)
                horizontalLineToRelative(3.667f)
                quadToRelative(0.375f, 0f, 0.729f, 0.23f)
                quadToRelative(0.354f, 0.229f, 0.563f, 0.52f)
                lineTo(25.25f, 30.5f)
                quadToRelative(0.167f, 0.292f, 0.167f, 0.729f)
                quadToRelative(0f, 0.438f, -0.167f, 0.729f)
                lineToRelative(-2.083f, 3.5f)
                quadToRelative(-0.209f, 0.292f, -0.563f, 0.521f)
                quadToRelative(-0.354f, 0.229f, -0.729f, 0.229f)
                close()
                moveToRelative(9.625f, -5.625f)
                quadToRelative(-0.375f, 0f, -0.75f, -0.208f)
                reflectiveQuadToRelative(-0.541f, -0.542f)
                lineToRelative(-2.125f, -3.5f)
                quadToRelative(-0.167f, -0.291f, -0.167f, -0.729f)
                quadToRelative(0f, -0.437f, 0.167f, -0.729f)
                lineToRelative(2.125f, -3.542f)
                quadToRelative(0.166f, -0.25f, 0.541f, -0.479f)
                quadToRelative(0.375f, -0.229f, 0.75f, -0.229f)
                horizontalLineToRelative(3.75f)
                quadToRelative(0.334f, 0f, 0.709f, 0.229f)
                quadToRelative(0.375f, 0.229f, 0.583f, 0.521f)
                lineToRelative(2.042f, 3.5f)
                quadToRelative(0.208f, 0.292f, 0.208f, 0.729f)
                quadToRelative(0f, 0.438f, -0.208f, 0.729f)
                lineToRelative(-2.042f, 3.5f)
                quadToRelative(-0.208f, 0.334f, -0.583f, 0.542f)
                quadToRelative(-0.375f, 0.208f, -0.709f, 0.208f)
                close()
            }
        }.build()
    }
}

@Composable
fun rememberTaskAlt(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "task_alt",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(35f, 15.5f)
                quadToRelative(0.333f, 1.083f, 0.479f, 2.208f)
                quadToRelative(0.146f, 1.125f, 0.146f, 2.292f)
                quadToRelative(0f, 3.292f, -1.208f, 6.125f)
                quadToRelative(-1.209f, 2.833f, -3.334f, 4.958f)
                reflectiveQuadToRelative(-4.958f, 3.334f)
                quadTo(23.292f, 35.625f, 20f, 35.625f)
                reflectiveQuadToRelative(-6.125f, -1.208f)
                quadToRelative(-2.833f, -1.209f, -4.958f, -3.334f)
                reflectiveQuadToRelative(-3.334f, -4.958f)
                quadTo(4.375f, 23.292f, 4.375f, 20f)
                reflectiveQuadToRelative(1.208f, -6.125f)
                quadToRelative(1.209f, -2.833f, 3.334f, -4.958f)
                reflectiveQuadToRelative(4.958f, -3.334f)
                quadTo(16.708f, 4.375f, 20f, 4.375f)
                quadToRelative(2.5f, 0f, 4.75f, 0.729f)
                reflectiveQuadToRelative(4.167f, 1.979f)
                quadToRelative(0.333f, 0.209f, 0.395f, 0.625f)
                quadToRelative(0.063f, 0.417f, -0.187f, 0.75f)
                quadToRelative(-0.292f, 0.334f, -0.708f, 0.396f)
                quadToRelative(-0.417f, 0.063f, -0.75f, -0.187f)
                quadToRelative(-1.625f, -1.084f, -3.584f, -1.688f)
                quadToRelative(-1.958f, -0.604f, -4.083f, -0.604f)
                quadToRelative(-5.75f, 0f, -9.688f, 3.937f)
                quadTo(6.375f, 14.25f, 6.375f, 20f)
                reflectiveQuadToRelative(3.937f, 9.688f)
                quadTo(14.25f, 33.625f, 20f, 33.625f)
                reflectiveQuadToRelative(9.688f, -3.937f)
                quadTo(33.625f, 25.75f, 33.625f, 20f)
                quadToRelative(0f, -1f, -0.125f, -1.938f)
                quadToRelative(-0.125f, -0.937f, -0.375f, -1.854f)
                quadToRelative(-0.083f, -0.333f, 0.063f, -0.708f)
                quadToRelative(0.145f, -0.375f, 0.479f, -0.542f)
                quadToRelative(0.375f, -0.208f, 0.791f, -0.062f)
                quadToRelative(0.417f, 0.146f, 0.542f, 0.604f)
                close()
                moveTo(16.708f, 26.083f)
                lineTo(12f, 21.333f)
                quadToRelative(-0.292f, -0.291f, -0.292f, -0.729f)
                quadToRelative(0f, -0.437f, 0.334f, -0.729f)
                quadToRelative(0.291f, -0.292f, 0.708f, -0.292f)
                reflectiveQuadToRelative(0.792f, 0.292f)
                lineTo(17.583f, 24f)
                lineTo(33.458f, 8.125f)
                quadToRelative(0.292f, -0.292f, 0.709f, -0.292f)
                quadToRelative(0.416f, 0f, 0.75f, 0.292f)
                quadToRelative(0.333f, 0.333f, 0.333f, 0.771f)
                quadToRelative(0f, 0.437f, -0.333f, 0.729f)
                lineTo(18.458f, 26.083f)
                quadToRelative(-0.333f, 0.375f, -0.854f, 0.375f)
                quadToRelative(-0.521f, 0f, -0.896f, -0.375f)
                close()
            }
        }.build()
    }
}