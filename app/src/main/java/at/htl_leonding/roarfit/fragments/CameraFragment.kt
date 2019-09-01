package at.htl_leonding.roarfit.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import at.htl_leonding.roarfit.R
import at.htl_leonding.roarfit.viewmodels.CameraViewModel
import github.nisrulz.qreader.QREader
import kotlinx.android.synthetic.main.fragment_camera.*

class CameraFragment : Fragment() {
    private lateinit var viewModel: CameraViewModel
    private lateinit var qrEader: QREader
    private lateinit var cameraView: SurfaceView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // val window = requireActivity().window
        // window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    }

    override fun onStart() {
        super.onStart()
        viewModel = ViewModelProviders.of(this).get(CameraViewModel::class.java)

        cameraView = requireView().findViewById(R.id.camera_view)

        // Init QREader
        qrEader = QREader.Builder(requireContext(), cameraView, viewModel)
            .facing(QREader.BACK_CAM)
            .enableAutofocus(true)
            .width(cameraView.width)
            .height(cameraView.height)
            .build()

        viewModel.qrLiveData.observe(this, Observer { qrResult ->
            camera_text.setText(qrResult)
        })

        viewModel.equipmentLiveData.observe(this, Observer { equipment ->
            hideKeyboard()
            val action =
                CameraFragmentDirections.actionCameraFragmentToExerciseInfoFragment(equipment)
            findNavController().navigate(action)
        })

        cameraView.setOnClickListener {
            hideKeyboard()
            camera_text.clearFocus()
        }

        camera_text.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) viewModel.handleTextChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
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
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}