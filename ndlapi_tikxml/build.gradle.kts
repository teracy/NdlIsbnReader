plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":ndlapi"))

    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar")))

    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.KotlinxCoroutines.core)
    implementation(Dependencies.KotlinxCoroutines.android)

    implementation(Dependencies.Support.appCompat)

    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.assertj)
    androidTestImplementation(Dependencies.assertj)

    // javax.inject
    implementation(Dependencies.javaxInject)
    // OkHttp
    implementation(Dependencies.Okhttp3.runtime)
    implementation(Dependencies.Okhttp3.loggingInterceptor)
    testImplementation(Dependencies.Okhttp3.mockwebserver)
    // retrofit
    implementation(Dependencies.Retrofit2.runtime)
    implementation(Dependencies.Retrofit2.kotlinCoroutinesAdapter)
    // TikXML
    implementation(Dependencies.Tikxml.annotation)
    implementation(Dependencies.Tikxml.core)
    implementation(Dependencies.Tikxml.retrofitConverter)
    kapt(Dependencies.Tikxml.processor)

    androidTestImplementation(Dependencies.SupportTest.runner)
    androidTestImplementation(Dependencies.SupportTest.espressoCore)
}
