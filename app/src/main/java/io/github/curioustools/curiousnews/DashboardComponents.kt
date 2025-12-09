package io.github.curioustools.curiousnews

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffset
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.apply
import kotlin.collections.forEachIndexed
import kotlin.collections.mapIndexed
import kotlin.let
import kotlin.run

// todo : center filled navbar https://stackoverflow.com/questions/77991426/compose-bottom-nav-bar-with-custom-shape-and-transparent-around-it

// todo tata aig navbar with jumping background

// todo glass ui navbar

// todo navbar auto hide

@Composable
fun CircleBottomBar(
    navBarItems: List<NavBarItem>,
    currentSelectedPos: Int,
    modifier: Modifier = Modifier,
    onSelected: (Int) -> Unit,
) {
    //val navBarItems = NavBarItem.entries
    val barColor = MaterialTheme.colorScheme.onTertiary
    val selectedColor = AppColors.orange_bright_ff8
    val unselectedColor = MaterialTheme.colorScheme.onBackground
    val context = LocalContext.current

    var selectedItem by rememberSaveable { mutableIntStateOf(currentSelectedPos) }
    var barSize by remember { mutableStateOf(IntSize(1080, 0)) }
    val offsetStep = remember(barSize) { barSize.width.toFloat() / (navBarItems.size * 2) }
    val offset = remember(selectedItem, offsetStep) {
        offsetStep + selectedItem * 2 * offsetStep
    }
    val circleRadiusPx = LocalDensity.current.run { selectedItemCircleRadius.toPx().toInt() }
    val offsetTransition = updateTransition(offset, "offset transition")
    val animation = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    val cutoutOffset by offsetTransition.animateFloat(
        transitionSpec = { if (this.initialState == 0f) snap() else animation },
        label = "cutout offset"
    ) { it }
    val circleOffset by offsetTransition.animateIntOffset(
        transitionSpec = {
            if (this.initialState == 0f) {
                snap()
            } else {
                spring(animation.dampingRatio, animation.stiffness)
            }
        },
        label = "circle offset"
    ) {
        IntOffset(it.toInt() - circleRadiusPx, -circleRadiusPx)
    }
    val barShape = remember(cutoutOffset) {
        BarShape(
            offset = cutoutOffset,
            circleRadius = selectedItemCircleRadius,
            cornerRadius = 25.dp,
        )
    }

    Box(modifier = modifier) {
        SelectedCircle(
            modifier = Modifier.Companion
                .offset { circleOffset }
                .zIndex(1f),
            navBarItem = navBarItems[selectedItem],
            tint = selectedColor,
            circleColor = barColor
        )
        Row(
            modifier = Modifier.Companion
                .onPlaced { barSize = it.size }
                .graphicsLayer { shape = barShape; clip = true }
                .fillMaxWidth()
                .background(barColor),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            navBarItems.forEachIndexed { index, navBarItem ->
                val isSelected = index == selectedItem
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        selectedItem = index
                        onSelected.invoke(index)
                    },
                    icon = {
                        val iconAlpha by animateFloatAsState(
                            targetValue = if (isSelected) 0f else 1f,
                            label = "Navbar item icon"
                        )
                        Icon(
                            imageVector = navBarItem.unselectedVector,
                            contentDescription = navBarItem.getTitle(context),
                            modifier = Modifier.Companion
                                .size(24.dp)
                                .alpha(iconAlpha)
                        )

                    },
                    label = { Text(
                        text =     navBarItem.getTitle(context),
                        color = if (isSelected) selectedColor else unselectedColor,
                    ) },
                    colors = NavigationBarItemDefaults.colors().copy(
                        selectedIconColor = selectedColor,
                        selectedTextColor = selectedColor,
                        unselectedIconColor = unselectedColor,
                        unselectedTextColor = unselectedColor,
                        selectedIndicatorColor = Color.Companion.Transparent,
                    )
                )
            }
        }
    }
}


@Composable
private fun SelectedCircle(
    modifier: Modifier = Modifier,
    navBarItem: NavBarItem,
    tint:Color,
    circleColor: Color
) {
    val context = LocalContext.current
    val isDark = isAppInDarkTheme()
    val iconTint = if(isDark) Color.White else tint
    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .size(selectedItemCircleRadius * 2)
            .clip(CircleShape)
            .background(circleColor)
            .border(1.dp, AppColors.orange_m200, CircleShape)
        ,
    ) {
        AnimatedContent(targetState = navBarItem.selectedVector) { targetIcon ->
            Icon(
                imageVector = targetIcon,
                contentDescription = navBarItem.getTitle(context),
                modifier = Modifier.size(36.dp),
                tint = iconTint
            )
        }
    }
}


private class BarShape(
    private val offset: Float,
    private val circleRadius: Dp,
    private val cornerRadius: Dp,
    private val circleGap: Dp = 5.dp,
) : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return getPath(size, density).let { Outline.Generic(it)  }
    }

    private fun getPath(size: Size, density: Density): Path {
        val cutoutCenterX = offset
        val cutoutRadius = density.run { (circleRadius + circleGap).toPx() }
        val cornerRadiusPx = density.run { cornerRadius.toPx() }
        val cornerDiameter = cornerRadiusPx * 2
        return Path().apply {
            val cutoutEdgeOffset = cutoutRadius * 1.5f
            val cutoutLeftX = cutoutCenterX - cutoutEdgeOffset
            val cutoutRightX = cutoutCenterX + cutoutEdgeOffset
            moveTo(x = 0F, y = size.height)
            if (cutoutLeftX > 0) {
                val realLeftCornerDiameter = if (cutoutLeftX >= cornerRadiusPx) {
                    // there is a space between rounded corner and cutout
                    cornerDiameter
                } else {
                    // rounded corner and cutout overlap
                    cutoutLeftX * 2
                }
                arcTo(
                    rect = Rect(
                        left = 0f,
                        top = 0f,
                        right = realLeftCornerDiameter,
                        bottom = realLeftCornerDiameter
                    ),
                    startAngleDegrees = 180.0f,
                    sweepAngleDegrees = 90.0f,
                    forceMoveTo = false
                )
            }
            lineTo(cutoutLeftX, 0f)
            cubicTo(
                x1 = cutoutCenterX - cutoutRadius,
                y1 = 0f,
                x2 = cutoutCenterX - cutoutRadius,
                y2 = cutoutRadius,
                x3 = cutoutCenterX,
                y3 = cutoutRadius,
            )
            cubicTo(
                x1 = cutoutCenterX + cutoutRadius,
                y1 = cutoutRadius,
                x2 = cutoutCenterX + cutoutRadius,
                y2 = 0f,
                x3 = cutoutRightX,
                y3 = 0f,
            )
            if (cutoutRightX < size.width) {
                val realRightCornerDiameter = if (cutoutRightX <= size.width - cornerRadiusPx) {
                    cornerDiameter
                } else {
                    (size.width - cutoutRightX) * 2
                }
                arcTo(
                    rect = Rect(
                        left = size.width - realRightCornerDiameter,
                        top = 0f,
                        right = size.width,
                        bottom = realRightCornerDiameter
                    ),
                    startAngleDegrees = -90.0f,
                    sweepAngleDegrees = 90.0f,
                    forceMoveTo = false
                )
            }
            lineTo(x = size.width, y = size.height)
            close()
        }
    }
}

val selectedItemCircleRadius = 32.dp


@Composable
fun LoginLogoutSection(
    modifier: Modifier = Modifier.Companion,
    isLogged: Boolean,
    profileEmail: String,
    onClick: (DashboardIntent) -> Unit
) {
    val themeColors = MaterialTheme.colorScheme
    val text = if (isLogged) stringResource(
        R.string.msg_logged_in,
        profileEmail
    ) else stringResource(R.string.msg_logged_out)
    val btn = if (isLogged) stringResource(R.string.string_my_profile) else stringResource(R.string.word_login)
    val cardColor = if(isLogged) themeColors.onTertiary else themeColors.surface
    val cardBorder = if(isLogged) themeColors.tertiary else themeColors.tertiary
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12),
        colors = CardDefaults.cardColors(cardColor),
        border = BorderStroke(1.dp, cardBorder),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            Modifier.Companion
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Companion.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier.Companion.weight(1f),
                minLines = 3,
                color = themeColors.onSurface,
                style = textStylesSystem().bodyMedium.copy(fontWeight = FontWeight.Companion.SemiBold)
            )
            if (!isLogged){
                AppPrimaryButton(
                    text = btn,
                    onClick = { onClick.invoke(DashboardIntent.AuthTouchPointClicked) },
                    textModifier = Modifier.padding(horizontal = 12.dp,vertical = 2.dp)
                )
            }
            else{
                AppSecondaryButton(
                    text = btn,
                    onClick = { onClick.invoke(DashboardIntent.AuthTouchPointClicked) },
                    textModifier = Modifier.padding(horizontal = 12.dp,vertical = 2.dp)
                )
            }

        }
    }
}