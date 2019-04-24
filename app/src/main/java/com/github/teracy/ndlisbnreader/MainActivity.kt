package com.github.teracy.ndlisbnreader

import android.Manifest
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewTreeObserver
import com.github.teracy.ndlapi_tikxml.OpenSearchApiClientImplTikXml
import com.github.teracy.ndlisbnreader.util.AppSchedulerProvider
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import permissions.dispatcher.*
import java.util.concurrent.TimeUnit

@RuntimePermissions
class MainActivity : AppCompatActivity() {
    private var cameraDevice: CameraDevice? = null
    private val cameraManager: CameraManager by lazy {
        getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    private var captureSession: CameraCaptureSession? = null

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
                    // Annotation ProcessorによってopenCameraから生成されたメソッドを呼ぶ
                    callCameraManagerWithPermissionCheck()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: 以下はAnnotation Processorで生成
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun callCameraManager() {
        if (cameraManager.cameraIdList.isEmpty()) {
            System.out.println("!!!!!!!!!!!cameraIdList is empty!!!!!!!!!!!")
            return
        }
        openCamera()
    }

    @Throws(SecurityException::class)
    private fun openCamera() {
        System.out.println("openCamera called")
        cameraManager.openCamera(cameraManager.cameraIdList.get(0), object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                createCameraPreviewSession()
            }

            override fun onDisconnected(camera: CameraDevice) {
                cameraDevice?.close()
                cameraDevice = null
            }

            override fun onError(camera: CameraDevice, p1: Int) {
                cameraDevice?.close()
                cameraDevice = null
            }
        }, null)
    }

    private fun createCameraPreviewSession() {
        if (cameraDevice == null) {
            return
        }
        val texture = textureView.surfaceTexture
        texture.setDefaultBufferSize(640, 480)
        val surface = Surface(texture)

        val previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        previewRequestBuilder.addTarget(surface)

        cameraDevice?.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                captureSession = session
                captureSession?.setRepeatingRequest(previewRequestBuilder.build(), null, null)
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {}
        }, null)
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    fun showRationaleForCamera(request: PermissionRequest) {
        showRationaleDialog(R.string.permission_camera_rationale, request)
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraDenied() {
//        Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun onCameraNeverAskAgain() {
//        Toast.makeText(this, R.string.permission_camera_never_askagain, Toast.LENGTH_SHORT).show()
    }

    private fun showRationaleDialog(@StringRes messageResId: Int, request: PermissionRequest) {
        AlertDialog.Builder(this)
            .setPositiveButton(android.R.string.ok) { _, _ -> request.proceed() }
            .setNegativeButton(android.R.string.no) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(messageResId)
            .show()
    }
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
