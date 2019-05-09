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
            setMinifyEnabled(false)
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

    implementation(Dependencies.kotlin.stdlib)
    implementation(Dependencies.support.app_compat)

    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.assertj)
    androidTestImplementation(Dependencies.assertj)

    // javax.inject
    implementation(Dependencies.javax_inject)
    // RxJava
    implementation(Dependencies.rxjava2)
    // OkHttp
    implementation(Dependencies.okhttp3.runtime)
    implementation(Dependencies.okhttp3.logging_interceptor)
    testImplementation(Dependencies.okhttp3.mockwebserver)
    // retrofit
    implementation(Dependencies.retrofit2.runtime)
    implementation(Dependencies.retrofit2.adapter_rxjava2) {
        exclude(group = "io.reactivex.rxjava2", module = "rxjava")
    }
    implementation(Dependencies.retrofit2.converter_simplexml) {
        exclude(group = "xpp3", module = "xpp3")
        exclude(group = "stax", module = "stax-api")
        exclude(group = "stax", module = "stax")
    }

    androidTestImplementation(Dependencies.support_test.runner)
    androidTestImplementation(Dependencies.support_test.espresso_core)
}
