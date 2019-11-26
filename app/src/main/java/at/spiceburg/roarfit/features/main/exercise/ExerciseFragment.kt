package at.spiceburg.roarfit.features.main.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import at.spiceburg.roarfit.R
import kotlinx.android.synthetic.main.fragment_exercise.*
import java.text.SimpleDateFormat
import java.util.*

class ExerciseFragment : Fragment() {
    private lateinit var viewModel: ExerciseViewModel
    private val formatter = SimpleDateFormat("mm:ss", Locale.ENGLISH)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(
            this,
            ExerciseViewModel.Factory(requireContext())
        )
            .get(ExerciseViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise, container, false)
    }

    override fun onStart() {
        super.onStart()
        // val args = ExerciseFragmentArgs.fromBundle(requireArguments())
        // text_exercise_title.text = args.equipment.string

        viewModel.stopWatch.observe(this, Observer { time ->
            text_exercise_timer.text = formatter.format(time)
        })

        button_exercise_pause.setOnClickListener {
            viewModel.clearTimer()
            button_exercise_finish.visibility = View.VISIBLE
        }

        button_exercise_finish.setOnClickListener { requireActivity().finish() }

        viewModel.startTimer()
    }
}
