package at.htl_leonding.roarfit.activities

import android.content.Context
import android.os.Bundle
import android.view.SurfaceView
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import at.htl_leonding.roarfit.R
import at.htl_leonding.roarfit.viewmodels.CameraViewModel
import github.nisrulz.qreader.QREader
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {
    private lateinit var model: CameraViewModel
    private lateinit var qrEader: QREader
    private lateinit var cameraView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        model = ViewModelProviders.of(this).get(CameraViewModel::class.java)

        // Setup SurfaceView
        cameraView = findViewById(R.id.camera_view)

        // Init QREader
        qrEader = QREader.Builder(this, cameraView, model)
            .facing(QREader.BACK_CAM)
            .enableAutofocus(true)
            .width(cameraView.width)
            .height(cameraView.height)
            .build()

        model.qrResult.observe(this, Observer { qrResult ->
            camera_text.setText(qrResult)
        })

        cameraView.setOnClickListener {
            hideKeyboard()
            camera_text.clearFocus()
        }
    }

    override fun onResume() {
        super.onResume()
        qrEader.initAndStart(cameraView)
    }

    override fun onPause() {
        super.onPause()
        qrEader.releaseAndCleanup()
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}
