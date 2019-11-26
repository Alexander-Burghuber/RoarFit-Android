package at.spiceburg.roarfit.features.main.camera

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.features.main.MainActivity
import github.nisrulz.qreader.QREader
import kotlinx.android.synthetic.main.fragment_camera.*

class CameraFragment : Fragment() {

    private lateinit var viewModel: CameraViewModel
    private lateinit var qrReader: QREader
    private lateinit var cameraView: SurfaceView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(CameraViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onStart() {
        super.onStart()

        // add status bar settings for camera
        with(requireActivity() as MainActivity) {
            with(window) {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = resources.getColor(R.color.black, null)
            }
        }

        cameraView = requireView().findViewById(R.id.surfaceview_camera)

        // Init QR-Reader
        qrReader = QREader.Builder(requireContext(), cameraView, viewModel)
            .facing(QREader.BACK_CAM)
            .enableAutofocus(true)
            .width(cameraView.width)
            .height(cameraView.height)
            .build()

        viewModel.qrLD.observe(this, Observer { qrResult ->
            text_camera_equipment.setText(qrResult)
        })

        viewModel.equipmentLD.observe(this, Observer { equipment ->
            // val action = CameraFragmentDirections.actionCameraFragmentToExerciseListFragment(equipment)
            // findNavController().navigate(action)
        })

        button_camera_close.setOnClickListener {
            findNavController().navigateUp()
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
        // remove status bar settings for camera
        with((requireActivity() as MainActivity)) {
            window.statusBarColor = resources.getColor(R.color.primaryDark, null)
        }
    }
}
