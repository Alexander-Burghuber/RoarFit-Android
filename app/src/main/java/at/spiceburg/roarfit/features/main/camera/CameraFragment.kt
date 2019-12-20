package at.spiceburg.roarfit.features.main.camera

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CameraViewModel::class.java)
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

        // init QR-Reader
        qrReader = QREader.Builder(requireContext(), cameraView, viewModel)
            .facing(QREader.BACK_CAM)
            .enableAutofocus(true)
            .width(cameraView.width)
            .height(cameraView.height)
            .build()

        viewModel.qrResult.observe(this) { equipment ->
            Log.d(TAG, "QR Result: ${equipment.string}")
            text_camera_equipment.setText(equipment.string)
            val action =
                CameraFragmentDirections.actionCameraFragmentToExerciseListFragment(equipment)
            findNavController().navigate(action)
        }

        button_camera_close.setOnClickListener {
            findNavController().navigateUp()
        }
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

    companion object {
        private val TAG = CameraFragment::class.java.simpleName
    }
}
