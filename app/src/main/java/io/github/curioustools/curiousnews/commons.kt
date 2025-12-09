package io.github.curioustools.curiousnews

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.annotation.Keep
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.placeholder
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import io.github.curioustools.curiousnews.AppButtonConfig.AppButtonType.ROUND_SECONDARY
import io.github.curioustools.curiousnews.AppButtonConfig.InternalIconConfig
import io.github.curioustools.curiousnews.AppButtonConfig.InternalTextConfig
import io.github.curioustools.curiousnews.AppButtonConfig.AppButtonType.LINK
import io.github.curioustools.curiousnews.AppButtonConfig.AppButtonType.ROUND_PRIMARY

@Composable
fun AnimatedSnackBarHost(
    message: String,
    onDismiss :()-> Unit
) {
    val isVisible = message.isNotBlank()
    val dimens = MaterialTheme.localDimens

    Box(modifier = Modifier.Companion.fillMaxSize()) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(start = dimens.dp16, end = dimens.dp16, bottom = dimens.dp24)
                .align(Alignment.Companion.BottomCenter)
        ) {
            CustomSnackBar(message)
        }
    }

    LaunchedEffect(message) {
        if (isVisible) {
            delay(3000)
            onDismiss.invoke()
        }
    }
}

@Preview
@Composable
fun CustomSnackBar(
    message: String = "hello",
    boxModifier: Modifier = Modifier.Companion
        .fillMaxWidth()
        .padding(MaterialTheme.localDimens.dp16)
        .background(
            color = AppColors.orange_bright_ff8,
            shape = RoundedCornerShape(MaterialTheme.localDimens.dp8)
        )
        .border(
            width = MaterialTheme.localDimens.dp1,
            color = AppColors.orange_m200,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(MaterialTheme.localDimens.dp8)
        )
        .padding(
            horizontal = MaterialTheme.localDimens.dp12,
            vertical = MaterialTheme.localDimens.dp8
        )
) {
    Box(
        modifier = boxModifier
    ) {
        Row(
            verticalAlignment = Alignment.Companion.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.Companion.padding(8.dp)
        ) {
            Image(
                painter = rememberVectorPainter(Icons.Default.CheckCircle),
                contentDescription = "Success",
                modifier = Modifier.Companion.size(MaterialTheme.localDimens.dp16),
                colorFilter = ColorFilter.Companion.tint(AppColors.white)
            )
            Spacer(modifier = Modifier.Companion.width(MaterialTheme.localDimens.dp8))
            Text(
                text = message,
                color = AppColors.white,
                style = MaterialTheme.localTypographyClass.labelRegular
            )
        }
    }
}



@Keep
@Immutable
@Serializable
sealed interface AppCommonUiActions {
    data class ShowBottomSheet(val type: AppCommonBottomSheetType) : AppCommonUiActions
    data class ShowLoader(val type: String ="") : AppCommonUiActions
    data class ShowToast(val resId: Int, val duration: Int = Toast.LENGTH_SHORT) : AppCommonUiActions
    data class ShowSnackBar(val message: String, val actionLabel: String? = null, val duration: SnackbarDuration = SnackbarDuration.Short) : AppCommonUiActions

    data object DoNothing : AppCommonUiActions
    data class LaunchComposableScreen(val route: AppRoutes): AppCommonUiActions
    data class LaunchUsingContext(val callback: (context: Context) -> Unit) : AppCommonUiActions
    data class LaunchUsingActivity(val callback: (context: Activity?) -> Unit) : AppCommonUiActions
    data class LaunchUsingController(val callback: (NavBackStack<NavKey>)-> Unit) : AppCommonUiActions
}

@Keep
@Immutable
@Serializable
sealed interface AppCommonBottomSheetType {
    data class ErrorSheet(val errorInfo: BaseStatus, val title: String = "Something went wrong", val subtitle: String = errorInfo.msg): AppCommonBottomSheetType{
        companion object{
            fun sww(title: String = "Something Went Wrong"): ErrorSheet{
                return ErrorSheet(BaseStatus.UNRECOGNISED,title = title)
            }
        }
    }
    data object ClearCacheBottomSheet: AppCommonBottomSheetType
    data class SelectThemeBottomSheet(val existingSelection: SharedPrefs.ThemeMode): AppCommonBottomSheetType

}

@Keep
@Immutable
@Serializable
sealed interface AppCommonBottomSheetIntents{
    data object OnCacheClearSelection: AppCommonBottomSheetIntents
    data class OnThemeSelected(val theme: SharedPrefs.ThemeMode) : AppCommonBottomSheetIntents
}







@Stable @Serializable @Immutable @Keep
data class AppButtonConfig(
    val text: InternalTextConfig?,
    val shape: Shape,
    val iconTint: Boolean = true,
    val outerSurfaceModifier: Modifier,
    val innerRowModifier: Modifier,
    val leftIcon: InternalIconConfig? = null,
    val rightIcon: InternalIconConfig? = null,
    val innerRowArrangement: Arrangement.Horizontal,
    val textTakesFullWidth: Boolean,
    val type: AppButtonType,
    val isEnabled: Boolean
){
    @Stable @Immutable @Serializable @Keep
    sealed interface InternalTextConfig{
        data class Text(val text: String, val modifier: Modifier, val style: TextStyle,val align: TextAlign): InternalTextConfig
        data class Annotated(val text: AnnotatedString, val modifier: Modifier, val style: TextStyle, val align: TextAlign): InternalTextConfig
    }

    @Stable @Serializable @Immutable @Keep
    sealed interface InternalIconConfig{
        val bgModifier: Modifier
        val iconModifier: Modifier
        data class Url(val url: String, val placeholder: Int? = null, val error: Int? = null,override val iconModifier: Modifier, override val bgModifier: Modifier): InternalIconConfig
        data class Drawable(val drawable: Int,override val iconModifier: Modifier, override val bgModifier: Modifier,): InternalIconConfig
        data class Vector(val imageVector: ImageVector,override val iconModifier: Modifier,override val bgModifier: Modifier,): InternalIconConfig
        data class Text(val text: String,override val iconModifier: Modifier,val style: TextStyle,override val bgModifier: Modifier,): InternalIconConfig

        companion object{
            fun vector(vector: ImageVector, transparent: Boolean=false,bg: Modifier = Modifier): InternalIconConfig.Vector{
                return InternalIconConfig.Vector(
                    imageVector = vector,
                    iconModifier = Modifier
                        .alpha(if (transparent) 0f else 1f)
                        .size(16.dp),
                    bgModifier = Modifier,
                )
            }

            fun vector2(vector: ImageVector, iconSize: Dp = 80.dp, bgColor:Color = AppColors.orange_bright_ff8.copy(alpha = 0.25f)): InternalIconConfig.Vector{
                return InternalIconConfig.Vector(
                    imageVector = vector,
                    iconModifier = Modifier.padding(8.dp),
                    bgModifier = Modifier
                        .size(iconSize)
                        .background(bgColor, CircleShape),
                )
            }

            fun res(res: Int, transparent: Boolean=false,bg: Modifier = Modifier): InternalIconConfig{
                return InternalIconConfig.Drawable(
                    drawable = res,
                    iconModifier = Modifier
                        .alpha(if (transparent) 0f else 1f)
                        .size(16.dp),
                    bgModifier = Modifier,
                )
            }
            @Composable
            fun res2(res: Int, iconSize: Dp = 80.dp, bgColor:Color = AppColors.orange_bright_ff8.copy(alpha = 0.25f)): InternalIconConfig.Drawable{
                return InternalIconConfig.Drawable(
                    drawable = res,
                    iconModifier = Modifier.padding(8.dp),
                    bgModifier = Modifier
                        .size(iconSize)
                        .background(bgColor, CircleShape),
                )
            }

            fun text(str: String, transparent: Boolean=false,bg: Modifier = Modifier): InternalIconConfig{
                return InternalIconConfig.Text(
                    text = str,
                    iconModifier = Modifier
                        .alpha(if (transparent) 0f else 1f)
                        .size(16.dp),
                    style = LocalTypographyClass().labelSmall,
                    bgModifier = Modifier,
                )
            }

        }
    }

    @Stable @Serializable @Immutable @Keep
    enum class AppButtonType{ ROUND_PRIMARY,ROUND_SECONDARY,LINK}
}

@Composable
fun InternalIcon(
    config: InternalIconConfig,
    modifier: Modifier = Modifier,
    iconTint: Color = colors().onTertiary,
    applyTint: Boolean= false,
) {
    Box(
        modifier = modifier.then(config.bgModifier),
        contentAlignment = Alignment.Center,
    ) {
        when (config) {
            is InternalIconConfig.Drawable -> {
                Image(
                    painter = painterResource(id = config.drawable),
                    contentDescription = null,
                    modifier = config.iconModifier,
                    colorFilter = if (applyTint) ColorFilter.tint(iconTint) else null
                )
            }

            is InternalIconConfig.Vector -> {
                Image(
                    imageVector = config.imageVector,
                    contentDescription = null,
                    modifier = config.iconModifier,
                    colorFilter = if (applyTint) ColorFilter.tint(iconTint) else null

                )
            }

            is InternalIconConfig.Text -> {
                Text(
                    text = config.text,
                    style = config.style,
                    modifier = config.iconModifier.wrapContentSize(),
                    textAlign = TextAlign.Center,
                    color = iconTint
                )
            }

            is InternalIconConfig.Url -> {
                val context = LocalContext.current
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(config.url)
                        .apply {
                            config.placeholder?.let { placeholder(it) }
                            config.error?.let { error(it) }
                        }
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = config.iconModifier,
                    colorFilter = if (applyTint) ColorFilter.tint(iconTint) else null
                )
            }
        }
    }
}

@Composable
fun RawAppRoundButton(config: AppButtonConfig, onclick:(AppButtonConfig)-> Unit){
    val allColors = MaterialTheme.colorScheme
    var colorBg = AppColors.orange_bright_ff8 //if (config.isPrimary) AppColors.orange_bright_ff8 else AppColors.orange_v_light
    var colorFg =  AppColors.white //if (config.isPrimary) AppColors.white else AppColors.orangee65
    var colorBorder = AppColors.transparent // if (config.isPrimary) AppColors.transparent else AppColors.orange_m200
    when(config.type){
        ROUND_PRIMARY -> {colorBg = AppColors.orange_bright_ff8;colorFg = AppColors.white;colorBorder = AppColors.transparent }
        ROUND_SECONDARY -> {colorBg = AppColors.orange_v_light;colorFg = AppColors.orangee65;colorBorder = AppColors.orange_m200 }
        LINK -> {colorBg = AppColors.transparent;colorFg = AppColors.orangee65;colorBorder = AppColors.transparent }
    }
    val interactionSource = remember { MutableInteractionSource() }


    @Composable
    fun InternalText(config: InternalTextConfig, e: Modifier){

        config.apply {
            when(this){
                is InternalTextConfig.Annotated -> Text(text = text, modifier = e.then(modifier), style = style, textAlign = align )
                is InternalTextConfig.Text -> Text(text = text, modifier = e.then(modifier), style = style, textAlign = align)
            }
        }

    }




    Surface(
        onClick = { onclick.invoke(config) },
        modifier = config.outerSurfaceModifier.semantics { role = Role.Button },
        enabled = config.isEnabled,
        shape = config.shape,
        color =if(colorBg== AppColors.transparent) colorBg else colorBg.copy(alpha = if (config.isEnabled) 1f else 0.5f),
        contentColor = colorFg.copy(alpha = if (config.isEnabled) 1f else 0.5f),
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp,colorBorder),
        interactionSource = interactionSource,
        content = {
            Row(
                modifier = config.innerRowModifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = config.innerRowArrangement
            ) {
                val textAdditionalM = if (config.textTakesFullWidth) Modifier.weight(1f) else Modifier

                config.leftIcon?.let {
                    InternalIcon(applyTint = config.iconTint, config = it, iconTint = colorFg.copy(alpha = if (config.isEnabled) 1f else 0.5f),)
                }
                config.text?.let { InternalText(config = it,textAdditionalM) }
                config.rightIcon?.let {
                    InternalIcon(applyTint = config.iconTint,config = it,iconTint = colorFg.copy(alpha = if (config.isEnabled) 1f else 0.5f),)
                }
            }
        },

        )
}

private val defaultTextModifier = Modifier.padding(horizontal = 8.dp,vertical = 8.dp)

@Composable
fun AppPrimaryButton(
    text: String? = null,
    modifier: Modifier = Modifier,
    shape: Shape  = RoundedCornerShape(50),
    leftIcon: InternalIconConfig? = null,
    rightIcon: InternalIconConfig? = null,
    isEnabled: Boolean = true,
    textTakesFullWidth: Boolean = false,
    iconTint: Boolean = true,
    textModifier: Modifier =  defaultTextModifier,
    onClick: () -> Unit = {},
) {
    val style = textStyles().CTALarge
    RawAppRoundButton(
        config = AppButtonConfig(
            text = text?.let {  InternalTextConfig.Text(
                text = text,
                modifier = textModifier,
                style = style,
                align = TextAlign.Center
            )},
            outerSurfaceModifier = modifier,
            innerRowModifier = Modifier.padding(8.dp, vertical = 8.dp),
            leftIcon = leftIcon,
            rightIcon  = rightIcon,
            shape = shape,
            iconTint = iconTint,
            type = ROUND_PRIMARY,
            isEnabled = isEnabled,

            innerRowArrangement = Arrangement.Center,
            textTakesFullWidth = textTakesFullWidth ,
        ),
        onclick = {onClick.invoke()}
    )
}

@Composable
fun AppSecondaryButton(
    text: String? = null,
    modifier: Modifier = Modifier,
    shape: Shape  = RoundedCornerShape(50),
    leftIcon: InternalIconConfig? = null,
    rightIcon: InternalIconConfig? = null,
    isEnabled: Boolean = true,
    textTakesFullWidth: Boolean = false,
    textModifier: Modifier =  defaultTextModifier,
    onClick: () -> Unit = {},
) {
    val style = MaterialTheme.localTypographyClass.CTALarge
    RawAppRoundButton(
        config = AppButtonConfig(
            text = text?.let {  InternalTextConfig.Text(
                text = text,
                modifier = textModifier,
                style = style,
                align = TextAlign.Center
            )},
            outerSurfaceModifier = modifier,
            innerRowModifier = Modifier.padding(8.dp, vertical = 8.dp),
            leftIcon = leftIcon,
            rightIcon  = rightIcon,
            shape = shape,
            type = ROUND_SECONDARY,
            isEnabled = isEnabled,
            innerRowArrangement = Arrangement.Center,
            textTakesFullWidth = textTakesFullWidth ,
        ),
        onclick = {onClick.invoke()}
    )
}


@Composable
fun AppLinkButton(
    text: String? = null,
    modifier: Modifier = Modifier,
    shape: Shape  = RoundedCornerShape(50),
    leftIcon: InternalIconConfig? = null,
    rightIcon: InternalIconConfig? = null,
    isEnabled: Boolean = true,
    textTakesFullWidth: Boolean = false,
    textModifier: Modifier =  defaultTextModifier,
    onClick: () -> Unit = {},
) {
    val style = MaterialTheme.localTypographyClass.CTALarge.copy(textDecoration = TextDecoration.Underline)
    RawAppRoundButton(
        config = AppButtonConfig(
            text = text?.let {  InternalTextConfig.Text(
                text = text,
                modifier = textModifier,
                style = style,
                align = TextAlign.Center
            )},
            outerSurfaceModifier = modifier,
            innerRowModifier = Modifier.padding(8.dp, vertical = 8.dp),
            leftIcon = leftIcon,
            shape = shape,
            rightIcon  = rightIcon,
            type = LINK,
            isEnabled = isEnabled,
            innerRowArrangement = Arrangement.Center,
            textTakesFullWidth = textTakesFullWidth ,
        ),
        onclick = {onClick.invoke()}
    )
}




@Preview(showBackground = true)
@Composable
fun PreviewButton() {
    Column(Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
    ) {
        val left = InternalIconConfig.vector(Icons.Default.Add)
        val right = InternalIconConfig.vector(Icons.AutoMirrored.Default.ArrowForwardIos)
        val rowArr =Arrangement.spacedBy(8.dp)

        // ───── Primary vs Secondary ─────
        Text("Primary / Secondary • Enabled", style = textStylesSystem().titleMedium)
        Row(horizontalArrangement = rowArr) {
            AppPrimaryButton(text = "Primary",  )
            AppSecondaryButton(text = "Secondary", )
            AppLinkButton(text = "Link")
        }

        Text("Primary / Secondary • Disabled", style = textStylesSystem().titleMedium)
        Row(horizontalArrangement = rowArr) {
            AppPrimaryButton(text = "Primary", isEnabled = false,)
            AppSecondaryButton(text = "Secondary",   isEnabled = false,)
            AppLinkButton(text = "Link",   isEnabled = false,)
        }

        Spacer(Modifier.height(16.dp))

        // ───── Icon combinations ─────
        Text("Icon combinations", style = textStylesSystem().titleMedium)
        Row(horizontalArrangement = rowArr) {
            AppPrimaryButton(text = "Both icons ", leftIcon = left, rightIcon = right)
            AppPrimaryButton(text = "No icons (default)")
        }
        Row(horizontalArrangement = rowArr) {
            AppSecondaryButton(text = "Both icons ", leftIcon = left, rightIcon = right)
            AppSecondaryButton(text = "No icons (default)")
        }
        Row(horizontalArrangement = rowArr) {
            AppLinkButton(text = "Both icons ", leftIcon = left, rightIcon = right)
            AppLinkButton(text = "No icons (default)")
        }

        Row(horizontalArrangement = rowArr){
            AppPrimaryButton(text = "Left icon only", leftIcon = left, rightIcon = null, )
            AppPrimaryButton(text = "Right icon only", leftIcon = null, rightIcon = right,)
        }
        Row(horizontalArrangement = rowArr){
            AppSecondaryButton(text = "Left icon only", leftIcon = left, rightIcon = null, )
            AppSecondaryButton(text = "Right icon only", leftIcon = null, rightIcon = right,)
        }


        Row(horizontalArrangement = rowArr){
            AppLinkButton(text = "Left icon only", leftIcon = left, rightIcon = null,)
            AppLinkButton(text = "Right icon only", leftIcon = null, rightIcon = right,)
        }
        Row(horizontalArrangement = rowArr) {
            AppPrimaryButton(leftIcon = left, )
            AppPrimaryButton(rightIcon = right, )
            AppSecondaryButton(leftIcon = left, )
            AppSecondaryButton(rightIcon = right, )
            AppLinkButton (leftIcon = left, )
            AppLinkButton(rightIcon = right, )
        }

        Spacer(Modifier.height(16.dp))

        Text("textTakesFullWidth = true", style = textStylesSystem().titleMedium)

        AppPrimaryButton(
            text = "Long Button Style 1",
            textTakesFullWidth = true,
            modifier = Modifier.fillMaxWidth(),
            leftIcon = left, rightIcon = right
        )

        AppPrimaryButton(
            text = "Long Button Style 2",
            textTakesFullWidth = false,
            modifier = Modifier.fillMaxWidth(),
            leftIcon = left, rightIcon = right
        )
        AppPrimaryButton(
            text = "Long Button Style 3",
            textTakesFullWidth = false,
            textModifier = Modifier.padding(horizontal = 24.dp),
            modifier = Modifier.fillMaxWidth(),
            leftIcon = left, rightIcon = right
        )
        AppPrimaryButton(
            text = "Custom Width Button Style 1",
            textTakesFullWidth = false,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            leftIcon = left, rightIcon = right
        )
        AppPrimaryButton(
            text = "Custom Width Button Style 2",
            textTakesFullWidth = false,
            textModifier = Modifier.padding(horizontal = 24.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            leftIcon = left, rightIcon = right
        )
        AppPrimaryButton(
            text = "Custom Width Button Style 2",
            textTakesFullWidth = false,
            textModifier = Modifier.padding(end = 8.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            leftIcon = null, rightIcon = InternalIconConfig.vector(Icons.AutoMirrored.Default.ArrowRightAlt)
        )


        Spacer(Modifier.height(16.dp))

        // ───── Compact / wrap-content examples ─────
        Text("Icon Types", style = textStylesSystem().titleMedium)

        Row(horizontalArrangement = rowArr, modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())) {
            AppPrimaryButton(text = "Vector", rightIcon = InternalIconConfig.vector(Icons.AutoMirrored.Default.ArrowRightAlt))
            AppPrimaryButton(text = "Resource", rightIcon = InternalIconConfig.res(R.drawable.ic_launcher_foreground))
            AppPrimaryButton(text = "Text", rightIcon = InternalIconConfig.text(">>"))
            AppPrimaryButton(text = "Emoji", rightIcon = InternalIconConfig.text("🤑"))
        }
    }
}


@Composable
fun AppToolbar(
    modifier: Modifier = Modifier,
    title: String,
    titleStyle: TextStyle = MaterialTheme.localTypographyClass.titleLarge,
    startIcon: ActionModel? = ActionModel("", "", ActionModelType.BACK),
    rightMenu: List<ActionModel> = listOf(),
    rightMenuIcon: ImageVector = Icons.Default.MoreVert,
    onClick: (ActionModel) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    val contentColor = colors.onSurface
    Column(modifier) {
        Column(
            Modifier.Companion
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                verticalAlignment = Alignment.Companion.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                startIcon?.let {
                    IconButton(onClick = { onClick.invoke((startIcon)) }, ) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "", tint = contentColor)
                    }
                }
                Text(
                    text = title,
                    modifier = Modifier.Companion
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    color = contentColor,
                    style = titleStyle
                )
                if (rightMenu.isNotEmpty()) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(rightMenuIcon, contentDescription = null, tint = contentColor)
                    }
                }

            }
        }

        Box(
            Modifier.Companion
                .wrapContentHeight()
                .wrapContentWidth()
                .align(Alignment.Companion.End)
                .padding(horizontal = 8.dp)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                rightMenu.map { action ->
                    val text = when (action.type) {
                        ActionModelType.URL -> action.text
                        else -> action.type.name.toLangSpecific()
                    }
                    DropdownMenuItem(
                        text = {
                            Row(
                                Modifier.Companion.fillMaxWidth(),
                                verticalAlignment = Alignment.Companion.CenterVertically
                            ) {
                                Image(
                                    modifier = Modifier.Companion
                                        .padding(end = 8.dp)
                                        .size(24.dp)
                                        .padding(4.dp),
                                    painter = painterResource(action.icon),
                                    contentScale = ContentScale.Companion.FillBounds,
                                    contentDescription = "",
                                    colorFilter = ColorFilter.Companion.tint(MaterialTheme.colorScheme.onBackground)
                                )
                                Text(
                                    text = text.toLangSpecific(),
                                    modifier = Modifier.Companion
                                        .weight(1f)
                                        .padding(8.dp),
                                    style = textStylesSystem().bodyMedium
                                )
                            }

                        },
                        onClick = {
                            expanded = false
                            onClick.invoke((action))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonBottomSheet(
    sheetType: AppCommonBottomSheetType,
    onDismiss: () -> Unit,
    onSheetActions:(AppCommonBottomSheetIntents)-> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    ModalBottomSheet(
        modifier = Modifier.Companion,
        onDismissRequest = { onDismiss() },
        dragHandle = { SheetDragHandle() },
        sheetState = sheetState,
        scrimColor = colors().background.copy(alpha = 0.5f),
        containerColor = colors().tertiaryContainer
    ) {
        when (sheetType) {

            is AppCommonBottomSheetType.ClearCacheBottomSheet -> ClearCacheBottomSheet(

                onDismiss,
                onSheetActions
            )

            is AppCommonBottomSheetType.ErrorSheet -> ErrorSheet(
                sheetType,
                onDismiss,
                onSheetActions
            )

            is AppCommonBottomSheetType.SelectThemeBottomSheet -> {
                SelectThemeSheet(sheetType,onDismiss,onSheetActions)
            }
        }
    }
}


@Preview
@Composable
fun SheetDragHandle(showDragHandle: Boolean = true) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = MaterialTheme.localDimens.dp8, bottom = MaterialTheme.localDimens.dp8),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showDragHandle) {
            Box(
                modifier = Modifier
                    .width(MaterialTheme.localDimens.dp80)
                    .height(MaterialTheme.localDimens.dp4)
                    .clip(RoundedCornerShape(MaterialTheme.localDimens.dp25))
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}


@Preview
@Composable
fun GradientCircularProgressIndicator(
    modifier: Modifier = Modifier.Companion,
    size: Dp = 72.dp,
    gradientColors: List<Color> = listOf(AppColors.orange_bright_ff8, AppColors.orange_m200),
    strokeWidth: Dp = 16.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ), label = ""
    )

    Canvas(
        modifier = modifier
            .size(size)
            .rotate(angle)
    ) {
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Companion.Round)
        drawArc(
            brush = Brush.Companion.linearGradient(gradientColors),
            startAngle = 0f,
            sweepAngle = 270f,
            useCenter = false,
            style = stroke
        )
    }
}

@Preview
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier.Companion,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
) {

    val grey = MaterialTheme.colorScheme.inversePrimary

    val shimmerColors = listOf(
        grey.copy(alpha = 0.3f),
        grey.copy(alpha = 0.5f),
        grey.copy(alpha = 1.0f),
        grey.copy(alpha = 0.5f),
        grey.copy(alpha = 0.3f),
    )

    val transition = rememberInfiniteTransition(label = "")

    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Shimmer loading animation",
    )

    val brush = Brush.Companion.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
        end = Offset(x = translateAnimation.value, y = angleOfAxisY),
    )

    Box(
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier.Companion
                .matchParentSize()
                .background(brush)
        )
    }


}






@Preview
@Composable
fun ErrorSheet(
    sheetConfig: AppCommonBottomSheetType.ErrorSheet = AppCommonBottomSheetType.ErrorSheet.sww(),
    onDismiss: () -> Unit = {},
    onBottomSheetEvent: (AppCommonBottomSheetIntents) -> Unit = {}
) {
    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = rememberVectorPainter(Icons.Default.WarningAmber),
            contentDescription = "",
            modifier = Modifier.Companion
                .size(100.dp)
                .background(AppColors.orange_m200, CircleShape)
                .clip(CircleShape)
                .padding(16.dp),
            colorFilter = ColorFilter.Companion.tint(AppColors.orange_bright_ff8)
        )
        Text(
            text = sheetConfig.title,
            style = textStylesSystem().titleLarge,
            modifier = Modifier.Companion.padding(horizontal = 8.dp)
        )
        Text(
            text = sheetConfig.subtitle,
            style = textStylesSystem().bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.Companion.padding(horizontal = 8.dp)
        )
        Text(
            text = "${sheetConfig.errorInfo.code} | ${sheetConfig.errorInfo.name}",
            style = textStylesSystem().bodyMedium,
            modifier = Modifier.Companion
                .padding(horizontal = 8.dp)
                .alpha(0.5f)
        )
        OutlinedButton(
            modifier = Modifier.Companion.fillMaxWidth(),
            onClick = { onDismiss.invoke() }
        ) {
            Text(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Companion.Center,
                style = textStylesSystem().bodyLarge,
                text = stringResource(R.string.dismiss)
            )
        }
        Spacer(
            Modifier.Companion
                .fillMaxWidth()
                .height(36.dp)
        )
    }
}
fun String.capitaliseEachWord(forceLowercaseFirst: Boolean = true): String {
    return this.split(" ").joinToString(" ") {
        val part = if (forceLowercaseFirst) it.lowercase() else it
        part.replaceFirstChar { c -> c.uppercase() }
    }
}


@Composable
fun String.toLangSpecific(): String{
    return when(this){
        ActionModelType.DEEPLINK_CHANGE_THEME.name -> stringResource(R.string.change_theme)
        ActionModelType.DEEPLINK_CLEAR_CACHE.name -> stringResource(R.string.clear_all_bookmarks_and_api_cache)

        else -> this
    }
}



