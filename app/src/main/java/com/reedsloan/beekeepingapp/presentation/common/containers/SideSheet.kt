package com.reedsloan.beekeepingapp.presentation.common.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SideSheetContainer(
    display: Boolean,
    modifier: Modifier = Modifier,
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier
        .zIndex(2F)
        .fillMaxSize()
    ) {
        AnimatedContent(
            targetState = display,
            transitionSpec = {
                // slide in from right on enter and slide out to right on exit
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(300)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(300)
                )

            }, label = ""
        ) { target ->
            val animatable by remember {
                mutableStateOf(
                    androidx.compose.animation.core.Animatable(
                        if (target) 1f else 0f
                    )
                )
            }

            val visible = animatable.value > 0.1f
            val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }
            if (visible) {
                LaunchedEffect(key1 = target) {
                    animatable.animateTo(if (target) 1f else 0f)
                }
                Column(
                    Modifier.fillMaxSize()
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                            )
                            .fillMaxHeight()
                            .width(300.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { }
                            .align(Alignment.End),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // title
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleLarge
                                )

                                // dismiss button
                                IconButton(onClick = { onDismiss() }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Close"
                                    )
                                }
                            }
                            content()
                        }
                    }
                }
            }
        }
    }
}