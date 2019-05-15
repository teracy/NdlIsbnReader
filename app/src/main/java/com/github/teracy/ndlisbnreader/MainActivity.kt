package com.github.teracy.ndlisbnreader

import android.Manifest
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
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
import com.github.teracy.ndlapi.response.Book
import com.github.teracy.ndlapi_tikxml.OpenSearchApiClientImplTikXml
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import permissions.dispatcher.*
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@RuntimePermissions
class MainActivity : AppCompatActivity() {
    private var captureSession: CameraCaptureSession? = null
    private var cameraDevice: CameraDevice? = null
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private var imageReader: ImageReader? = null
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null

    private val cameraOpenCloseLock = Semaphore(1)

    private var isbnCode: String? = null

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textureView.afterMeasured {
            // TextureViewの描画が完了したらsurfaceTextureListenerをセットする
            this.surfaceTextureListener = textureViewSurfaceTextureListener
        }
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
    }

    // TODO: 可能ならViewのonClickでフォーカスを発火させるための処理を実装する

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: 以下はAnnotation Processorで生成
        onRequestPermissionsResult(requestCode, grantResults)
    }

    // region Runtime Permission Implementation
    @NeedsPermission(Manifest.permission.CAMERA)
    fun openCamera() {
        val manager: CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            manager.openCamera(manager.cameraIdList[0], cameraDeviceStateCallback, null)
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
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

    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            captureSession?.close()
            captureSession = null
            cameraDevice?.close()
            cameraDevice = null
            imageReader?.close()
            imageReader = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            cameraOpenCloseLock.release()
        }
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").apply { start() }
        backgroundHandler = Handler(backgroundThread?.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val texture = textureView.surfaceTexture
            // TODO: サイズはひとまず仮
            texture.setDefaultBufferSize(300, 300)
            val surface = Surface(texture)

            previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget(surface)

            GlobalScope.launch(Dispatchers.Main) {
                captureSession = cameraDevice?.captureSession(Arrays.asList(surface, imageReader?.surface))
                resumeCapture()
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.message)
        }
    }

    /**
     * キャプチャ処理再開
     */
    private fun resumeCapture() {
        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
        captureSession?.setRepeatingRequest(previewRequestBuilder.build(), captureCallback, backgroundHandler)
    }

    private val textureViewSurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {}

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {}

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean = false

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, MAX_IMAGES).apply {
                setOnImageAvailableListener(onImageAvailableListener, backgroundHandler)
            }
            // Annotation ProcessorによってopenCameraから生成されたメソッドを呼ぶ
            openCameraWithPermissionCheck()
        }
    }

    // NOTE: CameraCaptureSession.StateCallbackのsuspendCoroutine版
    private suspend fun CameraDevice.captureSession(outputs: List<Surface?>): CameraCaptureSession? =
        suspendCoroutine { continuation ->
            val callback = object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    continuation.resume(session)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "CameraDevice.captureSession onConfigureFailed")
                    continuation.resume(null)
                }
            }
            createCaptureSession(outputs, callback, null)
        }

    // NOTE: CameraDevice.StateCallbackをsuspendCoroutine利用に置き換えたかったが、PermissionsDispatcherとの絡み？でダメだった
    private suspend fun CameraManager.openCamera(cameraId: String, handler: Handler?): CameraDevice? =
        suspendCoroutine { continuation ->
            val callback = object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    continuation.resume(camera)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                    continuation.resume(null)
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                    Log.w(TAG, "CameraManager.openCamera onError")
                    continuation.resume(null)
                }
            }
            openCamera(cameraId, callback, handler)
        }

    private val cameraDeviceStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            cameraOpenCloseLock.release()
            this@MainActivity.cameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraOpenCloseLock.release()
            cameraDevice.close()
            this@MainActivity.cameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            Log.w(TAG, "CameraDevice.StateCallback.onError")
            onDisconnected(cameraDevice)
            this@MainActivity.finish()
        }
    }

    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            process()
        }

        /**
         * 読み取った画像の処理
         */
        private fun process() {
            val captureBuilder = cameraDevice?.createCaptureRequest(
                CameraDevice.TEMPLATE_STILL_CAPTURE
            )?.apply {
                imageReader?.surface?.apply {
                    addTarget(this)
                }
                set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            }
            val captureCallback = object : CameraCaptureSession.CaptureCallback() {

                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    resumeCapture()
                }
            }

            captureBuilder?.build()?.let { request ->
                captureSession?.apply {
                    stopRepeating()
                    abortCaptures()
                    capture(request, captureCallback, null)
                }
            }
        }
    }

    private fun Image.readBarcode() {
        // ひとまず回転は考えない
        val visionImage = FirebaseVisionImage.fromMediaImage(this, 0)

        // バーコード読み取り：ISBNなのでEAN-13フォーマット
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_EAN_13)
            .build()
        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
        detector.detectInImage(visionImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isEmpty()) {
                    return@addOnSuccessListener
                }
                barcodes
                    .map { it.rawValue }
                    .firstOrNull {
                        it?.run {
                            "^978.*".toRegex().matches(this) || "^979.*".toRegex().matches(this)
                        } ?: false
                    }?.apply {
                        if (this != isbnCode) {
                            isbnCode = this
                            textViewIsbn.text = isbnCode
//                            requestNdlApi(isbnCode!!)
                            requestNdlApi(isbnCode!!)
                        }
                    }
            }
            .addOnFailureListener {
                Log.e(TAG, "FAILED: readBarcode")
            }
    }

    private val onImageAvailableListener = ImageReader.OnImageAvailableListener {
        backgroundHandler?.post {
            backgroundHandler?.post {
                it.acquireNextImage().apply {
                    readBarcode()
                }.close()
            }
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

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Log.e(TAG, String.format("error:" + throwable.message))
        }

        fun fetchAsync(): Deferred<Single<Book>> = scope.async { client.search(barcode) }
        scope.launch(exceptionHandler) {
            fetchAsync().await().let {
                val book = it.blockingGet()
                if (book.items.isEmpty()) {
                    return@let
                }
                book.items[0].apply {
                    // タイトル
                    val titleBuilder = StringBuilder()
                    titleBuilder.append(title)
                    subject?.let { s ->
                        if (s.isNotEmpty()) {
                            titleBuilder.append(" ").append(s)
                        }
                    }
                    volume?.let { v ->
                        if (v.isNotEmpty()) {
                            titleBuilder.append(" ").append(v)
                        }
                    }
                    edition?.let { e ->
                        if (e.isNotEmpty()) {
                            titleBuilder.append(" ").append(e)
                        }
                    }
                    seriesTitle?.let { t ->
                        if (t.isNotEmpty()) {
                            titleBuilder.append(" ").append(t)
                        }
                    }
                    textViewTitle.text = titleBuilder.toString()

                    // 出版社
                    textViewPublisher.text = publisher

                    // 著者
                    textViewCreator.text = creators?.joinToString(separator = ", ")
                }
            }
        }
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
        const val MAX_IMAGES = 2
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
