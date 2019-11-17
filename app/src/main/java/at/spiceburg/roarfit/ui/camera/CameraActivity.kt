package at.spiceburg.roarfit.ui.camera

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.SurfaceView
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import at.spiceburg.roarfit.R
import github.nisrulz.qreader.QREader
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {

    private lateinit var viewModel: CameraViewModel
    private lateinit var qrReader: QREader
    private lateinit var cameraView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }

    override fun onStart() {
        super.onStart()

        cameraView = findViewById(R.id.surfaceview_camera)

        // Init QR-Reader
        qrReader = QREader.Builder(this, cameraView, viewModel)
            .facing(QREader.BACK_CAM)
            .enableAutofocus(true)
            .width(cameraView.width)
            .height(cameraView.height)
            .build()

        viewModel.qrLD.observe(this, Observer { qrResult ->
            text_camera_equipment.setText(qrResult)
        })

        viewModel.equipmentLD.observe(this, Observer { equipment ->
            hideKeyboard()
            // val action = CameraFragmentDirections.actionCameraFragmentToExerciseListFragment(equipment)
            // findNavController().navigate(action)
        })

        cameraView.setOnClickListener {
            hideKeyboard()
            text_camera_equipment.clearFocus()
        }

        text_camera_equipment.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    viewModel.handleTextChange(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        qrReader.initAndStart(cameraView)
    }

    override fun onPause() {
        super.onPause()
        qrReader.releaseAndCleanup()
    }

    override fun onStop() {
        super.onStop()
        qrReader.stop()
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}
