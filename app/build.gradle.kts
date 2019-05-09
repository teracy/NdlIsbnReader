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

    implementation(Dependencies.kotlin.stdlib)
    implementation(Dependencies.support.app_compat)
    implementation(Dependencies.support.recyclerview)
    implementation(Dependencies.constraint.layout)

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
    // retrofit
    implementation(Dependencies.retrofit2.runtime)
    implementation(Dependencies.retrofit2.adapter_rxjava2) {
        exclude(group = "io.reactivex.rxjava2", module = "rxjava")
    }
    implementation(Dependencies.retrofit2.converter_moshi) {
        exclude(group = "com.squareup.moshi", module = "moshi")
    }
    // RxAndroid
    implementation(Dependencies.rxandroid)
    // Dagger2
    implementation(Dependencies.dagger.runtime)
    implementation(Dependencies.dagger.android) {
        exclude(group = "com.google.code.findbugs", module = "jsr305")
    }
    implementation(Dependencies.dagger.android_support) {
        exclude(group = "com.google.code.findbugs", module = "jsr305")
    }
    // Paging
    implementation(Dependencies.paging)
    // LifeCycle
    implementation(Dependencies.lifecycle.reactivestreams)
    implementation(Dependencies.lifecycle.extensions)
    // Firebase
    implementation(Dependencies.firebase.runtime)
    implementation(Dependencies.firebase.ml_vison)
    // PermissionsDispatcher
    implementation(Dependencies.permissionsdispatcher.runtime) {
        exclude(group = "com.android.support")
    }
    kapt(Dependencies.permissionsdispatcher.processor)

    androidTestImplementation(Dependencies.support_test.runner)
    androidTestImplementation(Dependencies.support_test.espresso_core)
}
