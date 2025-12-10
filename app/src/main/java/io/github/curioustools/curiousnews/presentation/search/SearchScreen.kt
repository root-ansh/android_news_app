package io.github.curioustools.curiousnews.presentation.search

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.curioustools.curiousnews.presentation.AppToolbar
import io.github.curioustools.curiousnews.presentation.dashboard.DashboardIntent
import io.github.curioustools.curiousnews.presentation.dashboard.DashboardState
import io.github.curioustools.curiousnews.R
import io.github.curioustools.curiousnews.presentation.colors
import io.github.curioustools.curiousnews.presentation.headlines.NewsCard
import io.github.curioustools.curiousnews.presentation.textStyles

@Composable
fun SearchScreen(
    state: DashboardState, onClick: (DashboardIntent) -> Unit
){
    val entries = if(state.allSearchLoading)state.loadingResults else state.allSearchResults

    Column(
        Modifier.Companion
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        AppToolbar(
            title = stringResource(R.string.search_for_an_article), startIcon = null,
            titleStyle = textStyles().titleLarge,
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
                        text = stringResource(R.string.search_results, state.allSearchResults.totalResults, state.allSearchRequest.search)
                    )
                }
                if (state.allSearchResults.articles.isEmpty() && state.allSearchLoading.not()){
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val text = if (state.allSearchRequest.search.isEmpty()) stringResource(R.string.searc_new) else stringResource(
                            R.string.search_no_results)

                        val icon = if (state.allSearchRequest.search.isEmpty()) Icons.Default.EnergySavingsLeaf else Icons.Default.EmojiEmotions
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(16.dp)
                                .size(150.dp),
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
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        content = {
                            entries.articles.map {
                                NewsCard(
                                    pos = 0,
                                    item = it,
                                    isBookMarkScreen = false,
                                    isSearchScreen = true,
                                    onClick = onClick
                                )
                            }
                            Spacer(Modifier.size(120.dp))
                        }

                    )
                }





            }
        )
    }
}



