package io.github.curioustools.curiousnews.presentation.headlines

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.curioustools.curiousnews.presentation.dashboard.AllResultsRequestType
import io.github.curioustools.curiousnews.presentation.AppSecondaryButton
import io.github.curioustools.curiousnews.presentation.AppToolbar
import io.github.curioustools.curiousnews.presentation.dashboard.DashboardIntent
import io.github.curioustools.curiousnews.presentation.dashboard.DashboardState
import io.github.curioustools.curiousnews.presentation.GradientCircularProgressIndicator
import io.github.curioustools.curiousnews.R
import io.github.curioustools.curiousnews.commons.log
import io.github.curioustools.curiousnews.presentation.colors
import io.github.curioustools.curiousnews.presentation.textStyles
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(FlowPreview::class)
@Composable
fun ArticlesScreen(state: DashboardState, onClick: (DashboardIntent) -> Unit){
    val entries = if(state.allNewsLoading)state.loadingResults else state.allNewsResults
    val listState = rememberLazyListState()

    LaunchedEffect(listState, entries.articles.size) {
        log("launched effect called")

        var previousLastVisibleIndex = -1

        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .distinctUntilChanged()
            .debounce(80L)
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == -1) return@collect
                val totalItems = entries.articles.size
                val isScrollingDown = lastVisibleIndex > previousLastVisibleIndex
                previousLastVisibleIndex = lastVisibleIndex

                if (!isScrollingDown) return@collect
                val isNearEnd = lastVisibleIndex >= totalItems - 3
                val canRequestMore = !state.allNewsPaginationLoading && !state.allNewsLoading && totalItems > 0
                log("pagination: can isNear End: $isNearEnd , canreq more : $canRequestMore")
                if (isNearEnd && canRequestMore) {
                    onClick(DashboardIntent.OnRequestAllResults(AllResultsRequestType.PAGINATION))
                }
            }
    }



    LazyColumn (Modifier,listState) {
        item {
            AppToolbar(
                title = stringResource(R.string.all_articles), startIcon = null,
                titleStyle = textStyles().titleLarge,
            )
        }

        items(entries.articles.size, key = { entries.articles[it].title}){ pos->
            NewsCard(
                pos = pos,
                item = entries.articles[pos],
                isBookMarkScreen = false,
                isSearchScreen = false,
                onClick = onClick
            )
        }
        if (state.allNewsPaginationLoading){
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors().background, RectangleShape)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ){
                    GradientCircularProgressIndicator(size = 36.dp)
                }
            }
        }

        if (state.allNewsPaginationLoading.not() && state.allNewsLoading.not() && state.allNewsResults.articles.isEmpty()){
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 300.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val text = if (state.allSearchRequest.search.isEmpty()) stringResource(R.string.searc_new) else stringResource(
                        R.string.search_no_results)
                    Icon(
                        imageVector = Icons.Default.EmojiEmotions,
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
                        text = stringResource(R.string.no_articles_found)
                    )
                    AppSecondaryButton(text = stringResource(R.string.retry)) {
                        onClick.invoke(
                            DashboardIntent.OnRequestAllResults(AllResultsRequestType.RETRY)
                        )
                    }

                }
            }
        }

        item {
            Spacer(
                Modifier.Companion
                .background(colors().background, RectangleShape)
                .fillMaxWidth()
                .height(120.dp))
        }
    }



}


