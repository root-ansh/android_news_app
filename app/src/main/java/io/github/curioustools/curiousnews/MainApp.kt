package io.github.curioustools.curiousnews

import android.app.Application
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp
import io.github.curioustools.curiousnews.commons.isDebugApp
import timber.log.Timber


@HiltAndroidApp
class MainApp: Application() {

    override fun onCreate() {
        super.onCreate()
        if (isDebugApp()) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }
    }
}