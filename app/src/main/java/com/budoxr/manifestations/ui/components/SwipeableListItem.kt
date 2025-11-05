package com.budoxr.manifestations.ui.components


import com.budoxr.manifestations.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun<T> SwipeableListItem(
    modifier: Modifier = Modifier,
    item: T,
    iconSize: Dp,
    alertColor: Color,
    actionAreaWidth: Dp = 140.dp,
    actionIcon: ImageVector,
    actionLabel: String,
    itemClickDisable: Boolean,
    actionIconTint: Color = Color.White,
    onActionClick: (T) -> Unit,
    onItemClick: (T) -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val horizontalMargin = dimensionResource(R.dimen.margin_horizontal)

    val factor = 0.95f
    val fallbackHeightDp = 36.dp
    var cardHeightDp by remember { mutableStateOf(fallbackHeightDp) }
    var contentSize by remember { mutableStateOf(IntSize.Zero) }
    var contentWidthSizePx by remember { mutableIntStateOf(0) }
    var anchorsInitialized by remember { mutableStateOf(false) }
    var isReadyToDisplay by remember { mutableStateOf(false) }

    val state = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Start,
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            decayAnimationSpec = decayAnimationSpec
        )
    }

    Box(
        modifier
            .fillMaxWidth()
            .onSizeChanged { layoutSize ->
                contentSize = layoutSize
                contentWidthSizePx = with(density) { (layoutSize.width.toDp() * factor).roundToPx() }

                val dragEndPoint = layoutSize.width - contentWidthSizePx
                val anchors = DraggableAnchors {
                    DragAnchors.entries.forEach { anchor ->
                        anchor at dragEndPoint * anchor.fraction
                    }
                }

                state.updateAnchors(anchors)
                anchorsInitialized = true
            }
    ) {
        AnimatedVisibility(
            visible = isReadyToDisplay && cardHeightDp > fallbackHeightDp,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .width(actionAreaWidth)
                        .height(cardHeightDp)
                        .background(alertColor),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(modifier = Modifier.padding(end = 5.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { onActionClick.invoke(item) } ) {
                                Icon(
                                    imageVector = actionIcon,
                                    tint = actionIconTint,
                                    modifier = Modifier.size(iconSize),
                                    contentDescription = actionLabel
                                )
                            }
                            Text(
                                text = actionLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = actionIconTint
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .width(with(density) { contentSize.width.toDp() })
                .wrapContentHeight()
                .offset {
                    IntOffset(x = state.requireOffset().roundToInt(), y = 0)
                }
                .anchoredDraggable(state, Orientation.Horizontal)
        ) {
            if (!itemClickDisable) {
                Card(
                    shape = RectangleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = modifier
                        .padding(end = horizontalMargin)
                        .onGloballyPositioned { coordinates ->
                            cardHeightDp = with(density) { coordinates.size.height.toDp() }
                        }
                        .width(with(density) { contentSize.width.toDp() * factor })
                        .wrapContentHeight()
                        .clickable {
                            onItemClick.invoke(item)
                        }
                ) {
                    content()
                }
            } else {
                Card(
                    shape = RectangleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = modifier
                        .padding(end = horizontalMargin)
                        .onGloballyPositioned { coordinates ->
                            cardHeightDp = with(density) { coordinates.size.height.toDp() }
                        }
                        .width(with(density) { contentSize.width.toDp() * factor })
                        .wrapContentHeight()
                ) {
                    content()
                }
            }

        }
    }

    LaunchedEffect(anchorsInitialized, cardHeightDp) {
        if (anchorsInitialized && cardHeightDp > fallbackHeightDp) {
            state.snapTo(DragAnchors.Start)
            isReadyToDisplay = true
        }
    }
}
