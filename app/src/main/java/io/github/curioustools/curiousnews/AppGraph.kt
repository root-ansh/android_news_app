package io.github.curioustools.curiousnews

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import kotlin.collections.removeLastOrNull

@Composable
fun AppGraph() {
    val backstack: NavBackStack<NavKey> = rememberNavBackStack(AppRoutes.Dashboard)
    val commonHandle = LocalViewModelStoreOwner.current
    NavDisplay(
        transitionSpec = { horizontalNextScreenAnimation() },
        popTransitionSpec = { horizontalBackPressAnimation() },
        predictivePopTransitionSpec = { horizontalBackPressAnimation() },
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AppRoutes.Dashboard>(
                content = {
                    val vm = if (commonHandle != null)
                        hiltViewModel<DashboardViewModel>(commonHandle)
                    else hiltViewModel<DashboardViewModel>()
                    DashboardScreen(backstack, vm)
                },
                metadata = emptyMap()
            )
            entry<AppRoutes.ArticleLoading>(
                content = {
                    val vm = if (commonHandle != null)
                        hiltViewModel<DashboardViewModel>(commonHandle)
                    else hiltViewModel<DashboardViewModel>()
                },
                metadata = emptyMap()
            )
            entry<AppRoutes.ArticleNative>(
                content = {
                    val vm = if (commonHandle != null)
                        hiltViewModel<DashboardViewModel>(commonHandle)
                    else hiltViewModel<DashboardViewModel>()
                    ArticleNativeScreen(backstack, vm, it.current)
                },
                metadata = emptyMap()
            )


        },

    )

}

sealed interface AppRoutes : NavKey {
    @Serializable data object Dashboard : AppRoutes
    @Serializable data class ArticleLoading(val current: String) : AppRoutes
    @Serializable data class ArticleNative(val current: NewsResults.NewsItem) : AppRoutes
}

fun horizontalBackPressAnimation(duration: Int = 500): ContentTransform {
    // Slide in from left when navigating back
    return slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(duration)
    ) togetherWith slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(duration))

}

fun horizontalNextScreenAnimation(duration: Int = 500): ContentTransform {
    // Slide in from right when navigating forward
    return slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(duration)
    ) togetherWith slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(duration))
}


