plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")

    id("com.google.gms.google-services")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "com.github.teracy.ndlisbnreader"
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
    implementation(project(":ndlapi_simplexml"))
    implementation(project(":ndlapi_tikxml"))

    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar")))

    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.kotlinxCoroutines)
    implementation(Dependencies.Support.appCompat)
    implementation(Dependencies.Support.recyclerview)
    implementation(Dependencies.Constraint.layout)

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
    // retrofit
    implementation(Dependencies.Retrofit2.runtime)
    implementation(Dependencies.Retrofit2.adapterRxjava2) {
        exclude(group = "io.reactivex.rxjava2", module = "rxjava")
    }
    implementation(Dependencies.Retrofit2.converterMoshi) {
        exclude(group = "com.squareup.moshi", module = "moshi")
    }
    // RxAndroid
    implementation(Dependencies.rxandroid)
    // Dagger2
    implementation(Dependencies.Dagger.runtime)
    implementation(Dependencies.Dagger.android) {
        exclude(group = "com.google.code.findbugs", module = "jsr305")
    }
    implementation(Dependencies.Dagger.androidSupport) {
        exclude(group = "com.google.code.findbugs", module = "jsr305")
    }
    // Paging
    implementation(Dependencies.paging)
    // LifeCycle
    implementation(Dependencies.Lifecycle.reactivestreams)
    implementation(Dependencies.Lifecycle.extensions)
    // Firebase
    implementation(Dependencies.Firebase.runtime)
    implementation(Dependencies.Firebase.mlVision)
    // PermissionsDispatcher
    implementation(Dependencies.Permissionsdispatcher.runtime) {
        exclude(group = "com.android.support")
    }
    kapt(Dependencies.Permissionsdispatcher.processor)

    androidTestImplementation(Dependencies.SupportTest.runner)
    androidTestImplementation(Dependencies.SupportTest.espressoCore)
}
