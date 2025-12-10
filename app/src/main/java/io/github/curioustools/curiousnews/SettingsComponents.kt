package io.github.curioustools.curiousnews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.curioustools.curiousnews.AppButtonConfig.InternalIconConfig
import kotlin.collections.lastIndex
import kotlin.collections.map
import kotlin.collections.mapIndexed
import kotlin.ranges.coerceAtMost
import kotlin.to

@Preview
@Composable
fun ClearCacheBottomSheet(
    onDismiss: () -> Unit = {},
    onBottomSheetEvent: (AppCommonBottomSheetIntents) -> Unit = {}
) {
    val onClick = { b:Boolean ->
        if(b){onBottomSheetEvent.invoke(AppCommonBottomSheetIntents.OnCacheClearSelection)}
        onDismiss.invoke()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InternalIcon(config = InternalIconConfig.vector2(Icons.AutoMirrored.Default.Logout), iconTint = AppColors.black)
        TitleText(text = stringResource(R.string.clear_cache))
        SubtitleText(text = stringResource(R.string.clearn_cache_msg))
        AppPrimaryButton(modifier = Modifier.fillMaxWidth(), text = stringResource(R.string.word_continue)){onClick.invoke(true)}
        AppSecondaryButton(modifier = Modifier.fillMaxWidth(), text = stringResource(R.string.dismiss)){onClick.invoke(false)}

        Spacer(Modifier.fillMaxWidth().height(36.dp))
    }

}


@Preview
@Composable
fun SelectThemeSheet(
    sheetConfig: AppCommonBottomSheetType.SelectThemeBottomSheet = AppCommonBottomSheetType.SelectThemeBottomSheet(SharedPrefs.ThemeMode.SYSTEM),
    onDismiss: () -> Unit = {},
    onBottomSheetEvent: (AppCommonBottomSheetIntents) -> Unit = {}
) {
    val selected = sheetConfig.existingSelection
    val all = SharedPrefs.ThemeMode.entries

    val onClick = { it: SharedPrefs.ThemeMode ->
        onBottomSheetEvent.invoke(AppCommonBottomSheetIntents.OnThemeSelected(it))
        onDismiss.invoke()
    }
    val selectedText = when(selected){
        SharedPrefs.ThemeMode.LIGHT -> stringResource(R.string.msg_theme_light)
        SharedPrefs.ThemeMode.DARK -> stringResource(R.string.msg_theme_dark)
        SharedPrefs.ThemeMode.SYSTEM -> stringResource(R.string.msg_theme_auto)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InternalIcon(config = InternalIconConfig.vector2(Icons.Default.LightMode), iconTint = AppColors.black)
        TitleText(text = stringResource(R.string.change_theme))
        Row(Modifier.fillMaxWidth(0.65f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            all.map {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if(it == selected){
                        AppPrimaryButton(
                            leftIcon = InternalIconConfig.vector(it.icon()).copy(iconModifier = Modifier
                                .size(42.dp)
                                .padding(4.dp)),
                            onClick = {onClick.invoke(it)}
                        )
                    }
                    else{
                        AppSecondaryButton(
                            leftIcon = InternalIconConfig.vector(it.icon()).copy(iconModifier = Modifier
                                .size(42.dp)
                                .padding(4.dp)),
                            onClick = {onClick.invoke(it)}
                        )
                    }
                    Text(
                        text = it.name.toLangSpecific().capitaliseEachWord(),
                        style = textStyles().bodyRegularB,
                        textAlign = TextAlign.Center
                    )


                }
            }


        }
        SubtitleText2(selectedText)
        Spacer(Modifier
            .fillMaxWidth()
            .height(36.dp))
    }

}



@Composable
fun TitleText(text: String, modifier: Modifier = Modifier) {
    Text(text = text, style = textStyles().titleLarge, modifier = modifier.padding(horizontal = 16.dp), textAlign = TextAlign.Center)
}

@Composable
fun SubtitleText(text: String, modifier: Modifier = Modifier) {
    Text(text = text, style = textStyles().bodyRegular, modifier = modifier.padding(horizontal = 16.dp), textAlign = TextAlign.Center)
}
@Composable
fun SubtitleText2(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = textStyles().bodyRegular,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .background(colors().tertiaryContainer, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
        ,
        textAlign = TextAlign.Center
    )
}



@Preview
@Composable
fun BuildVersion(name: String = "1.2.3",code:String = "1234",isDebug:Boolean = true){
    val colors = MaterialTheme.colorScheme
    val fonts = MaterialTheme.localTypographyClass
    Row(Modifier
        .padding(vertical = 16.dp, horizontal = 8.dp)
        .fillMaxWidth()
        .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        WavyDivider(Modifier.weight(1f), color = colors.onSurface)
        Text(
            modifier = Modifier.padding(horizontal = 4.dp),
            text = "Build: $name(V$code) • ${if(isDebug)"Debug" else "Production"}",
            style = fonts.bodySmallB,
            color = colors.onSurface
        )
        WavyDivider(Modifier.weight(1f), color = colors.onSurface)

    }
}

@Preview
@Composable
fun SettingItems(modifier: Modifier = Modifier, state: DashboardState = DashboardState(), onClick: (DashboardIntent) -> Unit = {}){
    @Composable
    fun buildSettingItemList(state: DashboardState): MutableList<Pair<String, List<ActionModel>>> {
        val finalList = mutableListOf<Pair<String, List<ActionModel>>>()


        finalList.add(
            stringResource(R.string.settings) to listOf(
                ActionModel(
                    "",
                    "",
                    ActionModelType.CLEAR_CACHE_CTA_CLICKED,
                ),
                ActionModel("", "", ActionModelType.CHANGE_THEME_CTA_CLICKED),
            )
        )

        return finalList
    }

    val colors = MaterialTheme.colorScheme
    val fonts = MaterialTheme.localTypographyClass
    val finalList = buildSettingItemList(state)




    Column(modifier
        .fillMaxWidth()
        .padding(8.dp)) {

        finalList.map { (title,entries) ->
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                style = fonts.bodySmall,
                color = colors.onSurface
            )
            entries.mapIndexed { index, model ->
                val shape = HorizontalListGroupShape.fromList(entries,index)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .clickable { onClick.invoke(DashboardIntent.ActionClicked(model)) }
                        .padding(bottom = 1.dp)
                        .background(colors.tertiaryContainer, shape)
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                    ,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        color = colors.onSurface,
                        text = model.type.name.toLangSpecific() ,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        style = fonts.bodyRegular
                    )
                    InternalIcon(
                        config = AppButtonConfig.InternalIconConfig.vector(Icons.AutoMirrored.Default.ArrowForwardIos),
                        iconTint = colors.tertiary,
                        applyTint = true,
                        modifier = Modifier
                    )
                }
            }
            Spacer(Modifier
                .fillMaxWidth()
                .height(16.dp))
        }

    }

}





@Composable
fun WavyDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
    amplitude: Dp = 4.dp,      // how tall the wave is
    wavelength: Dp = 16.dp,    // length of one wave cycle
    strokeWidth: Dp = 2.dp,    // thickness of the line
    innerPadding: Dp = 0.dp    // optional horizontal inset inside the canvas
) {
    val density = LocalDensity.current
    val amplitudePx = with(density) { amplitude.toPx() }
    val wavelengthPx = with(density) { wavelength.toPx() }
    val strokeWidthPx = with(density) { strokeWidth.toPx() }
    val innerPaddingPx = with(density) { innerPadding.toPx() }

    // ensure we fill the width allocated (by weight) and have a fixed height
    Canvas(modifier
        .fillMaxWidth()
        .height(amplitude * 2 + strokeWidth)) {
        val path = Path()
        val centerY = size.height / 2f
        val strokeInset = strokeWidthPx / 2f + innerPaddingPx
        val totalWidth = size.width
        if (totalWidth <= strokeInset * 2f) return@Canvas
        var x = strokeInset
        path.moveTo(x, centerY)
        var up = true
        while (x < totalWidth - strokeInset) {
            val controlX = x + wavelengthPx / 2f
            val controlY = centerY + if (up) -amplitudePx else amplitudePx
            val endX = (x + wavelengthPx).coerceAtMost(totalWidth - strokeInset)
            val endY = centerY
            path.quadraticBezierTo(controlX.coerceAtMost(totalWidth - strokeInset), controlY, endX, endY)
            x += wavelengthPx
            up = !up
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Round
            )
        )
    }
}

object HorizontalListGroupShape{
    val top = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 4.dp,
        bottomEnd = 4.dp
    )

    val center = RoundedCornerShape(
        topStart = 4.dp,
        topEnd = 4.dp,
        bottomStart = 4.dp,
        bottomEnd = 4.dp
    )

    val allRounded = RoundedCornerShape(16.dp)

    val bottom = RoundedCornerShape(
        topStart = 4.dp,
        topEnd = 4.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )

    fun fromList(list:List<Any>,idx: Int): RoundedCornerShape {
        return if(list.size==1) allRounded else when(idx){
            0 -> top
            list.lastIndex -> bottom
            else -> center
        }
    }
}