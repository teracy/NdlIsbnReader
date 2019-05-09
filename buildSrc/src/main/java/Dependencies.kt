object Dependencies {
    object Versions {
        const val androidGradlePlugin = "3.4.0"
        const val kotlin = "1.3.21"
        const val junit = "4.12"
        const val hamcrest = "1.3"
        const val dokka = "0.9.17"
        const val rxjava2 = "2.2.7"
        const val rxandroid = "2.1.1"
        const val moshi = "1.8.0"
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
        const val paging = "1.0.0"
        const val lifecycle = "1.1.1"
        const val glide = "4.9.0"
        const val tikxml = "0.8.13"
        const val gms = "4.2.0"
        const val firebase = "16.0.8"
        const val mlkit = "19.0.3"
        const val permissionsdispatcher = "3.3.1"
    }

    const val android_gradle_plugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    object kotlin {
        const val gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    }

    const val junit = "junit:junit:${Versions.junit}"
    const val hamcrest = "org.hamcrest:hamcrest-library:${Versions.hamcrest}"
    const val rxjava2 = "io.reactivex.rxjava2:rxjava:${Versions.rxjava2}"
    const val rxandroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxandroid}"
    const val javax_inject = "javax.inject:javax.inject:${Versions.javaxInject}"

    object dokka {
        const val gradle_plugin = "org.jetbrains.dokka:dokka-gradle-plugin:${Versions.dokka}"
    }

    object moshi {
        const val runtime = "com.squareup.moshi:moshi:${Versions.moshi}"
        const val moshi_kotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    }

    object okhttp3 {
        const val runtime = "com.squareup.okhttp3:okhttp:${Versions.okhttp3}"
        const val logging_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp3}"
        const val mockwebserver = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp3}"
    }

    object retrofit2 {
        const val runtime = "com.squareup.retrofit2:retrofit:${Versions.retrofit2}"
        const val adapter_rxjava2 = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit2}"
        const val converter_moshi = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit2}"
        const val converter_simplexml = "com.squareup.retrofit2:converter-simplexml:${Versions.retrofit2}"
    }

    const val assertj = "org.assertj:assertj-core:${Versions.assertj}"

    object support {
        const val annotations = "com.android.support:support-annotations:${Versions.support}"
        const val app_compat = "com.android.support:appcompat-v7:${Versions.support}"
        const val design = "com.android.support:design:${Versions.support}"
        const val recyclerview = "com.android.support:recyclerview-v7:${Versions.support}"
    }

    const val multidex = "com.android.support:multidex:${Versions.multidex}"

    object constraint {
        const val layout = "com.android.support.constraint:constraint-layout:${Versions.constraint}"
    }

    object support_test {
        const val runner = "com.android.support.test:runner:${Versions.supportTest}"
        const val espresso_core = "com.android.support.test.espresso:espresso-core:${Versions.espresso}"
    }

    object dagger {
        const val runtime = "com.google.dagger:dagger:${Versions.dagger}"
        const val compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
        const val android = "com.google.dagger:dagger-android:${Versions.dagger}"
        const val android_support = "com.google.dagger:dagger-android-support:${Versions.dagger}"
        const val android_support_compiler = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    }

    const val paging = "android.arch.paging:runtime:${Versions.paging}"

    object lifecycle {
        const val reactivestreams = "android.arch.lifecycle:reactivestreams:${Versions.lifecycle}"
        const val extensions = "android.arch.lifecycle:extensions:${Versions.lifecycle}"
        const val compiler = "android.arch.lifecycle:compiler:${Versions.lifecycle}"
    }

    object glide {
        const val runtime = "com.github.bumptech.glide:glide:${Versions.glide}"
        const val compiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
        const val okhttp3 = "com.github.bumptech.glide:okhttp3-integration:${Versions.glide}"
        const val recyclerview = "com.github.bumptech.glide:recyclerview-integration:${Versions.glide}"
    }

    object tikxml {
        const val core = "com.tickaroo.tikxml:core:${Versions.tikxml}"
        const val annotation = "com.tickaroo.tikxml:annotation:${Versions.tikxml}"
        const val processor = "com.tickaroo.tikxml:processor:${Versions.tikxml}"
        const val retrofit_converter = "com.tickaroo.tikxml:retrofit-converter:${Versions.tikxml}"
    }

    const val gms = "com.google.gms:google-services:${Versions.gms}"

    object firebase {
        const val runtime = "com.google.firebase:firebase-core:${Versions.firebase}"
        const val ml_vison = "com.google.firebase:firebase-ml-vision:${Versions.mlkit}"
    }

    object permissionsdispatcher {
        const val runtime = "com.github.hotchemi:permissionsdispatcher:${Versions.permissionsdispatcher}"
        const val processor = "com.github.hotchemi:permissionsdispatcher-processor:${Versions.permissionsdispatcher}"
    }
}