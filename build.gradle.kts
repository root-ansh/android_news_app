// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    //core
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.com.android.library) apply false

    //ksp
    alias(libs.plugins.com.google.devtools.ksp) apply false

    //hilt
    alias(libs.plugins.com.google.dagger.hilt.android) apply false

    //kotlin
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.org.jetbrains.kotlin.plugin.compose) apply false
    alias(libs.plugins.org.jetbrains.kotlin.plugin.parcelize) apply false
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization) apply false

}