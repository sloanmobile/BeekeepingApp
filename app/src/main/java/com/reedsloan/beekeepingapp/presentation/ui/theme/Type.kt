package com.reedsloan.beekeepingapp.presentation.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.reedsloan.beekeepingapp.R

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily(Font(R.font.satoshi_medium)),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = FontFamily(Font(R.font.satoshi_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily(Font(R.font.satoshi_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),
    h1 = TextStyle(
        fontFamily = FontFamily(Font(R.font.satoshi_bold)),
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp
    ),
    h2 = TextStyle(
        fontFamily = FontFamily(Font(R.font.satoshi_bold)),
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    h3 = TextStyle(
        fontFamily = FontFamily(Font(R.font.satoshi_medium)),
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    h4 = TextStyle(
        fontFamily = FontFamily(Font(R.font.satoshi_medium)),
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)