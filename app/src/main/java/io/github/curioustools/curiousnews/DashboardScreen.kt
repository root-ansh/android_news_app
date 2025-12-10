package io.github.curioustools.curiousnews

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Book
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.github.curioustools.curiousnews.NavBarItem.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun DashboardScreen(
    backStack: NavBackStack<NavKey>,
    viewModel: DashboardViewModel,
) {
    val scope = rememberCoroutineScope()
    val allPages = NavBarItem.entries
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { allPages.size })

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
            when(event){
                AppCommonUiActions.DoNothing -> {}
                is AppCommonUiActions.LaunchUsingController -> event.callback.invoke(backStack)
                is AppCommonUiActions.LaunchComposableScreen -> backStack.add(event.route)
                is AppCommonUiActions.LaunchUsingActivity -> event.callback.invoke(activity)
                is AppCommonUiActions.LaunchUsingContext -> event.callback.invoke(ctx)
                is AppCommonUiActions.ShowBottomSheet -> showBottomSheet = event.type
                is AppCommonUiActions.ShowLoader -> showLoader = true
                is AppCommonUiActions.ShowSnackBar -> showSnackBar = event.message
                is AppCommonUiActions.ShowToast -> Toast.makeText(ctx, event.resId, event.duration).show()
            }
        }
    }

    Box(Modifier.fillMaxSize()){
        SafeColorColumn(
            statusBarColors = listOf(colors().secondaryContainer,),
            mainColors = listOf(colors().secondaryContainer),
            bottomBarColors = listOf(colors().onTertiary),
        ){
            Box(modifier = Modifier.fillMaxSize()){
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = allPages.size,
                    userScrollEnabled = false
                ) { pos ->
                    val currentPage = allPages[pos]
                    when(currentPage){
                        Articles -> ArticlesScreen(state, onClick = {viewModel.onIntent(it)})
                        Bookmarks -> BookmarksScreen(state, onClick = {viewModel.onIntent(it)})
                        Search -> SearchScreen(state, onClick = {viewModel.onIntent(it)})
                        Settings -> SettingsScreen(backStack,viewModel)
                    }
                }
                CircleBottomBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart),
                    navBarItems = allPages,
                    currentSelectedPos = pagerState.currentPage,
                    onSelected = {scope.launch { pagerState.animateScrollToPage(it) }}
                )
            }
        }
        if (showLoader)  GradientCircularProgressIndicator(Modifier.align(Alignment.Center))
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



enum class NavBarItem(val unselectedVector: ImageVector, val selectedVector: ImageVector){
    Articles(Icons.Outlined.Book,Icons.Default.Book,),
    Search(Icons.Default.Search,Icons.Filled.Search,),
    Bookmarks(Icons.Default.BookmarkBorder,Icons.Default.Bookmark,),
    Settings(Icons.Default.PersonOutline,Icons.Default.Person);

    fun getTitle(context: Context): String{
        return when(this){
            Articles -> context.getString(R.string.articles)
            Bookmarks -> context.getString(R.string.bookmarks)
            Search -> context.getString(R.string.search)
            Settings -> context.getString(R.string.string_my_profile)
        }
    }
}







