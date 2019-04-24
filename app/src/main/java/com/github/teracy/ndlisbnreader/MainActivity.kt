package com.github.teracy.ndlisbnreader

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.TextureView
import android.view.View
import android.view.ViewTreeObserver
import com.github.teracy.ndlapi_tikxml.OpenSearchApiClientImplTikXml
import com.github.teracy.ndlisbnreader.util.AppSchedulerProvider
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textureView.afterMeasured {
            // TextureViewの描画が完了したらsurfaceTextureListenerをセットする
            this.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {}

                override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {}

                override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean = true

                override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
                    openCamera()
                }
            }
        }

        val interceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC)
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()

//        // SimpleXML版
//        val client = OpenSearchApiClientImplSimpleXml(okHttpClient)
        // TikXML版
        val client = OpenSearchApiClientImplTikXml(okHttpClient)
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

private fun openCamera() {
    System.out.println("openCamera called")
    // TODO: Runtime Permissionとカメラ処理を実装する
}

/**
 * View描画完了時処理
 * https://qiita.com/titoi2/items/7bf271cd17beae74620b
 */
inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}
