package com.github.teracy.ndlisbnreader

import android.Manifest
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewTreeObserver
import com.github.teracy.ndlapi_tikxml.OpenSearchApiClientImplTikXml
import com.github.teracy.ndlisbnreader.util.AppSchedulerProvider
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import permissions.dispatcher.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * https://github.com/kboy-silvergym/MLKitSample/blob/e869d384c9dcbf0ee869738c8fcac582659a1af8/Android/app/src/main/java/net/kboy/mlkitsample/MainActivity.kt
 */
@RuntimePermissions
class MainActivity : AppCompatActivity() {
    private var captureSession: CameraCaptureSession? = null
    private var cameraDevice: CameraDevice? = null
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private var imageReader: ImageReader? = null
    private lateinit var previewRequest: CaptureRequest
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        textureView.afterMeasured {
//            // TextureViewの描画が完了したらsurfaceTextureListenerをセットする
//            this.surfaceTextureListener = textureViewSurfaceTextureListener
//        }
        textureView.surfaceTextureListener = textureViewSurfaceTextureListener
        startBackgroundThread()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: 以下はAnnotation Processorで生成
        onRequestPermissionsResult(requestCode, grantResults)
    }

    // region Runtime Permission Implementation
    @NeedsPermission(Manifest.permission.CAMERA)
    fun openCamera() {
        Log.d(TAG, "openCamera called")
        val manager: CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            manager.openCamera(manager.cameraIdList[0], cameraDeviceStateCallback, null)
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
    // endregion

    private fun startBackgroundThread() {
        Log.d(TAG, "startBackgroundThread")
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread?.looper)
    }

    private fun createCameraPreviewSession() {
        try {
            val texture = textureView.surfaceTexture
            texture.setDefaultBufferSize(300, 300)
            val surface = Surface(texture)

            previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget(surface)

            cameraDevice?.createCaptureSession(
                Arrays.asList(surface, imageReader?.surface),
                cameraCaptureSessionStateCallback,
                null
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }
    }

    private val textureViewSurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {}

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {}

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean = false

        override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, width: Int, height: Int) {
            imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            imageReader?.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler)
            // Annotation ProcessorによってopenCameraから生成されたメソッドを呼ぶ
            openCameraWithPermissionCheck()
        }
    }

    private val cameraCaptureSessionStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            if (cameraDevice == null) return
            captureSession = session
            try {
                Log.d(TAG, "cameraCaptureSessionStateCallback")
                previewRequestBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                )
                previewRequest = previewRequestBuilder.build()
                captureSession?.setRepeatingRequest(
                    previewRequest,
                    captureCallback,
                    Handler(backgroundThread?.looper)
                )
            } catch (e: CameraAccessException) {
                Log.e(TAG, e.message)
            }
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            Log.e(TAG, "onConfigureFailed")
        }
    }

    private val cameraDeviceStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            this@MainActivity.cameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraDevice.close()
            this@MainActivity.cameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            onDisconnected(cameraDevice)
            finish()
        }
    }

    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {
//        override fun onCaptureStarted(
//            session: CameraCaptureSession,
//            request: CaptureRequest,
//            timestamp: Long,
//            frameNumber: Long
//        ) {
//            Log.d(TAG, "//////onCaptureStarted//////")
//        }
//
//        override fun onCaptureProgressed(
//            session: CameraCaptureSession,
//            request: CaptureRequest,
//            partialResult: CaptureResult
//        ) {
//            Log.d(TAG, "//////onCaptureProgressed//////")
//        }
//
//        override fun onCaptureCompleted(
//            session: CameraCaptureSession,
//            request: CaptureRequest,
//            result: TotalCaptureResult
//        ) {
//            Log.d(TAG, "//////onCaptureCompleted//////")
//        }
//
//        override fun onCaptureFailed(session: CameraCaptureSession, request: CaptureRequest, failure: CaptureFailure) {
//            Log.d(TAG, "//////onCaptureFailed//////")
//        }
//
//        override fun onCaptureSequenceCompleted(session: CameraCaptureSession, sequenceId: Int, frameNumber: Long) {
//            Log.d(TAG, "//////onCaptureSequenceCompleted//////")
//        }
//
//        override fun onCaptureSequenceAborted(session: CameraCaptureSession, sequenceId: Int) {
//            Log.d(TAG, "//////onCaptureSequenceAborted//////")
//        }
//
//        override fun onCaptureBufferLost(
//            session: CameraCaptureSession,
//            request: CaptureRequest,
//            target: Surface,
//            frameNumber: Long
//        ) {
//            Log.d(TAG, "//////onCaptureBufferLost//////")
//        }
    }

    // FIXME: 呼ばれないのはなぜ？？
    private val onImageAvailableListener = ImageReader.OnImageAvailableListener {
        val bitmap = textureView.bitmap
        val visionImage = FirebaseVisionImage.fromBitmap(bitmap)
        // バーコード読み取り：ISBNなのでEAN-13フォーマット
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_EAN_13)
            .build()
        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
        detector.detectInImage(visionImage)
            .addOnSuccessListener { barcodes ->
                Log.d(TAG, "****************SUCCESS****************")
                if (barcodes.isEmpty()) {
                    Log.w(TAG, "****************SUCCESS but EMPTY****************")
                    return@addOnSuccessListener
                }
                val barcode = barcodes[0]
                requestNdlApi(barcode.rawValue!!)
            }
            .addOnFailureListener {
                Log.e(TAG, "****************FAILED****************")
            }
    }

    /**
     * 国会図書館検索呼び出し
     */
    private fun requestNdlApi(barcode: String) {
        val interceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC)
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()

        val client = OpenSearchApiClientImplTikXml(okHttpClient)
        val schedulerProvider = AppSchedulerProvider()

        val subscribe = client.search(barcode)
            .subscribeOn(schedulerProvider.newThread())
            .observeOn(schedulerProvider.ui())
            .subscribe({
                Log.d(TAG, String.format("response:%d\n", it.items.size))
            }, {
                Log.e(TAG, String.format("error:" + it.message))
            })
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
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
