package io.github.curioustools.curiousnews.presentation.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import io.github.curioustools.curiousnews.R
import io.github.curioustools.curiousnews.presentation.textStyles
import kotlinx.coroutines.delay

@Preview
@Composable
fun SearchField(
    initialValue: String = "",
    selfFocus: FocusRequester? = null,
    nextFocus: FocusRequester?=null,
    debounceTime : Long  = 200L,
    modifier: Modifier = Modifier.Companion,
    onValueAvailable:(String, Boolean)-> Unit = { it, _ ->}
) {
    var currentValue: String? by remember { mutableStateOf(initialValue) }
    val selfFocusRequestor = remember { selfFocus ?: FocusRequester() }
    val styles = textStyles()

    OutlinedTextField(
        value = currentValue.orEmpty(),
        onValueChange = {
            currentValue = it
        },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (currentValue.orEmpty().isNotEmpty()) {
                IconButton(onClick = {
                    currentValue = ""
                    onValueAvailable.invoke("", true)
                }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = ""
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(selfFocusRequestor),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { nextFocus?.requestFocus() }),
        shape = RoundedCornerShape(50),
        singleLine = true,
        label = { Text(text = stringResource(R.string.search), style = styles.labelRegular) },
        placeholder = { Text(text = stringResource(R.string.search), style = styles.bodyRegular) },
        supportingText = {
            Text(
                text = stringResource(R.string.search_by_article_title_subtitle_author_or_label),
                style = styles.labelRegular
            )
        }
    )


    LaunchedEffect(currentValue) {
        if (currentValue.orEmpty().length < 3) return@LaunchedEffect
        delay(debounceTime)
        onValueAvailable(currentValue.orEmpty(), false)
    }
}