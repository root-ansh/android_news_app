import java.io.FileInputStream
import java.util.Properties

plugins {
    //core
    alias(libs.plugins.com.android.application)

    //ksp
    alias(libs.plugins.com.google.devtools.ksp)

    //hilt
    alias(libs.plugins.com.google.dagger.hilt.android)

    //kotlin
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.compose)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.parcelize)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
}

android {
    namespace = libs.versions.appinfo.packagename.get()
    buildToolsVersion = libs.versions.appinfo.buildtools.get().toString()
    compileSdk { version = release(libs.versions.appinfo.targetSdk.get().toInt()) }

    defaultConfig {
        applicationId =  libs.versions.appinfo.packagename.get()
        minSdk = libs.versions.appinfo.minSdk.get().toInt()
        targetSdk= libs.versions.appinfo.targetSdk.get().toInt()
        versionCode = libs.versions.appinfo.versionCode.get().toInt()
        versionName = libs.versions.appinfo.versionName.get()
        testInstrumentationRunner = libs.versions.appinfo.targetRunner.get().toString()
        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
    }


    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true
    }
    val newsApiKey = project.findProperty("NEWS_API_KEY") as? String ?: error("put NEWS_API_KEY missing in gradle.properties!")

    buildTypes {
        debug {
            buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources =  false
        }
    }
    androidResources{
        @Suppress("UnstableApiUsage")
        generateLocaleConfig = true
    }


    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    bundle {
        language {
            @Suppress("UnstableApiUsage")
            enableSplit = false// info here: https://stackoverflow.com/a/52733674/7500651.  Specifies that the app bundle should not support configuration APKs for language resources. These resources are instead packaged with each base and dynamic feature APK.
        }
    }

    packaging { // this block is needed for for supporting new aws sdk
        resources {
            excludes += setOf("META-INF/INDEX.LIST", "META-INF/io.netty.versions.properties", "META-INF/DEPENDENCIES", "META-INF/LICENSE", "META-INF/LICENSE.txt", "META-INF/NOTICE", "META-INF/NOTICE.txt")
        }
    }

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.appinfo.jvm.get().toInt()))
    }
}
kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.appinfo.jvm.get().toInt())
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.generateKotlin", "true")
}



dependencies {
    implementation(fileTree("libs") { include("*.jar", "*.aar") })

    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.1.4")


    //testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //arch : core/ui
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.core)

    //arch : hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //arch : lifecycles
    implementation(libs.lifecycle.runtime.ktx)

    //arch : navigation
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.lifecycle.viewmodel)

    // arch : paging and compose
    implementation("androidx.paging:paging-runtime:3.3.6")
    testImplementation("androidx.paging:paging-common:3.3.6")
    implementation("androidx.paging:paging-compose:3.4.0-alpha04")
    implementation("androidx.room:room-runtime:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    implementation("androidx.room:room-paging:2.8.4")
    testImplementation("androidx.room:room-testing:2.8.4")



    //arch compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.activity)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.text)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.adaptive.navigation.suite)
    implementation(libs.compose.material3.navigation)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.text.google.fonts)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    implementation(libs.compose.hilt.navigation)
    implementation(libs.compose.navigation)
    ksp(libs.compose.hilt.compiler)

    // network(ui)
    implementation(libs.glide)
    ksp(libs.glide.ksp)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    //network
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation (libs.retrofit.converter.scalars)
    implementation(libs.stetho)
    implementation(libs.stetho.okhttp3)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation (libs.okhttp.urlconnection)
    implementation(libs.timber)


    //uilib
    implementation(libs.jsoup)
    implementation(libs.browser)
    implementation(libs.androidx.webkit)



}
configurations.all {
    exclude(group = "io.netty")
    exclude(group = "org.bouncycastle")
    exclude(group = "org.apache.logging.log4j")
    exclude(group = "org.apache.log4j")
    exclude(group = "javax.naming")
    exclude(group = "com.sun.xml") // for MSV
}
fun loadSigningConfig(signingConfig: com.android.build.api.dsl.SigningConfig, rootDir: File) {
    val props = Properties()
    try {
        val fileInputStream = FileInputStream(file("$rootDir/a_secure/app_keystore_keys.properties"))
        fileInputStream.use { props.load(it) }
        val keyStorePath = "$rootDir/a_secure/app_keystore.keystore"
        val keyStorePWD = props["KEYSTORE_PWD"] as String
        val key = props["KEY_ALIAS"] as String
        val keyPWD = props["KEY_ALIAS_PWD"] as String
        signingConfig.apply {
            storeFile = file(keyStorePath)
            storePassword = keyStorePWD
            keyAlias = key
            keyPassword = keyPWD
        }
        println(" applied signing using passwords for keystore located at `$keyStorePath`(`REDACTED`) and key: $key(`REDACTED`)")

    } catch (e: Exception) {
        println("Error loading properties: ${e.message}")
    }
}