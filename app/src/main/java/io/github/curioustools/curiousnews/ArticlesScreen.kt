package io.github.curioustools.curiousnews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.curioustools.curiousnews.AppButtonConfig.AppButtonType.ROUND_PRIMARY
import io.github.curioustools.curiousnews.AppButtonConfig.InternalIconConfig
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(FlowPreview::class)
@Composable
fun ArticlesScreen(state: DashboardState, onClick: (DashboardIntent) -> Unit){
    val entries = if(state.isAllNewsLoading)state.loadingResults else state.allNewsResults
    val listState = rememberLazyListState()

    LaunchedEffect(listState, entries.articles.size) {
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
                val canRequestMore = !state.allNewsPaginationLoading && !state.isAllNewsLoading && totalItems > 0
                if (isNearEnd && canRequestMore) {
                    onClick(DashboardIntent.InitDashboard(paginationCall = true))
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

        item {
            Spacer(Modifier
                .background(colors().background, RectangleShape)
                .fillMaxWidth()
                .height(120.dp))
        }
    }
}

@Preview
@Composable
fun NewsCard(
    pos: Int =0,
    item: NewsResults.NewsItem = NewsResults.loading().articles.first(),
    isBookMarkScreen : Boolean = false,
    isSearchScreen: Boolean = false,
    onClick: (DashboardIntent) -> Unit = {},
){
    val bg = if (pos == 0)  RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp) else RectangleShape
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors().background, bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
        content = {
            Card(
                modifier = Modifier.fillMaxWidth()
            ){
                Column(Modifier
                    .fillMaxWidth()
                    .padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if(item.isLoading){
                        ShimmerBox(Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .clip(RoundedCornerShape(16.dp)))
                    }else{
                        Text(
                            text = item.title,
                            style = textStyles().bodyRegularB,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(Modifier.fillMaxWidth(),verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (item.isLoading){
                            ShimmerBox(Modifier
                                .size(120.dp, 80.dp)
                                .clip(RoundedCornerShape(16.dp)))
                        }
                        else{
                            NewsIcon(
                                imageUrl = item.urlToImage,
                                modifier = Modifier.size(120.dp,80.dp).background(AppColors.orange_bright_ff8, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)),

                            )
                        }
                        Column(Modifier
                            .fillMaxWidth()
                            .weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if(item.isLoading){
                                ShimmerBox(Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(16.dp)))
                            }else{
                                Text(text = item.description, style = textStyles().bodySmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, maxLines = 3, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                    if (item.isLoading){
                        ShimmerBox(Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(RoundedCornerShape(16.dp)))
                    }else{
                        Text(text = item.info(), style = textStyles().bodySmall)
                    }
                    HorizontalDivider(Modifier.fillMaxWidth())



                    Row(Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically, ) {
                        val shape = RoundedCornerShape(4.dp)
                        if(item.isLoading){
                            ShimmerBox(Modifier
                                .padding(horizontal = 4.dp)
                                .height(24.dp)
                                .weight(1f)
                                .clip(shape))
                        }else{
                            AppLinkButton(
                                text = "Share",
                                modifier = Modifier.weight(1f),
                                textModifier = Modifier,
                                shape = shape,
                                textStyle = textStyles().CTASmall,
                                onClick = {
                                    onClick.invoke(
                                        DashboardIntent.ActionClicked(
                                            ActionModel(
                                                type = ActionModelType.SHARE,
                                                item = item
                                            )
                                        )
                                    )
                                }
                            )
                        }
                        VerticalDivider(Modifier.height(12.dp))

                        if(item.isLoading){
                            ShimmerBox(Modifier
                                .padding(horizontal = 4.dp)
                                .height(24.dp)
                                .weight(1f)
                                .clip(shape))
                        }
                        else{
                            AppLinkButton(
                                text = "Read Summary",
                                modifier = Modifier.weight(1f),
                                textModifier = Modifier,
                                textStyle = textStyles().CTASmall,
                                shape = shape,
                                onClick = {
                                    onClick.invoke(
                                        DashboardIntent.ActionClicked(
                                            ActionModel(
                                                type = ActionModelType.OPEN_NATIVE,
                                                item = item
                                            )
                                        )
                                    )
                                }
                            )
                        }

                        VerticalDivider(Modifier.height(12.dp))
                        if(item.isLoading){
                            ShimmerBox(Modifier
                                .padding(horizontal = 4.dp)
                                .height(24.dp)
                                .weight(1f)
                                .clip(shape))
                        }
                        else {
                            AppLinkButton(
                                text = "Read in web",
                                modifier = Modifier.weight(1f),
                                textModifier = Modifier,
                                textStyle = textStyles().CTASmall,
                                shape = RoundedCornerShape(4.dp),
                                onClick = {
                                    onClick.invoke(
                                        DashboardIntent.ActionClicked(
                                            ActionModel(
                                                type = ActionModelType.URL,
                                                item = item,
                                                url = item.url.orEmpty()
                                            )
                                        )
                                    )
                                }
                            )
                        }

                        if (item.isLoading){
                            ShimmerBox(Modifier
                                .size(16.dp)
                                .clip(CircleShape))

                        }else{

                            Icon(
                                imageVector = when {
                                    isBookMarkScreen -> Icons.Default.Delete
                                    item.isBookmarked -> Icons.Default.Bookmark
                                    else -> Icons.Default.BookmarkBorder
                                },
                                contentDescription = stringResource(R.string.bookmark_this_item),
                                modifier =  Modifier.clickable(enabled = !isSearchScreen){
                                    onClick.invoke(
                                        DashboardIntent.ActionClicked(
                                            ActionModel(
                                                type = ActionModelType.BOOKMARK,
                                                item = item,
                                                url = item.url.orEmpty()
                                            )
                                        )
                                    )
                                }
                            )
                        }



                    }



                }
            }
        }

    )


}


@Composable
fun NewsIcon(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    fallbackRes: ImageVector = Icons.Default.ArtTrack,
) {
    var hasError by remember { mutableStateOf(false) }

    if (imageUrl.isNullOrBlank() || hasError) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Image(
                modifier = Modifier.fillMaxSize().padding(4.dp),
                imageVector = fallbackRes,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(AppColors.white),
            )
        }

    } else {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifier,
            onError = { hasError = true }
        )
    }
}