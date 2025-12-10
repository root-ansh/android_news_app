package io.github.curioustools.curiousnews.presentation

import androidx.annotation.Keep
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.curioustools.curiousnews.presentation.AppButtonConfig.InternalIconConfig
import io.github.curioustools.curiousnews.R
import io.github.curioustools.curiousnews.commons.capitaliseEachWord
import io.github.curioustools.curiousnews.data.SharedPrefs
import io.github.curioustools.curiousnews.domain.BaseStatus
import io.github.curioustools.curiousnews.presentation.settings.SubtitleText
import io.github.curioustools.curiousnews.presentation.settings.SubtitleText2
import io.github.curioustools.curiousnews.presentation.settings.TitleText
import kotlinx.serialization.Serializable

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonBottomSheet(
    sheetType: AppCommonBottomSheetType,
    onDismiss: () -> Unit,
    onSheetActions:(AppCommonBottomSheetIntents)-> Unit
) {
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
                SelectThemeSheet(sheetType, onDismiss, onSheetActions)
            }
        }
    }
}


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
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InternalIcon(
            config = InternalIconConfig.vector2(Icons.AutoMirrored.Default.Logout),
            iconTint = AppColors.black
        )
        TitleText(text = stringResource(R.string.clear_cache))
        SubtitleText(text = stringResource(R.string.clearn_cache_msg))
        AppPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.word_continue)
        ) { onClick.invoke(true) }
        AppSecondaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.dismiss)
        ) { onClick.invoke(false) }

        Spacer(Modifier.fillMaxWidth().height(36.dp))
    }

}

@Preview
@Composable
fun SelectThemeSheet(
    sheetConfig: AppCommonBottomSheetType.SelectThemeBottomSheet = AppCommonBottomSheetType.SelectThemeBottomSheet(
        SharedPrefs.ThemeMode.SYSTEM),
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
        InternalIcon(
            config = InternalIconConfig.vector2(Icons.Default.LightMode),
            iconTint = AppColors.black
        )
        TitleText(text = stringResource(R.string.change_theme))
        Row(Modifier.fillMaxWidth(0.65f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            all.map {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if(it == selected){
                        AppPrimaryButton(
                            leftIcon = InternalIconConfig.vector(it.icon()).copy(
                                iconModifier = Modifier
                                    .size(42.dp)
                                    .padding(4.dp)
                            ),
                            onClick = { onClick.invoke(it) }
                        )
                    }
                    else{
                        AppSecondaryButton(
                            leftIcon = InternalIconConfig.vector(it.icon()).copy(
                                iconModifier = Modifier
                                    .size(42.dp)
                                    .padding(4.dp)
                            ),
                            onClick = { onClick.invoke(it) }
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
        horizontalAlignment = Alignment.CenterHorizontally,
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
            colorFilter = ColorFilter.tint(AppColors.orange_bright_ff8)
        )
        Text(
            text = sheetConfig.title,
            style = textStylesSystem().titleLarge,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Text(
            text = sheetConfig.subtitle,
            style = textStylesSystem().bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Text(
            text = "${sheetConfig.errorInfo.code} | ${sheetConfig.errorInfo.name}",
            style = textStylesSystem().bodyMedium,
            modifier = Modifier.Companion
                .padding(horizontal = 8.dp)
                .alpha(0.5f)
        )
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onDismiss.invoke() }
        ) {
            Text(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
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






