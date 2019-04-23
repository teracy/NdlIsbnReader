package com.github.teracy.ndlisbnreader

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.teracy.ndlapi_simplexml.OpenSearchApiClient
import com.github.teracy.ndlisbnreader.util.AppSchedulerProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val interceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC)
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()

        // ひとまずSimpleXML版
        val client = OpenSearchApiClient(okHttpClient)
        val schedulerProvider = AppSchedulerProvider()

        val subscribe = client.search("9784563005641")
            .subscribeOn(schedulerProvider.newThread())
            .observeOn(schedulerProvider.ui())
            .subscribe({
                System.out.printf("response:%d\n", it.items.size)
            }, {
                System.out.println("error:" + it.message)
            })
    }
}
