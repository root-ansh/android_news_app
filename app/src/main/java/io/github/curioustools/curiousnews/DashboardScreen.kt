package io.github.curioustools.curiousnews

import android.content.Context
import androidx.annotation.Keep
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.curioustools.curiousnews.ActionModelType.*
import io.github.curioustools.curiousnews.AppCommonUiActions.*
import io.github.curioustools.curiousnews.NavBarItem.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject


@Composable
fun DashboardScreen(
    backStack: NavBackStack<NavKey>,
    viewModel: DashboardViewModel,
) {
    val scope = rememberCoroutineScope()
    val allPages = NavBarItem.entries
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { allPages.size })

    SafeColorColumn(
        statusBarColors = listOf(colors().secondaryContainer,),
        mainColors = listOf(colors().secondaryContainer),
        bottomBarColors = listOf(colors().onTertiary),
    ){
        Box(modifier = Modifier.fillMaxSize()){
            DashboardPager(
                modifier = Modifier.fillMaxSize(),
                pagerState = pagerState,
                pages = allPages.toList(),
                backStack = backStack,
                viewModel = viewModel,
            )
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




}

@Composable
fun DashboardPager(
    modifier: Modifier,
    pagerState: PagerState,
    pages: List<NavBarItem>,
    backStack: NavBackStack<NavKey>,
    viewModel: DashboardViewModel,
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        beyondViewportPageCount = pages.size,
        userScrollEnabled = false
    ) { pos ->
        val currentPage = pages[pos]
        when(currentPage){
            Articles -> ArticlesScreen(backStack,viewModel)
            Bookmarks -> BookmarksScreen(backStack,viewModel)
            Search -> SearchScreen(backStack,viewModel)
            Settings -> SettingsScreen(backStack,viewModel)
        }
    }
}



enum class NavBarItem(val unselectedVector: ImageVector, val selectedVector: ImageVector){
    Articles(Icons.Outlined.Book,Icons.Default.Book,),
    Bookmarks(Icons.Default.BookmarkBorder,Icons.Default.Bookmark,),
    Search(Icons.Default.Search,Icons.Filled.Search,),
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



@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefs,
) :ViewModel(){



    private val _events = Channel<AppCommonUiActions>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()



    fun onIntent(intent: DashboardIntent){
        when(intent){

            DashboardIntent.AuthTouchPointClicked -> {}
            DashboardIntent.InitScreen -> {}
            is DashboardIntent.QuickLinkClick -> handleLinks(intent.quickLink)
            DashboardIntent.UpdateAppClick -> {}
            DashboardIntent.UpdateDownloadedClick -> {}
        }
    }

    fun onBottomSheetIntent(intent: AppCommonBottomSheetIntents){
        when(intent){
            is AppCommonBottomSheetIntents.OnSendFeedBack -> {
                emitLaunchEffectEvent(ShowSnackBar("Thank you for your feedback!"))
            }

            is AppCommonBottomSheetIntents.OnLanguageSelected -> {
                emitLaunchEffectEvent(LaunchUsingActivity{ AppLanguages.setSelectedLanguage(intent.lang) })
            }

            AppCommonBottomSheetIntents.OnLogout -> {
                sharedPrefs.profileSettings.email = "User"
                sharedPrefs.profileSettings.isLoggedIn =false
                _state.update {
                    it.copy(
                        isLoggedIn = false,
                        profileEmail = "User",
                    )
                }
            }
            is AppCommonBottomSheetIntents.OnThemeSelected -> {
                sharedPrefs.userSettings.themeType = intent.theme

            }
        }
    }

    private fun handleLinks(quickLink: ActionModel) {
        when(quickLink.type){
            DEEPLINK_FEEDBACK -> {
                emitLaunchEffectEvent(
                    scope = viewModelScope,
                    event = ShowBottomSheet(AppCommonBottomSheetType.FeedbackBottomSheet)
                )
            }
            DEEPLINK_CLEAR_CACHE -> {
                emitLaunchEffectEvent(
                    scope = viewModelScope,
                    event = ShowBottomSheet(AppCommonBottomSheetType.SelectLanguageBottomSheet)
                )
            }
            DEEPLINK_CHANGE_THEME -> {
                val theme = sharedPrefs.userSettings.themeType
                emitLaunchEffectEvent(
                    scope = viewModelScope,
                    event = ShowBottomSheet(AppCommonBottomSheetType.SelectThemeBottomSheet(theme))
                )
            }
            SETTINGS_LOGOUT ->{
                emitLaunchEffectEvent(
                    scope = viewModelScope,
                    event = ShowBottomSheet(AppCommonBottomSheetType.LogoutSheet)
                )
            }
            BACK -> {
                emitLaunchEffectEvent(LaunchUsingController{
                    it.removeLastOrNull()
                })
            }
            else -> emitLaunchEffectEvent(DoNothing)
        }
    }


    private fun emitLaunchEffectEvent(event: AppCommonUiActions, scope: CoroutineScope=viewModelScope) {
        scope.launch{ _events.send(event) }
    }



}



@Keep
@Serializable
@Immutable
sealed interface DashboardIntent{
    data object InitScreen: DashboardIntent
    data object UpdateAppClick: DashboardIntent
    data object UpdateDownloadedClick: DashboardIntent
    data object AuthTouchPointClicked : DashboardIntent
    data class QuickLinkClick(val quickLink: ActionModel):DashboardIntent

}

@Keep
@Serializable
@Immutable
data class DashboardState(
    val isLoggedIn: Boolean = false,
    val profileEmail: String = "",
    val showUpdateSection: Boolean = false,
)


@Keep
@Serializable
@Immutable
data class ActionModel(
    val text:String = "",
    val url:String = "",
    val type: ActionModelType = ActionModelType.URL,
    val icon:Int = R.drawable.ic_launcher_foreground,
    val cardColor:Int = R.color.blue_v_light_f0ffff,
)

@Keep
@Serializable
@Immutable
enum class ActionModelType{URL,DEEPLINK_FEEDBACK,DEEPLINK_CLEAR_CACHE,DEEPLINK_CHANGE_THEME,BACK,SETTINGS_LOGOUT}





