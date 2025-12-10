package io.github.curioustools.curiousnews.commons

import io.github.curioustools.curiousnews.BuildConfig
import timber.log.Timber

fun isDebugApp():Boolean{
    return BuildConfig.DEBUG
}

fun String.capitaliseEachWord(forceLowercaseFirst: Boolean = true): String {
    return this.split(" ").joinToString(" ") {
        val part = if (forceLowercaseFirst) it.lowercase() else it
        part.replaceFirstChar { c -> c.uppercase() }
    }
}
fun log(key: String, value: Any? = null, tag: String = "CUSTOM_LOGS") {
    val msg = if (value == null) key else "$key:$value"
    Timber.Forest.tag(tag).i(msg)
}