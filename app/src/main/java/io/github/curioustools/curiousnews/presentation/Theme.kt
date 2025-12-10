package io.github.curioustools.curiousnews.presentation

import android.content.res.Configuration
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import io.github.curioustools.curiousnews.R
import io.github.curioustools.curiousnews.data.SharedPrefs
import io.github.curioustools.curiousnews.presentation.MyFonts.ContentFont
import io.github.curioustools.curiousnews.presentation.MyFonts.DisplayFont
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.apply
import kotlin.collections.average
import kotlin.collections.first
import kotlin.collections.map
import kotlin.let
import kotlin.stackTraceToString

//    "best way to make a theme : https://material-foundation.github.io/material-theme-builder/"
//    "refer : https://fonts.gstatic.com/s/a/directory.xml for dynamic font names"


//---------------<Themes>--------------------
object AppColors{
    val transparent = Color(0x00000000)
    val black_000 = Color(0xFF000000)
    val blue_1e88e5 = Color(0xFF1E88E5)
    val black_393939 = Color(0xFF393939)
    val white_ececec = Color(0xFFF4F4F4)
    val gray_light_c1c1c1 = Color(0xFFC1C1C1)
    val green_dark_4caf50 = Color(0xFF4CAF50)
    val brown_m900 = Color(0xFFBF360C)
    val black = Color(0xFF000000)
    val white = Color(0xFFFFFFFF)
    val pink_e91 = Color(0xFFE91E63)
    val purple_9c2 = Color(0xFF9C27B0)
    val green_5ae = Color(0xFF5AE860)
    val green_048 = Color(0xFF4CAF50)
    val orangee65 = Color(0xFFE65100)
    val orange_bright_ff8 = Color(0xFFFF812D)
    val orange_m200 = Color(0xFFFFCC80)
    val gray_c1c1 = Color(0xFFC1C1C1)
    val orange_v_light = Color(0xFFFFFBF5)
    val pink_v_light = Color(0xFFFDF5FF)
    val blue_v_light_f0ffff = Color(0xFFF0FFFF)
    val black_tp30 = Color(0x4D000000)
    val green_v_light_aacf98 = Color(0xFFE4FEE1)
    val pink_ff206d = Color(0xFFFF206D)
    val purple_e86afd = Color(0xFFE86AFD)
    val blue_0d9bfa = Color(0xFF0D9BFA)
    val blue_6ae7fd = Color(0xFF6AE7FD)
    val green_8dd837 = Color(0xFF8DD837)
    val green_5aca15 = Color(0xFF5ACA15)
    val gray_eeeded = Color(0xFFEEEDED)
    val red_ffff0c00 = Color(0xFFff0c00)

    val primaryLight = Color(0xFF8C4E29)
    val onPrimaryLight = Color(0xFFFFFFFF)
    val primaryContainerLight = Color(0xFFFFDBCA)
    val onPrimaryContainerLight = Color(0xFF6F3814)
    val secondaryLight = Color(0xFF8C4E29)
    val onSecondaryLight = Color(0xFFFFFFFF)
    val secondaryContainerLight = Color(0xFFFFDBCA)
    val onSecondaryContainerLight = Color(0xFF6F3814)
    val tertiaryLight = Color(0xFF8C4E29)
    val onTertiaryLight = Color(0xFFFFFFFF)
    val tertiaryContainerLight = Color(0xFFFFDBCA)
    val onTertiaryContainerLight = Color(0xFF6F3814)
    val errorLight = Color(0xFFBA1A1A)
    val onErrorLight = Color(0xFFFFFFFF)
    val errorContainerLight = Color(0xFFFFDAD6)
    val onErrorContainerLight = Color(0xFF93000A)
    val backgroundLight = Color(0xFFFFF8F6)
    val onBackgroundLight = Color(0xFF221A15)
    val surfaceLight = Color(0xFFFFF8F6)
    val onSurfaceLight = Color(0xFF221A15)
    val surfaceVariantLight = Color(0xFFF4DED4)
    val onSurfaceVariantLight = Color(0xFF52443C)
    val outlineLight = Color(0xFF85746B)
    val outlineVariantLight = Color(0xFFD7C2B9)
    val scrimLight = Color(0xFF000000)
    val inverseSurfaceLight = Color(0xFF382E29)
    val inverseOnSurfaceLight = Color(0xFFFFEDE5)
    val inversePrimaryLight = Color(0xFFFFB68E)
    val surfaceDimLight = Color(0xFFE8D7CF)
    val surfaceBrightLight = Color(0xFFFFF8F6)
    val surfaceContainerLowestLight = Color(0xFFFFFFFF)
    val surfaceContainerLowLight = Color(0xFFFFF1EB)
    val surfaceContainerLight = Color(0xFFFCEAE3)
    val surfaceContainerHighLight = Color(0xFFF6E5DD)
    val surfaceContainerHighestLight = Color(0xFFF0DFD7)

    val primaryDark = Color(0xFFFFB68E)
    val onPrimaryDark = Color(0xFF532201)
    val primaryContainerDark = Color(0xFF6F3814)
    val onPrimaryContainerDark = Color(0xFFFFDBCA)
    val secondaryDark = Color(0xFFFFB68E)
    val onSecondaryDark = Color(0xFF532201)
    val secondaryContainerDark = Color(0xFF6F3814)
    val onSecondaryContainerDark = Color(0xFFFFDBCA)
    val tertiaryDark = Color(0xFFFFB68E)
    val onTertiaryDark = Color(0xFF532201)
    val tertiaryContainerDark = Color(0xFF6F3814)
    val onTertiaryContainerDark = Color(0xFFFFDBCA)
    val errorDark = Color(0xFFFFB4AB)
    val onErrorDark = Color(0xFF690005)
    val errorContainerDark = Color(0xFF93000A)
    val onErrorContainerDark = Color(0xFFFFDAD6)
    val backgroundDark = Color(0xFF1A120D)
    val onBackgroundDark = Color(0xFFF0DFD7)
    val surfaceDark = Color(0xFF1A120D)
    val onSurfaceDark = Color(0xFFF0DFD7)
    val surfaceVariantDark = Color(0xFF52443C)
    val onSurfaceVariantDark = Color(0xFFD7C2B9)
    val outlineDark = Color(0xFF9F8D84)
    val outlineVariantDark = Color(0xFF52443C)
    val scrimDark = Color(0xFF000000)
    val inverseSurfaceDark = Color(0xFFF0DFD7)
    val inverseOnSurfaceDark = Color(0xFF382E29)
    val inversePrimaryDark = Color(0xFF8C4E29)
    val surfaceDimDark = Color(0xFF1A120D)
    val surfaceBrightDark = Color(0xFF413732)
    val surfaceContainerLowestDark = Color(0xFF140C09)
    val surfaceContainerLowDark = Color(0xFF221A15)
    val surfaceContainerDark = Color(0xFF271E19)
    val surfaceContainerHighDark = Color(0xFF322823)
    val surfaceContainerHighestDark = Color(0xFF3D332E)
}
private val lightScheme = lightColorScheme(
    primary = AppColors.primaryLight,
    onPrimary = AppColors.onPrimaryLight,
    primaryContainer = AppColors.primaryContainerLight,
    onPrimaryContainer = AppColors.onPrimaryContainerLight,
    secondary = AppColors.secondaryLight,
    onSecondary = AppColors.onSecondaryLight,
    secondaryContainer = AppColors.secondaryContainerLight,
    onSecondaryContainer = AppColors.onSecondaryContainerLight,
    tertiary = AppColors.tertiaryLight,
    onTertiary = AppColors.onTertiaryLight,
    tertiaryContainer = AppColors.tertiaryContainerLight,
    onTertiaryContainer = AppColors.onTertiaryContainerLight,
    error = AppColors.errorLight,
    onError = AppColors.onErrorLight,
    errorContainer = AppColors.errorContainerLight,
    onErrorContainer = AppColors.onErrorContainerLight,
    background = AppColors.backgroundLight,
    onBackground = AppColors.onBackgroundLight,
    surface = AppColors.surfaceLight,
    onSurface = AppColors.onSurfaceLight,
    surfaceVariant = AppColors.surfaceVariantLight,
    onSurfaceVariant = AppColors.onSurfaceVariantLight,
    outline = AppColors.outlineLight,
    outlineVariant = AppColors.outlineVariantLight,
    scrim = AppColors.scrimLight,
    inverseSurface = AppColors.inverseSurfaceLight,
    inverseOnSurface = AppColors.inverseOnSurfaceLight,
    inversePrimary = AppColors.inversePrimaryLight,
    surfaceDim = AppColors.surfaceDimLight,
    surfaceBright = AppColors.surfaceBrightLight,
    surfaceContainerLowest = AppColors.surfaceContainerLowestLight,
    surfaceContainerLow = AppColors.surfaceContainerLowLight,
    surfaceContainer = AppColors.surfaceContainerLight,
    surfaceContainerHigh = AppColors.surfaceContainerHighLight,
    surfaceContainerHighest = AppColors.surfaceContainerHighestLight,
)
private val darkScheme = darkColorScheme(
    primary = AppColors.primaryDark,
    onPrimary = AppColors.onPrimaryDark,
    primaryContainer = AppColors.primaryContainerDark,
    onPrimaryContainer = AppColors.onPrimaryContainerDark,
    secondary = AppColors.secondaryDark,
    onSecondary = AppColors.onSecondaryDark,
    secondaryContainer = AppColors.secondaryContainerDark,
    onSecondaryContainer = AppColors.onSecondaryContainerDark,
    tertiary = AppColors.tertiaryDark,
    onTertiary = AppColors.onTertiaryDark,
    tertiaryContainer = AppColors.tertiaryContainerDark,
    onTertiaryContainer = AppColors.onTertiaryContainerDark,
    error = AppColors.errorDark,
    onError = AppColors.onErrorDark,
    errorContainer = AppColors.errorContainerDark,
    onErrorContainer = AppColors.onErrorContainerDark,
    background = AppColors.backgroundDark,
    onBackground = AppColors.onBackgroundDark,
    surface = AppColors.surfaceDark,
    onSurface = AppColors.onSurfaceDark,
    surfaceVariant = AppColors.surfaceVariantDark,
    onSurfaceVariant = AppColors.onSurfaceVariantDark,
    outline = AppColors.outlineDark,
    outlineVariant = AppColors.outlineVariantDark,
    scrim = AppColors.scrimDark,
    inverseSurface = AppColors.inverseSurfaceDark,
    inverseOnSurface = AppColors.inverseOnSurfaceDark,
    inversePrimary = AppColors.inversePrimaryDark,
    surfaceDim = AppColors.surfaceDimDark,
    surfaceBright = AppColors.surfaceBrightDark,
    surfaceContainerLowest = AppColors.surfaceContainerLowestDark,
    surfaceContainerLow = AppColors.surfaceContainerLowDark,
    surfaceContainer = AppColors.surfaceContainerDark,
    surfaceContainerHigh = AppColors.surfaceContainerHighDark,
    surfaceContainerHighest = AppColors.surfaceContainerHighestDark,
)

//---------------</Themes>--------------------


//---------------<Fonts and Typography>--------------------
// common fonts
private data object MyFonts{
    private val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
    val CustomFontNormal = FontFamily(Font(
        googleFont = GoogleFont("Comfortaa"),
        fontProvider = provider,
        weight =  FontWeight.Normal,
    ))
    val CustomFontMedium = FontFamily(Font(
        googleFont = GoogleFont("Comfortaa"),
        fontProvider = provider,
        weight =  FontWeight.Medium,
    ))
    val CustomFontSemiBold = FontFamily(Font(
        googleFont = GoogleFont("Comfortaa"),
        fontProvider = provider,
        weight =  FontWeight.SemiBold,
    ))
    val CustomFontBold = FontFamily(Font(
        googleFont = GoogleFont("Comfortaa"),
        fontProvider = provider,
        weight =  FontWeight.Bold,
    ))

    val DisplayFont = FontFamily(Font(
            googleFont = GoogleFont("Comfortaa"),
            fontProvider = provider,
        ))
    val ContentFont = FontFamily(Font(
        googleFont = GoogleFont("Comfortaa"),
        fontProvider = provider,
    ))
}
data class LocalTypographyClass(val sf: Float = 1f) {
    private val bold = MyFonts.CustomFontBold
    private val semiBold = MyFonts.CustomFontSemiBold
    private val regular = MyFonts.CustomFontNormal
    private val medium = MyFonts.CustomFontMedium
    val h0: TextStyle = TextStyle(fontFamily = bold,fontSize = 28f.toSp(sf), )
    val h1: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 28f.toSp(sf), )
    val h2: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 24f.toSp(sf), )
    val h3: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 20f.toSp(sf), )
    val h4: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 18f.toSp(sf), )
    val h5: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 16f.toSp(sf), )
    val h6: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 14f.toSp(sf), )
    val h7: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 12f.toSp(sf), )
    val bodyExtraSmall: TextStyle = TextStyle(fontFamily = regular,fontSize = 10f.toSp(sf),)
    val bodyLarge: TextStyle = TextStyle(fontFamily = regular,fontSize = 16f.toSp(sf),fontWeight = W400)
    val bodyRegular: TextStyle = TextStyle(fontFamily = regular,fontSize = 14f.toSp(sf),fontWeight = W400)
    val bodyRegularB: TextStyle = TextStyle(fontFamily = bold,fontSize = 14f.toSp(sf),fontWeight = W700)
    val bodySmall: TextStyle = TextStyle(fontFamily = regular,fontSize = 12f.toSp(sf),fontWeight = W400)
    val bodySmallB: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 12f.toSp(sf),fontWeight = W600)
    val bodyXS: TextStyle = TextStyle(fontFamily = regular,fontSize = 10f.toSp(sf),fontWeight = W400)
    val CTALarge: TextStyle = TextStyle(fontFamily = medium,fontSize = 14f.toSp(sf),fontWeight = W400)
    val ctaRegular: TextStyle = TextStyle(fontFamily = medium,fontSize = 14f.toSp(sf),fontWeight = W500)
    val ctaRegularB: TextStyle = TextStyle(fontFamily = bold,fontSize = 14f.toSp(sf),fontWeight = W700)
    val CTASmall: TextStyle = TextStyle(fontFamily = medium,fontSize = 12f.toSp(sf),)
    val ctaSmall: TextStyle = TextStyle(fontFamily = medium,fontSize = 12f.toSp(sf),fontWeight = W500)
    val formMessage: TextStyle = TextStyle(fontFamily = medium,fontSize = 10f.toSp(sf),fontWeight = W500)
    val formPlaceholder: TextStyle = TextStyle(fontFamily = medium,fontSize = 14f.toSp(sf),fontWeight = W500)
    val headerLarge: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 28f.toSp(sf),fontWeight = W600)
    val headerLargeB: TextStyle = TextStyle(fontFamily = bold,fontSize = 28f.toSp(sf),fontWeight = W700)
    val headerRegular: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 24f.toSp(sf),fontWeight = W600)
    val inputFieldPlaceHolder: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 10f.toSp(sf),)
    val labelLarge: TextStyle = TextStyle(fontFamily = medium,fontSize = 14f.toSp(sf),fontWeight = W500)
    val labelRegular: TextStyle = TextStyle(fontFamily = medium,fontSize = 12f.toSp(sf),fontWeight = W500)
    val labelSmall: TextStyle = TextStyle(fontFamily = medium,fontSize = 10f.toSp(sf),fontWeight = W500)
    val labelXLarge: TextStyle = TextStyle(fontFamily = medium,fontSize = 16f.toSp(sf),fontWeight = W500)
    val labelXS: TextStyle = TextStyle(fontFamily = medium,fontSize = 8f.toSp(sf),fontWeight = W500)
    val regularLabel: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 12f.toSp(sf),)
    val smallLabels: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 10f.toSp(sf),)
    val subtitle: TextStyle = TextStyle(fontFamily = medium,fontSize = 14f.toSp(sf), )
    val subtitleSmall: TextStyle = TextStyle(fontFamily = medium,fontSize = 12f.toSp(sf),)
    val titleLarge: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 18f.toSp(sf),fontWeight = W600)
    val titleRegular: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 16f.toSp(sf),fontWeight = W600)
    val titleSmall: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 14f.toSp(sf),fontWeight = W600)
    val titleXL: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 20f.toSp(sf),fontWeight = W600)
    val titleXS: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 12f.toSp(sf),fontWeight = W600)
    val titleXXS: TextStyle = TextStyle(fontFamily = semiBold,fontSize = 10f.toSp(sf),fontWeight = W600)
}//typography classes accessible using MaterialTheme.localTypographyClass
val LocalTypography = compositionLocalOf { LocalTypographyClass() }
val MaterialTheme.localTypographyClass: LocalTypographyClass @Composable @ReadOnlyComposable get() = LocalTypography.current
val baseline = Typography()//general typography classes applied to system components(calender, buttons etc). accessible using textStylesSystem()
val systemTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = DisplayFont),
    displayMedium = baseline.displayMedium.copy(fontFamily = DisplayFont),
    displaySmall = baseline.displaySmall.copy(fontFamily = DisplayFont),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = DisplayFont),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = DisplayFont),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = DisplayFont),
    titleLarge = baseline.titleLarge.copy(fontFamily = DisplayFont),
    titleMedium = baseline.titleMedium.copy(fontFamily = DisplayFont),
    titleSmall = baseline.titleSmall.copy(fontFamily = DisplayFont),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = ContentFont),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = ContentFont),
    bodySmall = baseline.bodySmall.copy(fontFamily = ContentFont),
    labelLarge = baseline.labelLarge.copy(fontFamily = ContentFont),
    labelMedium = baseline.labelMedium.copy(fontFamily = ContentFont),
    labelSmall = baseline.labelSmall.copy(fontFamily = ContentFont),
)

// exception handler for fonts
val fontErrorHandler =
    CoroutineExceptionHandler { _, throwable -> println("There has been an issue in accessing fonts: " + throwable.stackTraceToString()) }

//---------------</Fonts and Typography>--------------------

//------- <Dimens> (Dynamic text size scaling) -------// -------// -------//
fun Configuration.getCustomScaleFactor(): Float{
    val configuration = this
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp
    val scale =  when {
        screenWidthDp <= 320 && screenHeightDp <= 480 -> 0.80f  // Very small screens (e.g. old phones)
        screenWidthDp <= 360 && screenHeightDp <= 640 -> 0.90f  // Small phones
        screenWidthDp <= 400 && screenHeightDp <= 800 -> 1.0f   // Normal phones
        screenWidthDp <= 600 && screenHeightDp <= 1024 -> 1.15f // Large phones or small tablets
        screenWidthDp <= 720 && screenHeightDp <= 1280 -> 1.25f // Small tablets or phablets
        else -> 1.35f                                           // Large tablets and beyond
    }
    return scale
}
fun Float.toDp(scale: Float=1f): Dp = (this * scale).dp
fun Float.toSp(scale: Float): TextUnit = (this * scale).sp
@Composable fun Float.toScaledSp(): TextUnit = toSp(LocalConfiguration.current.getCustomScaleFactor())
@Composable fun Float.toScaledDp(): Dp = toDp(LocalConfiguration.current.getCustomScaleFactor())
data class Dimens(val scaleFactor: Float = 1f) {
    val dp0: Dp = 0f.toDp(scaleFactor)
    val dpPoint5: Dp = 0.5f.toDp(scaleFactor)
    val dp1: Dp = 1f.toDp(scaleFactor)
    val dp2: Dp = 2f.toDp(scaleFactor)
    val dp3: Dp = 3f.toDp(scaleFactor)
    val dp4: Dp = 4f.toDp(scaleFactor)
    val dp5: Dp = 5f.toDp(scaleFactor)
    val dp6: Dp = 6f.toDp(scaleFactor)
    val dp7: Dp = 7f.toDp(scaleFactor)
    val dp8: Dp = 8f.toDp(scaleFactor)
    val dp9: Dp = 9f.toDp(scaleFactor)
    val dp9p5: Dp = 9.5f.toDp(scaleFactor)
    val dp10: Dp = 10f.toDp(scaleFactor)
    val dp11: Dp = 11f.toDp(scaleFactor)
    val dp12: Dp = 12f.toDp(scaleFactor)
    val dp13: Dp = 13f.toDp(scaleFactor)
    val dp14: Dp = 14f.toDp(scaleFactor)
    val dp15: Dp = 15f.toDp(scaleFactor)
    val dp16: Dp = 16f.toDp(scaleFactor)
    val dp17: Dp = 17f.toDp(scaleFactor)
    val dp18: Dp = 18f.toDp(scaleFactor)
    val dp19: Dp = 19f.toDp(scaleFactor)
    val dp20: Dp = 20f.toDp(scaleFactor)
    val dp21: Dp = 21f.toDp(scaleFactor)
    val dp22: Dp = 22f.toDp(scaleFactor)
    val dp23: Dp = 23f.toDp(scaleFactor)
    val dp24: Dp = 24f.toDp(scaleFactor)
    val dp25: Dp = 25f.toDp(scaleFactor)
    val dp26: Dp = 26f.toDp(scaleFactor)
    val dp27: Dp = 27f.toDp(scaleFactor)
    val dp28: Dp = 28f.toDp(scaleFactor)
    val dp29: Dp = 29f.toDp(scaleFactor)
    val dp30: Dp = 30f.toDp(scaleFactor)
    val dp31: Dp = 31f.toDp(scaleFactor)
    val dp32: Dp = 32f.toDp(scaleFactor)
    val dp33: Dp = 33f.toDp(scaleFactor)
    val dp34: Dp = 34f.toDp(scaleFactor)
    val dp35: Dp = 35f.toDp(scaleFactor)
    val dp36: Dp = 36f.toDp(scaleFactor)
    val dp37: Dp = 37f.toDp(scaleFactor)
    val dp38: Dp = 38f.toDp(scaleFactor)
    val dp39: Dp = 39f.toDp(scaleFactor)
    val dp40: Dp = 40f.toDp(scaleFactor)
    val dp41: Dp = 41f.toDp(scaleFactor)
    val dp42: Dp = 42f.toDp(scaleFactor)
    val dp43: Dp = 43f.toDp(scaleFactor)
    val dp44: Dp = 44f.toDp(scaleFactor)
    val dp45: Dp = 45f.toDp(scaleFactor)
    val dp46: Dp = 46f.toDp(scaleFactor)
    val dp47: Dp = 47f.toDp(scaleFactor)
    val dp48: Dp = 48f.toDp(scaleFactor)
    val dp49: Dp = 49f.toDp(scaleFactor)
    val dp50: Dp = 50f.toDp(scaleFactor)
    val dp51: Dp = 51f.toDp(scaleFactor)
    val dp52: Dp = 52f.toDp(scaleFactor)
    val dp53: Dp = 53f.toDp(scaleFactor)
    val dp54: Dp = 54f.toDp(scaleFactor)
    val dp55: Dp = 55f.toDp(scaleFactor)
    val dp56: Dp = 56f.toDp(scaleFactor)
    val dp57: Dp = 57f.toDp(scaleFactor)
    val dp58: Dp = 58f.toDp(scaleFactor)
    val dp59: Dp = 59f.toDp(scaleFactor)
    val dp60: Dp = 60f.toDp(scaleFactor)
    val dp61: Dp = 61f.toDp(scaleFactor)
    val dp62: Dp = 62f.toDp(scaleFactor)
    val dp63: Dp = 63f.toDp(scaleFactor)
    val dp64: Dp = 64f.toDp(scaleFactor)
    val dp65: Dp = 65f.toDp(scaleFactor)
    val dp66: Dp = 66f.toDp(scaleFactor)
    val dp67: Dp = 67f.toDp(scaleFactor)
    val dp68: Dp = 68f.toDp(scaleFactor)
    val dp69: Dp = 69f.toDp(scaleFactor)
    val dp70: Dp = 70f.toDp(scaleFactor)
    val dp71: Dp = 71f.toDp(scaleFactor)
    val dp72: Dp = 72f.toDp(scaleFactor)
    val dp73: Dp = 73f.toDp(scaleFactor)
    val dp74: Dp = 74f.toDp(scaleFactor)
    val dp75: Dp = 75f.toDp(scaleFactor)
    val dp76: Dp = 76f.toDp(scaleFactor)
    val dp77: Dp = 77f.toDp(scaleFactor)
    val dp78: Dp = 78f.toDp(scaleFactor)
    val dp79: Dp = 79f.toDp(scaleFactor)
    val dp80: Dp = 80f.toDp(scaleFactor)
    val dp81: Dp = 81f.toDp(scaleFactor)
    val dp82: Dp = 82f.toDp(scaleFactor)
    val dp83: Dp = 83f.toDp(scaleFactor)
    val dp84: Dp = 84f.toDp(scaleFactor)
    val dp85: Dp = 85f.toDp(scaleFactor)
    val dp86: Dp = 86f.toDp(scaleFactor)
    val dp87: Dp = 87f.toDp(scaleFactor)
    val dp88: Dp = 88f.toDp(scaleFactor)
    val dp89: Dp = 89f.toDp(scaleFactor)
    val dp90: Dp = 90f.toDp(scaleFactor)
    val dp91: Dp = 91f.toDp(scaleFactor)
    val dp92: Dp = 92f.toDp(scaleFactor)
    val dp93: Dp = 93f.toDp(scaleFactor)
    val dp94: Dp = 94f.toDp(scaleFactor)
    val dp95: Dp = 95f.toDp(scaleFactor)
    val dp96: Dp = 96f.toDp(scaleFactor)
    val dp97: Dp = 97f.toDp(scaleFactor)
    val dp98: Dp = 98f.toDp(scaleFactor)
    val dp99: Dp = 99f.toDp(scaleFactor)
    val dp100: Dp = 100f.toDp(scaleFactor)
    val dp108: Dp = 108f.toDp(scaleFactor)
    val dp110: Dp = 110f.toDp(scaleFactor)
    val dp112: Dp = 112f.toDp(scaleFactor)
    val dp114: Dp = 114f.toDp(scaleFactor)
    val dp115: Dp = 115f.toDp(scaleFactor)
    val dp116: Dp = 116f.toDp(scaleFactor)
    val dp120: Dp = 120f.toDp(scaleFactor)
    val dp125: Dp = 125f.toDp(scaleFactor)
    val dp130: Dp = 130f.toDp(scaleFactor)
    val dp140: Dp = 140f.toDp(scaleFactor)
    val dp145: Dp = 145f.toDp(scaleFactor)
    val dp150: Dp = 150f.toDp(scaleFactor)
    val dp160: Dp = 160f.toDp(scaleFactor)
    val dp165: Dp = 165f.toDp(scaleFactor)
    val dp170: Dp = 170f.toDp(scaleFactor)
    val dp180: Dp = 180f.toDp(scaleFactor)
    val dp185: Dp = 185f.toDp(scaleFactor)
    val dp190: Dp = 190f.toDp(scaleFactor)
    val dp200: Dp = 200f.toDp(scaleFactor)
    val dp210: Dp = 210f.toDp(scaleFactor)
    val dp220: Dp = 220f.toDp(scaleFactor)
    val dp230: Dp = 230f.toDp(scaleFactor)
    val dp240: Dp = 240f.toDp(scaleFactor)
    val dp250: Dp = 250f.toDp(scaleFactor)
    val dp260: Dp = 260f.toDp(scaleFactor)
    val dp270: Dp = 270f.toDp(scaleFactor)
    val dp280: Dp = 280f.toDp(scaleFactor)
    val dp290: Dp = 290f.toDp(scaleFactor)
    val dp300: Dp = 300f.toDp(scaleFactor)
    val dp310: Dp = 310f.toDp(scaleFactor)
    val dp320: Dp = 320f.toDp(scaleFactor)
    val dp330: Dp = 330f.toDp(scaleFactor)
    val dp340: Dp = 340f.toDp(scaleFactor)
    val dp350: Dp = 350f.toDp(scaleFactor)
    val dp360: Dp = 360f.toDp(scaleFactor)
    val dp370: Dp = 370f.toDp(scaleFactor)
    val dp380: Dp = 380f.toDp(scaleFactor)
    val dp390: Dp = 390f.toDp(scaleFactor)
    val dp400: Dp = 400f.toDp(scaleFactor)
    val dp460: Dp = 460f.toDp(scaleFactor)
    val dp700: Dp = 700f.toDp(scaleFactor)

    //text sizes
    val sp8: TextUnit = 8f.toSp(scaleFactor)
    val sp9: TextUnit = 9f.toSp(scaleFactor)
    val sp10: TextUnit = 10f.toSp(scaleFactor)
    val sp11: TextUnit = 11f.toSp(scaleFactor)
    val sp12: TextUnit = 12f.toSp(scaleFactor)
    val sp13: TextUnit = 13f.toSp(scaleFactor)
    val sp14: TextUnit = 14f.toSp(scaleFactor)
    val sp15: TextUnit = 15f.toSp(scaleFactor)
    val sp16: TextUnit = 16f.toSp(scaleFactor)
    val sp17: TextUnit = 17f.toSp(scaleFactor)
    val sp18: TextUnit = 18f.toSp(scaleFactor)
    val sp19: TextUnit = 19f.toSp(scaleFactor)
    val sp20: TextUnit = 20f.toSp(scaleFactor)
    val sp21: TextUnit = 21f.toSp(scaleFactor)
}
val MaterialTheme.localDimens: Dimens @Composable @ReadOnlyComposable get() = LocalDimens.current
val LocalDimens = compositionLocalOf { Dimens() }
//------- </Dimens> (Dynamic text size scaling) -------// -------// -------//

//------- <Theme Related Utils>  -------// -------// -------//  -------//  -------//  -------//  -------//
fun ComponentActivity.enableBackgroundControllableEdgeToEdge() {
    enableEdgeToEdge()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.setNavigationBarContrastEnforced(false)// so that safe color box is useful
    }
}

@Composable
fun colors() = MaterialTheme.colorScheme

@Composable
fun textStyles() = MaterialTheme.localTypographyClass

@Composable
fun textStylesSystem() = MaterialTheme.typography



@Preview
@Composable
fun SafeColorColumn(
    statusBarColors: List<Color> = listOf(colors().background),
    bottomBarColors: List<Color> = statusBarColors,
    mainColors:  List<Color> = statusBarColors,
    content: @Composable () -> Unit = {}
){

    val activity = LocalActivity.current?:return
    val view = LocalView.current
    val statusBarBg = statusBarColors.let { if(it.size>1) Brush.linearGradient(it) else SolidColor(it.first()) }
    val bottomBarBg = bottomBarColors.let { if(it.size>1) Brush.linearGradient(it) else SolidColor(it.first()) }
    val mainBg = mainColors.let { if(it.size>1) Brush.linearGradient(it) else SolidColor(it.first()) }
    SideEffect {
        val window = activity.window
        val showLightStatusIcons = statusBarColors.map { it.luminance() }.average() > 0.5
        val showLightNavIcons = bottomBarColors.map { it.luminance() }.average() > 0.5
        WindowInsetsControllerCompat(window, view).apply {
            isAppearanceLightStatusBars = showLightStatusIcons
            isAppearanceLightNavigationBars = showLightNavIcons
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxWidth().weight(1f).background(brush = statusBarBg), content = {})
            Column(Modifier.fillMaxWidth().weight(1f).background(brush = bottomBarBg), content = {})
        }
        Column(Modifier.fillMaxSize().systemBarsPadding().background(mainBg)) {
            content()
        }

    }
}




//------- </Theme Related Utils>  -------// -------// -------//  -------//  -------//  -------//  -------//

//------- <Actual Theme>  -------// -------// -------//  -------//  -------//  -------//  -------//
@Composable fun CommonTheme(darkTheme: Boolean,dynamicColor: Boolean,  content:@Composable () -> Unit){
    val context = LocalContext.current
    val scaleFactor = LocalConfiguration.current.getCustomScaleFactor()
    val isGreaterOrEqualSDK31S = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && isGreaterOrEqualSDK31S && darkTheme -> dynamicDarkColorScheme(context)
        dynamicColor && isGreaterOrEqualSDK31S && !darkTheme -> dynamicLightColorScheme(context)
        darkTheme -> darkScheme
        else -> lightScheme
    }
    CompositionLocalProvider(
        LocalFontFamilyResolver provides createFontFamilyResolver(context, fontErrorHandler),
        LocalTypography provides LocalTypographyClass(scaleFactor),
        LocalDimens provides Dimens(scaleFactor)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = systemTypography,
            content = content
        )
    }
}


@Composable
fun isAppInDarkTheme(): Boolean{
    val userPrefs = SharedPrefs(LocalContext.current).userSettings
    return userPrefs.themeType == SharedPrefs.ThemeMode.DARK ||
            userPrefs.themeType == SharedPrefs.ThemeMode.SYSTEM && isSystemInDarkTheme()
}



@Composable
fun AppTheme(
    themeType: SharedPrefs.ThemeMode,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    val darkTheme: Boolean = when(themeType) {
        SharedPrefs.ThemeMode.LIGHT -> false
        SharedPrefs.ThemeMode.DARK -> true
        SharedPrefs.ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    CommonTheme(darkTheme,dynamicColor,content)
}

