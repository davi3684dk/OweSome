package com.owesome.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun SliderButton(
    text: String,
    size: Float,
    loading: Boolean,
    onComplete: () -> Unit
) {
    val density = LocalDensity.current

    var sliderPositionPx by remember { mutableFloatStateOf(0f) }
    var sliderSize by remember { mutableFloatStateOf(0f) }

    val offset by animateIntOffsetAsState(
        targetValue = IntOffset(sliderPositionPx.toInt(), 0),
        label = "offset"
    )

    val textAlpha by animateFloatAsState(
        targetValue = 1f - sliderPositionPx / sliderSize,
        label = "alpha"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    shape = MaterialTheme.shapes.large
                )
                .padding(10.dp)
                .onSizeChanged {
                    sliderSize = (it.width / density.density) - size
                },
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(size.dp, size.dp)
                        .align(Alignment.Center)
                )
            } else {
                Text(
                    text = text,
                    modifier = Modifier
                        .alpha(textAlpha)
                        .align(Alignment.Center)
                )
                Row(
                    modifier = Modifier
                        .offset(x = offset.x.dp)
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                val newPosition = sliderPositionPx + delta / density.density

                                sliderPositionPx = newPosition.coerceIn(0f, sliderSize)
                            },
                            onDragStopped = {
                                if (sliderPositionPx > 0.6f * sliderSize) {
                                    onComplete()
                                }

                                sliderPositionPx = 0f
                            }
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .size(size.dp, size.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                    ) {
                        Row(
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForwardIos,
                                "Arrow",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}