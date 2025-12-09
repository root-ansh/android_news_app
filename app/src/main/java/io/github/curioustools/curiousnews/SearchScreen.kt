package io.github.curioustools.curiousnews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

@Composable
fun SearchScreen(
    backStack: NavBackStack<NavKey>,
    dashboardViewModel: DashboardViewModel
){
    Column(
        Modifier.Companion
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        AppToolbar(
            title = stringResource(R.string.search_for_an_article), startIcon = null,
            titleStyle = MaterialTheme.localTypographyClass.titleRegular,
        )

        Column(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .defaultMinSize(minHeight = 1000.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ),
            content = {}
        )
    }
}