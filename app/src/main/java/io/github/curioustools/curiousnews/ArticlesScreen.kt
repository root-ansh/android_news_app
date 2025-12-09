package io.github.curioustools.curiousnews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Delete
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
import io.github.curioustools.curiousnews.AppButtonConfig.AppButtonType.ROUND_PRIMARY
import io.github.curioustools.curiousnews.AppButtonConfig.InternalIconConfig
import io.github.curioustools.curiousnews.AppButtonConfig.InternalTextConfig

@Composable
fun ArticlesScreen(state: DashboardState, onClick: (DashboardIntent) -> Unit){
    val entries = if(state.isAllNewsLoading)state.loadingResults else state.allNewsResults
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {

        AppToolbar(
            title = stringResource(R.string.all_articles), startIcon = null,
            titleStyle = MaterialTheme.localTypographyClass.titleRegular,
        )

        Column(
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 1000.dp).background(color = MaterialTheme.colorScheme.background,                 shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ,
            content = {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    entries.articles.map { NewsCard(it) }
                    Spacer(Modifier.size(120.dp))
                }
            }
        )
    }
}

@Preview
@Composable
fun NewsCard(
    item: NewsResults.NewsItem = NewsResults.loading().articles.first(),
    isBookMarkScreen : Boolean = false,
    onClick: (NewsResults.NewsItem) -> Unit = {},
    onShare:  (NewsResults.NewsItem) -> Unit = {},
    onBookBark : (NewsResults.NewsItem, Boolean) -> Unit = {_,_ -> }
){
    Card(
        modifier = Modifier.fillMaxWidth().clickable{onClick.invoke(item)}
    ){

        Column(Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.End) {
                if(item.isLoading){
                    ShimmerBox(Modifier.size(16.dp).clip(CircleShape))
                    Spacer(Modifier.size(8.dp))
                    ShimmerBox(Modifier.size(16.dp).clip(CircleShape))
                }else{
                    Icon(
                        imageVector = when {
                            isBookMarkScreen -> Icons.Default.Delete
                            item.isBookmarked -> Icons.Default.Bookmark
                            else -> Icons.Default.BookmarkBorder
                        },
                        contentDescription = stringResource(R.string.bookmark_this_item),
                        modifier = Modifier.clickable{onBookBark.invoke(item,isBookMarkScreen)}
                    )
                    Spacer(Modifier.size(8.dp))
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share) ,
                        modifier = Modifier.clickable{onShare.invoke(item)}

                    )
                }

            }
            if(item.isLoading){
                ShimmerBox(Modifier.fillMaxWidth().height(36.dp).clip(RoundedCornerShape(16.dp)))
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
                    ShimmerBox(Modifier.size(120.dp,80.dp).clip(RoundedCornerShape(16.dp)))
                }
                else{
                    NewsIcon(
                        isLoading = item.isLoading,
                        isEnabled = false,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(120.dp,80.dp),
                        leftIcon = InternalIconConfig.vector(Icons.Default.ArtTrack).copy(iconModifier = Modifier.size(100.dp,80.dp)),
                    )
                }
                Column(Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    if(item.isLoading){
                        ShimmerBox(Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(16.dp)))
                    }else{
                        Text(text = item.description, style = textStyles().bodyRegular, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, maxLines = 3, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            HorizontalDivider(Modifier.fillMaxWidth())
            if (item.isLoading){
                ShimmerBox(Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(16.dp)))
            }else{
                Text(text = item.info(), style = textStyles().bodySmall)
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
