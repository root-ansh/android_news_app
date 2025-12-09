package io.github.curioustools.curiousnews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    state: DashboardState, onClick: (DashboardIntent) -> Unit
){
    val entries = if(state.isAllSearchLoading)state.loadingResults else state.allSearchResults

    Column(
        Modifier.Companion
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        AppToolbar(
            title = stringResource(R.string.search_for_an_article), startIcon = null,
            titleStyle = MaterialTheme.localTypographyClass.titleRegular,
        )

        Column(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .defaultMinSize(minHeight = 1000.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ),
            content = {
                SearchField(modifier = Modifier.padding(8.dp)) { str: String, b: Boolean ->
                    onClick.invoke(DashboardIntent.OnArticleSearchRequest(str))
                }

                if (state.allSearchRequest.search.isNotEmpty()){
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
                        style = textStyles().labelRegular,
                        color = colors().onSurface,
                        text = "${state.allSearchResults.totalResults} items available for your query '${state.allSearchRequest.search}'"
                    )
                }
                if (state.allSearchResults.articles.isEmpty() && state.isAllSearchLoading.not()){
                    Column(
                        Modifier.fillMaxWidth()
                            .padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val text = if (state.allSearchRequest.search.isEmpty()) "Enter something to start a search" else " No Results found"

                        val icon = if (state.allSearchRequest.search.isEmpty()) Icons.Default.EnergySavingsLeaf else Icons.Default.EmojiEmotions
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp).size(150.dp),
                            tint = colors().primary.copy(alpha = 0.5f)
                        )
                        Text(
                            color = colors().onSurface,
                            modifier = Modifier.padding( 8.dp),
                            style = textStyles().labelRegular,
                            text = text
                        )

                    }
                }else{
                    Column(Modifier
                        .fillMaxWidth()
                        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp),
                        content = {
                            entries.articles.map { NewsCard(it) }
                            Spacer(Modifier.size(120.dp))

                        }

                    )
                }





            }
        )
    }
}

@Preview
@Composable
fun SearchField(
    initialValue: String = "",
    selfFocus: FocusRequester? = null,
    nextFocus: FocusRequester?=null,
    debounceTime : Long  = 200L,
    modifier: Modifier = Modifier,
    onValueAvailable:(String, Boolean)-> Unit = { it, _ ->}
) {
    var currentValue by remember { mutableStateOf(initialValue) }
    val selfFocusRequestor = remember { selfFocus ?: FocusRequester() }
    val styles = textStyles()

    OutlinedTextField(
        value = currentValue,
        onValueChange = {
            currentValue = it
        },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (currentValue.isNotEmpty()) {
                IconButton(onClick = {
                    currentValue = ""
                    onValueAvailable.invoke("",true)
                }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = ""
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth().focusRequester(selfFocusRequestor),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { nextFocus?.requestFocus() }),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
        singleLine = true,
        label = { Text(text = stringResource(R.string.search), style = styles.labelRegular) },
        placeholder = { Text(text = stringResource(R.string.search), style = styles.bodyRegular) },
        supportingText = {
            Text(text = stringResource(R.string.search_by_article_title_subtitle_author_or_label), style = styles.labelRegular)
        }
    )


    LaunchedEffect(currentValue) {
        if (currentValue.length < 3) return@LaunchedEffect
        delay(debounceTime)
        onValueAvailable(currentValue,false)
    }
}

