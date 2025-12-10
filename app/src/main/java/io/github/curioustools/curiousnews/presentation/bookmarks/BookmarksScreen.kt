package io.github.curioustools.curiousnews.presentation.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.curioustools.curiousnews.presentation.AppToolbar
import io.github.curioustools.curiousnews.presentation.dashboard.DashboardIntent
import io.github.curioustools.curiousnews.presentation.dashboard.DashboardState
import io.github.curioustools.curiousnews.presentation.GradientCircularProgressIndicator
import io.github.curioustools.curiousnews.R
import io.github.curioustools.curiousnews.presentation.colors
import io.github.curioustools.curiousnews.presentation.headlines.NewsCard
import io.github.curioustools.curiousnews.presentation.textStyles

@Composable
fun BookmarksScreen(
    state: DashboardState, onClick: (DashboardIntent) -> Unit
){
    val entries = if(state.allNewsLoading)state.loadingResults else state.allBookmarks()
    val listState = rememberLazyListState()

    LazyColumn (Modifier,listState) {
        item {
            AppToolbar(
                title = stringResource(R.string.your_saved_bookmarks), startIcon = null,
                titleStyle = textStyles().titleLarge,
            )
        }

        items(entries.articles.size, key = { entries.articles[it].title}){ pos->
            NewsCard(
                pos = pos,
                item = entries.articles[pos],
                isBookMarkScreen = true,
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

        item {
            Spacer(
                Modifier.Companion
                .background(colors().background, RectangleShape)
                .fillMaxWidth()
                .height(800.dp))
        }
    }
}