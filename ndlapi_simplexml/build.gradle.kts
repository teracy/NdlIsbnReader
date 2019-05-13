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

    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Support.appCompat)

    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.assertj)
    androidTestImplementation(Dependencies.assertj)

    // javax.inject
    implementation(Dependencies.javaxInject)
    // RxJava
    implementation(Dependencies.rxjava2)
    // OkHttp
    implementation(Dependencies.Okhttp3.runtime)
    implementation(Dependencies.Okhttp3.loggingInterceptor)
    testImplementation(Dependencies.Okhttp3.mockwebserver)
    // retrofit
    implementation(Dependencies.Retrofit2.runtime)
    implementation(Dependencies.Retrofit2.adapterRxjava2) {
        exclude(group = "io.reactivex.rxjava2", module = "rxjava")
    }
    implementation(Dependencies.Retrofit2.converterSimplexml) {
        exclude(group = "xpp3", module = "xpp3")
        exclude(group = "stax", module = "stax-api")
        exclude(group = "stax", module = "stax")
    }

    androidTestImplementation(Dependencies.SupportTest.runner)
    androidTestImplementation(Dependencies.SupportTest.espressoCore)
}
