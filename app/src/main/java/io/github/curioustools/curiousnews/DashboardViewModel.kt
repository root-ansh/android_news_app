package io.github.curioustools.curiousnews

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val articlesUseCase: ArticlesUseCase,
) : ViewModel(){



    private val _events = Channel<AppCommonUiActions>(Channel.Factory.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    fun onIntent(intent: DashboardIntent){
        when(intent){
            is DashboardIntent.QuickLinkClick -> handleLinks(intent.quickLink)
            DashboardIntent.InitDashboard -> requestDashboardAndBookmarks()
            is DashboardIntent.OnArticleSearchRequest ->requestSearchAndBookMarks(intent.query)
        }
    }

    private fun requestSearchAndBookMarks(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()){
                _state.update { it.copy(allSearchRequest = NewsRequest.query(""), allSearchResults = NewsResults(), isAllSearchLoading = false) }
                return@launch
            }
            delay(100)
            _state.update { it.copy(isAllSearchLoading = true) }
            runCatching {
                val curRequest = NewsRequest.query(query)
                val data = articlesUseCase.executeAsync(curRequest)
                when(data){
                    is BaseResponse.Failure -> {
                        _state.update { it.copy(allSearchRequest = curRequest, allSearchResults = NewsResults(), isAllSearchLoading = false) }

                    }
                    is BaseResponse.Success -> {
                        _state.update { it.copy(allSearchRequest = curRequest, allSearchResults =  data.body, isAllSearchLoading = false) }
                        emitLaunchEffectEvent(DoNothing)
                    }
                }
            }
        }
    }

    private fun requestDashboardAndBookmarks() {
        viewModelScope.launch {
            delay(100)
            _state.update { it.copy(isAllNewsLoading = true) }
            runCatching {
                val curRequest = NewsRequest.all()
                val data = articlesUseCase.executeAsync(curRequest)
                when(data){
                    is BaseResponse.Failure -> emitLaunchEffectEvent(ShowBottomSheet(ErrorSheet(data.status)), this)
                    is BaseResponse.Success -> {
                        _state.update { it.copy(allNewsRequest = curRequest, allNewsResults =  data.body, isAllNewsLoading = false) }
                        emitLaunchEffectEvent(DoNothing)
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

            AppCommonBottomSheetIntents.OnCacheClearSelection -> {/*todo clear db*/}
        }
    }

    private fun handleLinks(quickLink: ActionModel) {
        when(quickLink.type){
            ActionModelType.DEEPLINK_CLEAR_CACHE -> {
                emitLaunchEffectEvent(
                    scope = viewModelScope,
                    event = ShowBottomSheet(AppCommonBottomSheetType.ClearCacheBottomSheet)
                )
            }
            ActionModelType.DEEPLINK_CHANGE_THEME -> {
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
            ActionModelType.BACK -> {
                emitLaunchEffectEvent(LaunchUsingController {
                    it.removeLastOrNull()
                })
            }

            ActionModelType.URL -> TODO()
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
    data object InitDashboard: DashboardIntent
    data class OnArticleSearchRequest(val query: String): DashboardIntent
    data class QuickLinkClick(val quickLink: ActionModel):DashboardIntent

}

@Keep @Serializable @Immutable
data class DashboardState(
    val isAllNewsLoading: Boolean = true,
    val allNewsRequest:NewsRequest = NewsRequest.all(),
    val allNewsResults: NewsResults = NewsResults(),

    val isAllSearchLoading: Boolean = false,
    val allSearchRequest:NewsRequest = NewsRequest.query(""),
    val allSearchResults: NewsResults = NewsResults(),
    val allCacheResults: NewsResults = NewsResults(),

    val loadingResults: NewsResults = NewsResults.loading()

)


@Keep @Serializable @Immutable
data class ActionModel(
    val text:String = "",
    val url:String = "",
    val type: ActionModelType = ActionModelType.URL,
    val icon:Int = R.drawable.ic_launcher_foreground,
    val cardColor:Int = R.color.blue_v_light_f0ffff,
)

@Keep @Serializable @Immutable
enum class ActionModelType{URL,DEEPLINK_CLEAR_CACHE,DEEPLINK_CHANGE_THEME,BACK}