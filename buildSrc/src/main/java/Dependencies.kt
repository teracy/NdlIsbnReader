object Dependencies {
    internal object Versions {
        const val androidGradlePlugin = "3.4.0"
        const val kotlin = "1.3.30"
        const val junit = "4.12"
        const val dokka = "0.9.17"
        const val javaxInject = "1"
        const val okhttp3 = "3.14.0"
        const val retrofit2 = "2.5.0"
        const val assertj = "3.11.1"
        const val support = "28.0.0"
        const val multidex = "1.0.3"
        const val constraint = "1.1.3"
        const val supportTest = "1.0.2"
        const val espresso = "3.0.2"
        const val dagger = "2.16"
        const val tikxml = "0.8.13"
        const val gms = "4.2.0"
        const val firebase = "16.0.8"
        const val mlkit = "19.0.3"
        const val permissionsdispatcher = "3.3.1"
        const val kotlinxCoroutines = "1.2.1"
        const val retrofit2KotlinCoroutinesAdapter = "0.9.2"
    }

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    object Kotlin {
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    }

    const val junit = "junit:junit:${Versions.junit}"
    const val javaxInject = "javax.inject:javax.inject:${Versions.javaxInject}"

    object Dokka {
        const val gradlePlugin = "org.jetbrains.dokka:dokka-gradle-plugin:${Versions.dokka}"
    }

    object Okhttp3 {
        const val runtime = "com.squareup.okhttp3:okhttp:${Versions.okhttp3}"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp3}"
        const val mockwebserver = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp3}"
    }

    object Retrofit2 {
        const val runtime = "com.squareup.retrofit2:retrofit:${Versions.retrofit2}"
        const val converterSimplexml = "com.squareup.retrofit2:converter-simplexml:${Versions.retrofit2}"
        const val kotlinCoroutinesAdapter =
            "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:${Versions.retrofit2KotlinCoroutinesAdapter}"
    }

    const val assertj = "org.assertj:assertj-core:${Versions.assertj}"

    object Support {
        const val appCompat = "com.android.support:appcompat-v7:${Versions.support}"
        const val design = "com.android.support:design:${Versions.support}"
        const val recyclerview = "com.android.support:recyclerview-v7:${Versions.support}"
    }

    const val multidex = "com.android.support:multidex:${Versions.multidex}"

    object Constraint {
        const val layout = "com.android.support.constraint:constraint-layout:${Versions.constraint}"
    }

    object SupportTest {
        const val runner = "com.android.support.test:runner:${Versions.supportTest}"
        const val espressoCore = "com.android.support.test.espresso:espresso-core:${Versions.espresso}"
    }

    object Dagger {
        const val runtime = "com.google.dagger:dagger:${Versions.dagger}"
        const val compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
        const val android = "com.google.dagger:dagger-android:${Versions.dagger}"
        const val androidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
        const val androidSupportCompiler = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    }

    object Tikxml {
        const val core = "com.tickaroo.tikxml:core:${Versions.tikxml}"
        const val annotation = "com.tickaroo.tikxml:annotation:${Versions.tikxml}"
        const val processor = "com.tickaroo.tikxml:processor:${Versions.tikxml}"
        const val retrofitConverter = "com.tickaroo.tikxml:retrofit-converter:${Versions.tikxml}"
    }

    const val gms = "com.google.gms:google-services:${Versions.gms}"

    object Firebase {
        const val runtime = "com.google.firebase:firebase-core:${Versions.firebase}"
        const val mlVision = "com.google.firebase:firebase-ml-vision:${Versions.mlkit}"
    }

    object Permissionsdispatcher {
        const val runtime = "com.github.hotchemi:permissionsdispatcher:${Versions.permissionsdispatcher}"
        const val processor = "com.github.hotchemi:permissionsdispatcher-processor:${Versions.permissionsdispatcher}"
    }

    object KotlinxCoroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutines}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinxCoroutines}"
    }
}
