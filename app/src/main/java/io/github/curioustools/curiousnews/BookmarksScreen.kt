package io.github.curioustools.curiousnews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

@Composable
fun BookmarksScreen(
    state: DashboardState, onClick: (DashboardIntent) -> Unit
){
    val entries = if(state.isAllNewsLoading)state.loadingResults else state.allBookmarks()
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
            Spacer(Modifier
                .background(colors().background, RectangleShape)
                .fillMaxWidth()
                .height(800.dp))
        }
    }
}