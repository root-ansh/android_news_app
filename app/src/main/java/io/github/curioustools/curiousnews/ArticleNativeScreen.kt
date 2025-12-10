package io.github.curioustools.curiousnews

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ArticleNativeScreen(
    backStack: NavBackStack<NavKey>,
    viewModel: DashboardViewModel,
    currentItem: NewsResults.NewsItem
) {

    val scope = rememberCoroutineScope()
    val allPages = NavBarItem.entries

    val lifeCycleOwner = LocalLifecycleOwner.current
    val ctx = LocalContext.current
    val activity = LocalActivity.current
    var showBottomSheet by remember { mutableStateOf<AppCommonBottomSheetType?>(null) }
    var showLoader by remember { mutableStateOf(false) }
    var showSnackBar by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsStateWithLifecycle(lifeCycleOwner)
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        scope.launch {
            delay(100)
            log("test : calling init dashboard")
            viewModel.onIntent(DashboardIntent.OnRequestAllResults(AllResultsRequestType.FRESH))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.events.flowWithLifecycle(lifeCycleOwner.lifecycle).collect { event ->
            showLoader = false
            showSnackBar = ""
            showBottomSheet = null
            keyboardController?.hide()
            when (event) {
                AppCommonUiActions.DoNothing -> {}
                is AppCommonUiActions.LaunchUsingController -> event.callback.invoke(backStack)
                is AppCommonUiActions.LaunchComposableScreen -> backStack.add(event.route)
                is AppCommonUiActions.LaunchUsingActivity -> event.callback.invoke(activity)
                is AppCommonUiActions.LaunchUsingContext -> event.callback.invoke(ctx)
                is AppCommonUiActions.ShowBottomSheet -> showBottomSheet = event.type
                is AppCommonUiActions.ShowLoader -> showLoader = true
                is AppCommonUiActions.ShowSnackBar -> showSnackBar = event.message
                is AppCommonUiActions.ShowToast -> Toast.makeText(ctx, event.resId, event.duration)
                    .show()
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        ArticleNativeScreenUI(currentItem) { it: DashboardIntent -> viewModel.onIntent(it) }
        if (showLoader) GradientCircularProgressIndicator(Modifier.align(Alignment.Center))
        AnimatedSnackBarHost(showSnackBar) { showSnackBar = "" }
        showBottomSheet?.let {
            CommonBottomSheet(
                sheetType = it,
                onDismiss = { showBottomSheet = null },
                onSheetActions = { event -> viewModel.onBottomSheetIntent(event) }
            )
        }
    }

}

@Preview
@Composable
fun ArticleNativeScreenUI(
    article: NewsResults.NewsItem = NewsResults.mock().articles.first(),
    onClick: (DashboardIntent) -> Unit = {}
) {

    LazyColumn(verticalArrangement = Arrangement.Top, modifier = Modifier
        .fillMaxSize()
        .background(colors().background)) {
        item {
            Column(Modifier.fillMaxWidth()) {
                NewsIcon(
                    imageUrl = article.urlToImage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 80.dp)
                        .background(AppColors.orange_bright_ff8)
                    ,
                )
                AppToolbar(
                    title = stringResource(R.string.article_summary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                ) { onClick.invoke(DashboardIntent.ActionClicked(it)) }
            }
        }
        item {
            article.title.orEmpty().let {
                if (it.isNotBlank()) {
                    Text(text = it,
                        color = colors().onBackground,
                        style = textStyles().titleLarge,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 24.dp),
                    )
                    HorizontalDivider(Modifier.fillMaxWidth())
                }
            }
        }

        item {
            article.description.orEmpty().let {
                if (it.isNotBlank()) {
                    Text(text = it,
                        color = colors().onBackground,
                        style = textStyles().bodySmallB,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .alpha(0.5f),
                    )
                    HorizontalDivider(Modifier.fillMaxWidth())
                }
            }
        }
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                article.source.name.orEmpty().let {
                    if (it.isNotBlank()){
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = stringResource(R.string.from, it),
                            style = textStyles().bodyExtraSmall.copy(color = (AppColors.orange_bright_ff8)),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                if(article.source.name.isNotBlank() && article.publishedOn().isNotBlank()){
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = stringResource(R.string.bullet_ascii),
                        style = textStyles().bodyExtraSmall.copy(color = (AppColors.orange_bright_ff8)),
                        textAlign = TextAlign.Center
                    )
                }
                article.publishedOn().orEmpty().let {
                    if (it.isNotBlank()){
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = stringResource(R.string.published_on, it),
                            style = textStyles().bodyExtraSmall.copy(color = (AppColors.orange_bright_ff8)),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            HorizontalDivider(Modifier.fillMaxWidth())

        }
        items((1..10).map { article.content }.size){ content ->
            article.content.orEmpty().let {
                if (it.isNotBlank()) {
                    Text(text = it.take(it.length-16),
                        color = colors().onBackground,
                        style = textStyles().bodyRegular,
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                    )
                }
            }
        }
        item {
            article.content.let { if(it.isNotBlank()) HorizontalDivider(Modifier.fillMaxWidth()) }
        }

        item {
            var bookmark by remember { mutableStateOf(article.isBookmarked) }
            Row(Modifier
                .fillMaxWidth()
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                val shape = RoundedCornerShape(4.dp)
                AppLinkButton(
                    text = stringResource(R.string.share),
                    modifier = Modifier.weight(1f),
                    textModifier = Modifier,
                    shape = shape,
                    textStyle = textStyles().CTASmall,
                    onClick = {
                        onClick.invoke(
                            DashboardIntent.ActionClicked(
                                ActionModel(
                                    type = ActionModelType.SHARE_CTA_CLICKED,
                                    item = article
                                )
                            )
                        )
                    }
                )
                VerticalDivider(Modifier.height(12.dp))
                AppLinkButton(
                    text = stringResource(R.string.read_in_web),
                    modifier = Modifier.weight(1f),
                    textModifier = Modifier,
                    textStyle = textStyles().CTASmall,
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        onClick.invoke(
                            DashboardIntent.ActionClicked(
                                ActionModel(
                                    type = ActionModelType.URL,
                                    item = article,
                                    url = article.url.orEmpty()
                                )
                            )
                        )
                    }
                )
                Icon(
                    imageVector = when {
                        bookmark -> Icons.Default.Bookmark
                        else -> Icons.Default.BookmarkBorder
                    },
                    tint = colors().onBackground,
                    contentDescription = stringResource(R.string.bookmark_this_item),
                    modifier = Modifier
                        .clickable {
                            bookmark = bookmark.not()
                            onClick.invoke(
                                DashboardIntent.ActionClicked(
                                    ActionModel(
                                        type = ActionModelType.BOOKMARK,
                                        item = article,
                                        url = article.url.orEmpty()
                                    )
                                )
                            )
                        }
                )


            }
        }

        item { Spacer(Modifier.height(120.dp)) }
    }





}