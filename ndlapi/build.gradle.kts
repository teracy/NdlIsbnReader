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
    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar")))

    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Support.appCompat)

    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.assertj)
    androidTestImplementation(Dependencies.assertj)

    // RxJava
    implementation(Dependencies.rxjava2)

    androidTestImplementation(Dependencies.SupportTest.runner)
    androidTestImplementation(Dependencies.SupportTest.espressoCore)
}
