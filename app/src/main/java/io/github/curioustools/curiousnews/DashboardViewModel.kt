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
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val newsListUseCase: NewsListUseCase,
    private val searchUseCase: SearchUseCase,
    private val updateBookmarksUseCase: UpdateBookmarksUseCase,
    private val clarCacheUseCase: ClearCacheUseCase,
) : ViewModel(){

    private val _events = Channel<AppCommonUiActions>(Channel.Factory.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private var lastAllResultsRequest: AllResultsRequestType? = null

    fun onIntent(intent: DashboardIntent){
        when(intent){
            is DashboardIntent.OnRequestAllResults -> {
                if(lastAllResultsRequest!= AllResultsRequestType.FRESH) requestDashboard(intent.requestType)
            }
            is DashboardIntent.OnArticleSearchRequest ->requestSearch(intent.query)
            is DashboardIntent.ActionClicked -> {
                val actionModel = intent.quickLink
                when(actionModel.type){
                    CLEAR_CACHE_CTA_CLICKED ->  emitLaunchEffectEvent(scope = viewModelScope, event = ShowBottomSheet(ClearCacheBottomSheet))
                    CHANGE_THEME_CTA_CLICKED -> {
                        val theme = sharedPrefs.userSettings.themeType
                        emitLaunchEffectEvent(
                            scope = viewModelScope,
                            event = ShowBottomSheet(SelectThemeBottomSheet(theme)
                            )
                        )
                    }
                    BACK_CTA_CLICKED -> {
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
                    SHARE_CTA_CLICKED -> {
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
        }
    }
    fun onBottomSheetIntent(intent: AppCommonBottomSheetIntents){
        when(intent){
            is AppCommonBottomSheetIntents.OnThemeSelected -> {
                sharedPrefs.userSettings.themeType = intent.theme
            }
            AppCommonBottomSheetIntents.OnCacheClearSelection -> clearCacheAndBookmarks()
        }
    }

    private fun requestDashboard(requestType: AllResultsRequestType) {
        lastAllResultsRequest = requestType
        viewModelScope.launch {
            _state.update {
                if(requestType== AllResultsRequestType.PAGINATION) it.copy(allNewsPaginationLoading = true, allNewsLoading = false)
                else it.copy(allNewsLoading = true, allNewsPaginationLoading = false)
            }
            runCatching {
                val curRequest = NewsRequest.all(_state.value.allNewsRequest.pageNum+1)
                val data = newsListUseCase.executeAsync(NewsListUseCase.Params(curRequest,curRequest.pageNum==1))
                log("test : received result : items =   ${data.body.articles.size}")
                log("test : setting state")
                synchronized(this){//todo
                    _state.update { it.copy(
                        allNewsRequest = curRequest,
                        allNewsResults =  data.body,
                        allNewsLoading = false,
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

    private var searchJob: Job? = null

    private fun requestSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isEmpty()){
                _state.update { it.copy(allSearchRequest = NewsRequest.query(""), allSearchResults = NewsResults(), allSearchLoading = false, allSearchResultsPaginationLoading = false) }
                return@launch
            }
            delay(100)
            _state.update { it.copy(allSearchLoading = true) }
            runCatching {
                val curRequest = SearchUseCase.Params(NewsRequest.query(query), cachedList = _state.value.allNewsResults.articles)
                val data = searchUseCase.executeAsync(curRequest)
                if (this.isActive.not()) return@launch
                _state.update { it.copy(allSearchRequest = curRequest.request, allSearchResults =  data.body, allSearchLoading = false) }
                emitLaunchEffectEvent(DoNothing)
            }.getOrElse {
                _state.update { it.copy( allSearchLoading = false, allSearchResultsPaginationLoading = false) }
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
                    allNewsLoading = false,
                    allNewsPaginationLoading = false
                ) }
            }
        }
    }




    private fun clearCacheAndBookmarks() {
       viewModelScope.launch {
           clarCacheUseCase.executeAsync(Unit)
           val curRequest = NewsRequest.all(1)
           _state.update { it.copy(allNewsRequest = curRequest) }
           emitLaunchEffectEvent(ShowToast(R.string.cache_cleared),this)
           requestDashboard(AllResultsRequestType.FRESH_AFTER_CLEAR)
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
    data class OnRequestAllResults(val requestType: AllResultsRequestType): DashboardIntent
    data class OnArticleSearchRequest(val query: String): DashboardIntent
    data class ActionClicked(val quickLink: ActionModel):DashboardIntent
}

@Keep @Serializable @Immutable
enum class AllResultsRequestType{FRESH,RETRY,PAGINATION,FRESH_AFTER_CLEAR}

@Keep @Serializable @Immutable
data class DashboardState(
    val allNewsLoading: Boolean = true,
    val allNewsRequest:NewsRequest = NewsRequest.all(0),
    val allNewsResults: NewsResults = NewsResults(),
    val allNewsPaginationLoading: Boolean = false,

    val allSearchLoading: Boolean = false,
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
enum class ActionModelType{URL,CLEAR_CACHE_CTA_CLICKED,CHANGE_THEME_CTA_CLICKED,BACK_CTA_CLICKED,SHARE_CTA_CLICKED,BOOKMARK,OPEN_NATIVE}