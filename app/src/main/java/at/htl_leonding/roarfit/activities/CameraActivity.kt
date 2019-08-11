package at.htl_leonding.roarfit.activities

import android.os.Bundle
import android.view.SurfaceView
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
    private lateinit var surfaceView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        model = ViewModelProviders.of(this).get(CameraViewModel::class.java)

        // Setup SurfaceView
        surfaceView = findViewById(R.id.camera_view)

        // Init QREader
        qrEader = QREader.Builder(this, surfaceView, model)
            .facing(QREader.BACK_CAM)
            .enableAutofocus(true)
            .width(surfaceView.width)
            .height(surfaceView.height)
            .build()

        model.qrResult.observe(this, Observer { qrResult ->
            textData.text = qrResult
        })
    }

    override fun onResume() {
        super.onResume()
        qrEader.initAndStart(surfaceView)
    }

    override fun onPause() {
        super.onPause()
        qrEader.releaseAndCleanup()
    }

}
