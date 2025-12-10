package io.github.curioustools.curiousnews.presentation.headlines

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
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
import io.github.curioustools.curiousnews.presentation.dashboard.ActionModel
import io.github.curioustools.curiousnews.presentation.dashboard.ActionModelType
import io.github.curioustools.curiousnews.presentation.AppColors
import io.github.curioustools.curiousnews.presentation.AppLinkButton
import io.github.curioustools.curiousnews.presentation.dashboard.DashboardIntent
import io.github.curioustools.curiousnews.R
import io.github.curioustools.curiousnews.presentation.ShimmerBox
import io.github.curioustools.curiousnews.presentation.colors
import io.github.curioustools.curiousnews.domain.dto.NewsResults
import io.github.curioustools.curiousnews.presentation.textStyles

@Preview
@Composable
fun NewsCard(
    pos: Int =0,
    item: NewsResults.NewsItem = NewsResults.loading().articles.first(),
    isBookMarkScreen : Boolean = false,
    isSearchScreen: Boolean = false,
    onClick: (DashboardIntent) -> Unit = {},
){
    val bg = if (pos == 0) RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp) else RectangleShape
    Box(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .background(colors().background, bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
        content = {
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    onClick.invoke(
                        DashboardIntent.ActionClicked(
                            ActionModel(
                                type = ActionModelType.OPEN_NATIVE,
                                item = item
                            )
                        )
                    )
                }
            ) {
                Column(
                    Modifier.Companion
                        .fillMaxWidth()
                        .padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (item.isLoading) {
                        ShimmerBox(
                            Modifier.Companion
                                .fillMaxWidth()
                                .height(36.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        )
                    } else {
                        Text(
                            text = item.title,
                            style = textStyles().bodyRegularB,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (item.isLoading) {
                            ShimmerBox(
                                Modifier.Companion
                                    .size(120.dp, 80.dp)
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                            )
                        } else {
                            NewsIcon(
                                imageUrl = item.urlToImage,
                                modifier = Modifier.Companion
                                    .size(120.dp, 80.dp)
                                    .background(
                                        AppColors.orange_bright_ff8,
                                        androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                                    )
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),

                                )
                        }
                        Column(
                            Modifier.Companion
                                .fillMaxWidth()
                                .weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (item.isLoading) {
                                ShimmerBox(
                                    Modifier.Companion
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clip(
                                            androidx.compose.foundation.shape.RoundedCornerShape(
                                                16.dp
                                            )
                                        )
                                )
                            } else {
                                Text(
                                    text = item.description.orEmpty(),
                                    style = textStyles().bodySmall,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start,
                                    maxLines = 6,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    if (item.isLoading) {
                        ShimmerBox(
                            Modifier.Companion
                                .fillMaxWidth()
                                .height(24.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        )
                    } else {
                        Text(text = item.info(), style = textStyles().bodySmall)
                    }
                    HorizontalDivider(Modifier.fillMaxWidth())



                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                        if (item.isLoading) {
                            ShimmerBox(
                                Modifier.Companion
                                    .padding(horizontal = 4.dp)
                                    .height(24.dp)
                                    .weight(1f)
                                    .clip(shape)
                            )
                        } else {
                            AppLinkButton(
                                text = stringResource(R.string.share),
                                modifier = Modifier.weight(1f),
                                textModifier = Modifier.Companion,
                                shape = shape,
                                textStyle = textStyles().CTASmall,
                                onClick = {
                                    onClick.invoke(
                                        DashboardIntent.ActionClicked(
                                            ActionModel(
                                                type = ActionModelType.SHARE_CTA_CLICKED,
                                                item = item
                                            )
                                        )
                                    )
                                }
                            )
                        }
                        VerticalDivider(Modifier.height(12.dp))

                        if (item.isLoading) {
                            ShimmerBox(
                                Modifier.Companion
                                    .padding(horizontal = 4.dp)
                                    .height(24.dp)
                                    .weight(1f)
                                    .clip(shape)
                            )
                        } else {
                            AppLinkButton(
                                text = stringResource(R.string.read_summary),
                                modifier = Modifier.weight(1f),
                                textModifier = Modifier.Companion,
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
                        if (item.isLoading) {
                            ShimmerBox(
                                Modifier.Companion
                                    .padding(horizontal = 4.dp)
                                    .height(24.dp)
                                    .weight(1f)
                                    .clip(shape)
                            )
                        } else {
                            AppLinkButton(
                                text = stringResource(R.string.read_in_web),
                                modifier = Modifier.weight(1f),
                                textModifier = Modifier.Companion,
                                textStyle = textStyles().CTASmall,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
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

                        if (item.isLoading) {
                            ShimmerBox(
                                Modifier.Companion
                                    .size(16.dp)
                                    .clip(CircleShape)
                            )

                        } else {

                            Icon(
                                imageVector = when {
                                    isBookMarkScreen -> Icons.Default.Delete
                                    isSearchScreen -> Icons.Default.BookmarkAdded
                                    item.isBookmarked -> Icons.Default.Bookmark
                                    else -> Icons.Default.BookmarkBorder
                                },
                                contentDescription = stringResource(R.string.bookmark_this_item),
                                modifier = Modifier.alpha(if (isSearchScreen && item.isBookmarked.not()) 0f else 1f)
                                    .clickable(enabled = !isSearchScreen) {
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
    modifier: Modifier = Modifier.Companion,
    fallbackRes: ImageVector = Icons.Default.ArtTrack,
) {
    var hasError by remember { mutableStateOf(false) }

    if (imageUrl.isNullOrBlank() || hasError) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Image(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .padding(4.dp),
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