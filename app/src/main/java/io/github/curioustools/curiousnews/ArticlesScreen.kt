package io.github.curioustools.curiousnews

import androidx.annotation.Keep
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.google.gson.annotations.SerializedName
import io.github.curioustools.curiousnews.AppButtonConfig.AppButtonType.ROUND_PRIMARY
import io.github.curioustools.curiousnews.AppButtonConfig.InternalIconConfig
import io.github.curioustools.curiousnews.AppButtonConfig.InternalTextConfig
import java.text.SimpleDateFormat
import java.util.TimeZone

@Composable
fun ArticlesScreen(
    backStack: NavBackStack<NavKey>,
    dashboardViewModel: DashboardViewModel
){
    Column(
        Modifier.Companion
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        AppToolbar(
            title = stringResource(R.string.all_articles), startIcon = null,
            titleStyle = MaterialTheme.localTypographyClass.titleRegular,
        )

        Column(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .defaultMinSize(minHeight = 1000.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
            ,
            content = {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    NewsMetaItemList.mockLoading().articles.map {
                        NewsCard(it)
                    }
                }
            }
        )
    }
}


//bc45ff3e7dfe4362bb18a14ca3ad2c5b

@Keep
data class NewsItem(
    @SerializedName("author") val author: String = "",
    @SerializedName("content") val content: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("publishedAt") val publishedAt: String = "",
    @SerializedName("source") val source: Source = Source(),
    @SerializedName("title") val title: String = "",
    @SerializedName("url") val url: String = "",
    @SerializedName("urlToImage") val urlToImage: String = "",
) {
    fun publishedOn(): String {
        return runCatching {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("dd MMM yyyy, h:mm a")

            val date = inputFormat.parse(publishedAt)
            outputFormat.format(date!!)
        }.getOrElse { publishedAt }
    }

    fun info() = "From: ${source.name} • Published on: ${publishedOn()}"

    @Keep
    data class Source(
        @SerializedName("id") val id: Any? = null,
        @SerializedName("name") val name: String = ""
    )

    companion object{
        fun mock() =
            NewsItem(
                author = "Noah Whitman",
                source = Source(name = "Lifesciencesworld.com"),
                title = "Who did Albert Einstein say was the smartest person?",
                description = "Who Did Albert Einstein Say Was The Smartest Person? Unraveling the Mystery Albert Einstein, one of history’s most brilliant minds, reportedly considered Nikola Tesla the smartest person on Earth. While the exact quote remains elusive, its persistent circulat…",
                url = "https://www.lifesciencesworld.com/who-did-albert-einstein-say-was-the-smartest-person/",
                urlToImage = "https://d32r1sh890xpii.cloudfront.net/news/718x300/2025-12-09_l8ihjjsemb.jpg",
                publishedAt = "2025-12-09T07:31:35Z",
                content = "Albert Einstein, one of history’s most brilliant minds, reportedly considered Nikola Tesla the smartest person on Earth. While the exact quote remains elusive, its persistent circulation highlights t… [+8753 chars]"
            )
    }
}

data class NewsItemWithMeta(
    val item: NewsItem,
    val cachedId: String,
    val isLoading: Boolean,
    val isBookmarked: Boolean,
){
    val fallback = Icons.Default.ArtTrack
    companion object{
        fun mockItemBookmarked(book: Boolean,loading: Boolean) = NewsItemWithMeta(NewsItem.mock(),"1", isLoading = loading, isBookmarked = book)

        fun mockItemBookmarked(book: Boolean) = NewsItemWithMeta(NewsItem.mock(),"1", isLoading = false, isBookmarked = book)
        fun mockItemLoading() = NewsItemWithMeta(NewsItem.mock(),"1",isLoading = true, isBookmarked = false)
    }
}


data class NewsMetaItemList(
    val currentPageNum: Int = 0,
    val nextPageNum: Int = 1,
    val pageSize: Int = 50,
    val articles: List<NewsItemWithMeta> = listOf()
) {
    companion object {
        fun mockLoading() = NewsMetaItemList(
            articles = listOf(
                NewsItemWithMeta.mockItemBookmarked(true),
                NewsItemWithMeta.mockItemBookmarked(false),
                NewsItemWithMeta.mockItemBookmarked(false),
                NewsItemWithMeta.mockItemBookmarked(true),
                NewsItemWithMeta.mockItemLoading(),
                NewsItemWithMeta.mockItemLoading(),
                NewsItemWithMeta.mockItemLoading(),
            )
        )
    }
}

@Preview
@Composable
fun NewsCard(
    item: NewsItemWithMeta = NewsItemWithMeta.mockItemBookmarked(book = false, loading = false)
){
    Card(
        modifier = Modifier.fillMaxWidth()
    ){

        Column(Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if(item.isLoading){
                    ShimmerBox(Modifier.fillMaxWidth().weight(1f).height(36.dp).clip(RoundedCornerShape(16.dp)))
                    ShimmerBox(Modifier.size(16.dp).clip(CircleShape))
                    ShimmerBox(Modifier.size(16.dp).clip(CircleShape))
                }else{
                    Text(
                        text = item.item.title,
                        style = textStyles().bodyLarge,
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        textAlign = TextAlign.Start
                    )

                    Icon(
                        imageVector = if(item.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = stringResource(R.string.bookmark_this_item)
                    )
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share)
                    )
                }

            }

            Row(Modifier.fillMaxWidth(),verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (item.isLoading){
                    ShimmerBox(Modifier.size(120.dp,80.dp).clip(RoundedCornerShape(16.dp)))
                }
                else{
                    NewsIcon(
                        isLoading = item.isLoading,
                        isEnabled = false,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(120.dp,80.dp),
                        leftIcon = InternalIconConfig.vector(item.fallback).copy(iconModifier = Modifier.size(100.dp,80.dp)),
                    )
                }
                Column(Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    if(item.isLoading){
                        ShimmerBox(Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(16.dp)))
                    }else{
                        Text(text = item.item.description, style = textStyles().bodyRegular, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, maxLines = 3, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            HorizontalDivider(Modifier.fillMaxWidth())
            if (item.isLoading){
                ShimmerBox(Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(16.dp)))
            }else{
                Text(text = item.item.info(), style = textStyles().bodySmall)
            }



        }
    }

}




@Composable
fun NewsIcon(
    isLoading: Boolean,
    text: String? = null,
    modifier: Modifier = Modifier,
    shape: Shape  = RoundedCornerShape(50),
    leftIcon: InternalIconConfig? = null,
    rightIcon: InternalIconConfig? = null,
    isEnabled: Boolean = true,
    textTakesFullWidth: Boolean = false,
    iconTint: Boolean = true,
    textModifier: Modifier =   Modifier.padding(horizontal = 8.dp,vertical = 8.dp),
    onClick: () -> Unit = {},
) {
    val style = textStyles().CTALarge
    RawAppRoundButton(
        config = AppButtonConfig(
            text = text?.let {  InternalTextConfig.Text(
                text = text,
                modifier = textModifier,
                style = style,
                align = TextAlign.Center
            )},
            outerSurfaceModifier = modifier,
            innerRowModifier = Modifier.padding(8.dp, vertical = 8.dp),
            leftIcon = leftIcon,
            rightIcon  = rightIcon,
            shape = shape,
            iconTint = iconTint,
            type = ROUND_PRIMARY,
            isEnabled = isEnabled,

            innerRowArrangement = Arrangement.Center,
            textTakesFullWidth = textTakesFullWidth ,
        ),
        onclick = {onClick.invoke()}
    )
}
