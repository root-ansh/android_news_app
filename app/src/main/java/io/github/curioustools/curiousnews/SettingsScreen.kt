package io.github.curioustools.curiousnews

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    backStack: NavBackStack<NavKey>,
    dashboardViewModel: DashboardViewModel
) {

    val lifeCycleOwner = LocalLifecycleOwner.current
    val ctx = LocalContext.current
    val activity = LocalActivity.current
    var showBottomSheet by remember { mutableStateOf<AppCommonBottomSheetType?>(null) }
    var showLoader by remember { mutableStateOf(false) }
    var showSnackBar by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val state by dashboardViewModel.state.collectAsStateWithLifecycle(lifeCycleOwner)
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        scope.launch {
            delay(100)
            dashboardViewModel.onIntent(DashboardIntent.InitScreen)
        }
    }
    LaunchedEffect(Unit) {
        dashboardViewModel.events.flowWithLifecycle(lifeCycleOwner.lifecycle).collect { event ->
            showLoader = false
            showSnackBar = ""
            showBottomSheet = null
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

    Box(Modifier.Companion.fillMaxSize()) {
        SettingsScreenUI(
            state,
            onClick = { it: DashboardIntent -> dashboardViewModel.onIntent(it) })

        if (showLoader) GradientCircularProgressIndicator(Modifier.Companion.align(Alignment.Companion.Center))
        AnimatedSnackBarHost(showSnackBar) { showSnackBar = "" }
        showBottomSheet?.let {
            CommonBottomSheet(
                sheetType = it,
                onDismiss = { showBottomSheet = null },
                onSheetActions = { event -> dashboardViewModel.onBottomSheetIntent(event) }
            )
        }
    }
}

@Composable
fun SettingsScreenUI(state: DashboardState = DashboardState(), onClick: (DashboardIntent) -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    val fonts = MaterialTheme.localTypographyClass
    SafeColorColumn(
        statusBarColors = listOf(colors.secondaryContainer,),
        mainColors = listOf(colors.secondaryContainer),
        bottomBarColors = listOf(colors.onTertiary),
    ) {
        Column(Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(120.dp)
            ){
                Box(Modifier
                    .fillMaxSize()
                    .background(colors.onTertiary, CircleShape))
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .align(Alignment.Center)
                )
            }
            Text(
                text = stringResource(R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = colors.onSurface,
                textAlign = TextAlign.Center,
                style = fonts.titleRegular
            )
            BuildVersion(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE.toString(), isDebugApp())

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 800.dp)
                    .background(
                        color = colors.background,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ),
                content = {
                    SettingItems(state = state, onClick = onClick)
                    Spacer(Modifier
                        .fillMaxWidth()
                        .height(120.dp))
                }
            )
        }
    }

}