package at.spiceburg.roarfit.features.main.camera

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import at.spiceburg.roarfit.R
import github.nisrulz.qreader.QREader
import kotlinx.android.synthetic.main.fragment_camera.*

class CameraFragment : Fragment() {

    private lateinit var viewModel: CameraViewModel
    private lateinit var qrReader: QREader
    private lateinit var cameraView: SurfaceView

    private var statusBarColor: Int = 0
    private var navigationBarColor: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onStart() {
        super.onStart()

        viewModel = ViewModelProvider(this).get(CameraViewModel::class.java)

        // change theme to fit camera
        requireActivity().apply {
            // allow the modification of system bar colors
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            // backup the main theme colors
            statusBarColor = window.statusBarColor
            navigationBarColor = window.navigationBarColor

            // set the camera theme colors
            window.statusBarColor = resources.getColor(R.color.black, null)
            window.navigationBarColor = resources.getColor(R.color.black, null)
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

        // reset theme
        requireActivity().apply {
            window.statusBarColor = statusBarColor
            window.navigationBarColor = navigationBarColor
        }
    }

    companion object {
        private val TAG = CameraFragment::class.java.simpleName
    }
}
