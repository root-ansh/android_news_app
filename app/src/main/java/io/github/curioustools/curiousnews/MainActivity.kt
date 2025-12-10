package io.github.curioustools.curiousnews

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import dagger.hilt.android.AndroidEntryPoint
import io.github.curioustools.curiousnews.presentation.AppGraph
import io.github.curioustools.curiousnews.presentation.AppTheme
import io.github.curioustools.curiousnews.data.SharedPrefs
import io.github.curioustools.curiousnews.presentation.enableBackgroundControllableEdgeToEdge
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sharedPrefs: SharedPrefs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableBackgroundControllableEdgeToEdge()


        setContent {
            val themeSnapshot by produceState(
                initialValue = sharedPrefs.getCurrentThemeInfo(),
                key1 = sharedPrefs,
                producer ={
                    val callback = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                        if (sharedPrefs.isChangedKeyThemeKey(key)) {
                            value = sharedPrefs.getCurrentThemeInfo()
                        }
                    }
                    sharedPrefs.userSettings.registerListener(callback)
                    awaitDispose { sharedPrefs.userSettings.unregisterListener(callback) }
                }
            )

            AppTheme(
                themeType = themeSnapshot.first,
                dynamicColor = themeSnapshot.second,
                content = { AppGraph() }
            )
        }
    }
}

