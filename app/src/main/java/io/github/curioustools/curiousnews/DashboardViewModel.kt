package io.github.curioustools.curiousnews

import android.content.Intent
import android.net.Uri
import androidx.annotation.Keep
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.curioustools.curiousnews.ActionModelType.*
import io.github.curioustools.curiousnews.AppCommonBottomSheetType.*
import io.github.curioustools.curiousnews.AppCommonUiActions.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val newsListUseCase: NewsListUseCase,
    private val updateBookmarksUseCase: UpdateBookmarksUseCase
) : ViewModel(){



    private val _events = Channel<AppCommonUiActions>(Channel.Factory.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    fun onIntent(intent: DashboardIntent){
        when(intent){
            is DashboardIntent.ActionClicked -> handleLinks(intent.quickLink)
            is DashboardIntent.InitDashboard -> requestDashboard(intent.paginationCall)
            is DashboardIntent.OnArticleSearchRequest ->requestSearch(intent.query)
        }
    }


    private fun requestDashboard(isPagingCall: Boolean) {
        log("test : requestDashboard isPagingCall:$isPagingCall ")
        viewModelScope.launch {
            _state.update {
                log("test : setting state according to isPagingCall:$isPagingCall ")
                if(isPagingCall) it.copy(allNewsPaginationLoading = true, isAllNewsLoading = false)
                else it.copy(isAllNewsLoading = true, allNewsPaginationLoading = false)
            }
            runCatching {
                val curRequest = NewsRequest.all(_state.value.allNewsRequest.pageNum+1)
                val data = newsListUseCase.executeAsync(NewsListUseCase.Params(curRequest,curRequest.pageNum==1))
                log("test : received result : items =   ${data.body.articles.size}")
                log("test : setting state")
                synchronized(this){
                    _state.update { it.copy(
                        allNewsRequest = curRequest,
                        allNewsResults =  data.body,
                        isAllNewsLoading = false,
                        allNewsPaginationLoading = false
                    ) }
                }
                emitLaunchEffectEvent(DoNothing)
            }.getOrElse {
                log("test : something went wrong")
                it.printStackTrace()
            }
        }
    }
    private fun requestSearch(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()){
                _state.update { it.copy(allSearchRequest = NewsRequest.query(""), allSearchResults = NewsResults(), isAllSearchLoading = false) }
                return@launch
            }
            delay(100)
            _state.update { it.copy(isAllSearchLoading = true) }
            runCatching {
                val curRequest = NewsListUseCase.Params(NewsRequest.query(query), isSearch = true)
                val data = newsListUseCase.executeAsync(curRequest)
                when(data){
                    is BaseResponse.Success -> {
                        _state.update { it.copy(allSearchRequest = curRequest.request, allSearchResults =  data.body, isAllSearchLoading = false) }
                        emitLaunchEffectEvent(DoNothing)
                    }
                }
            }
        }
    }

    private fun handleBookmarkClick(item: NewsResults.NewsItem?) {
        item?:return
        viewModelScope.launch {
            updateBookmarksUseCase.executeAsync(item.copy(isBookmarked = item.isBookmarked.not()))
            val curRequest = NewsRequest.all()
            val data = newsListUseCase.executeAsync(NewsListUseCase.Params(curRequest, cachedOnly = true))
            synchronized(this){
                _state.update { it.copy(
                    allNewsRequest = curRequest,
                    allNewsResults =  data.body,
                    isAllNewsLoading = false,
                    allNewsPaginationLoading = false
                ) }
            }
        }
    }


    fun onBottomSheetIntent(intent: AppCommonBottomSheetIntents){
        when(intent){
            is AppCommonBottomSheetIntents.OnThemeSelected -> {
                sharedPrefs.userSettings.themeType = intent.theme
            }

            AppCommonBottomSheetIntents.OnCacheClearSelection -> {/*todo clear db*/}
        }
    }

    private fun handleLinks(actionModel: ActionModel) {
        when(actionModel.type){
            DEEPLINK_CLEAR_CACHE -> {
                emitLaunchEffectEvent(
                    scope = viewModelScope,
                    event = ShowBottomSheet(ClearCacheBottomSheet)
                )
            }
            DEEPLINK_CHANGE_THEME -> {
                val theme = sharedPrefs.userSettings.themeType
                emitLaunchEffectEvent(
                    scope = viewModelScope,
                    event = ShowBottomSheet(
                        SelectThemeBottomSheet(
                            theme
                        )
                    )
                )
            }
            BACK -> {
                emitLaunchEffectEvent(LaunchUsingController {
                    it.removeLastOrNull()
                })
            }

            URL ->{
                emitLaunchEffectEvent(LaunchUsingActivity { activity ->
                    if (activity != null) {
                        CustomTabsIntent.Builder().build().launchUrl(activity, Uri.parse(actionModel.url))
                    }
                }
                )
            }
            SHARE -> {
                if (actionModel.item!=null){
                    emitLaunchEffectEvent(LaunchUsingActivity{ act->
                        val intent = Intent(Intent.ACTION_SEND).also {
                            it.type = "text/plain"
                            it.putExtra(Intent.EXTRA_TEXT, actionModel.item.toShareMsg())
                        }
                        act?.startActivity(Intent.createChooser(intent, "Share via"))
                    })
                }

            }
            BOOKMARK -> handleBookmarkClick(actionModel.item)
            OPEN_NATIVE -> {
                emitLaunchEffectEvent(LaunchUsingController{
                    it.add(AppRoutes.ArticleNative(actionModel.item?.title.orEmpty()))
                })
            }
        }
    }



    private fun emitLaunchEffectEvent(event: AppCommonUiActions, scope: CoroutineScope =viewModelScope) {
        scope.launch { _events.send(event) }
    }



}




@Keep
@Serializable
@Immutable
sealed interface DashboardIntent{
    data class InitDashboard(val paginationCall: Boolean = false): DashboardIntent
    data class OnArticleSearchRequest(val query: String): DashboardIntent
    data class ActionClicked(val quickLink: ActionModel):DashboardIntent

}

@Keep @Serializable @Immutable
data class DashboardState(
    val isAllNewsLoading: Boolean = true,
    val allNewsRequest:NewsRequest = NewsRequest.all(0),
    val allNewsResults: NewsResults = NewsResults(),
    val allNewsPaginationLoading: Boolean = false,

    val isAllSearchLoading: Boolean = false,
    val allSearchRequest:NewsRequest = NewsRequest.query("",0),
    val allSearchResults: NewsResults = NewsResults(),
    val allSearchResultsPaginationLoading: Boolean = false,
    val loadingResults: NewsResults = NewsResults.loading()
){
    fun allBookmarks(): NewsResults {
        return allSearchResults.copy(articles = allNewsResults.articles.filter { it.isBookmarked })
    }
}

@Keep @Serializable @Immutable
data class ActionModel(
    val text:String = "", val url:String = "",
    val type: ActionModelType = ActionModelType.URL,
    val item: NewsResults.NewsItem? = null
)

@Keep @Serializable @Immutable
enum class ActionModelType{URL,DEEPLINK_CLEAR_CACHE,DEEPLINK_CHANGE_THEME,BACK,SHARE,BOOKMARK,OPEN_NATIVE}