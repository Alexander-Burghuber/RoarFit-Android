package at.spiceburg.roarfit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.viewmodels.ExerciseViewModel
import kotlinx.android.synthetic.main.fragment_exercise.*

class ExerciseFragment : Fragment() {
    private lateinit var viewModel: ExerciseViewModel

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
        val args = ExerciseFragmentArgs.fromBundle(requireArguments())
        text_exercise_title.text = args.equipment.toString()

        viewModel.timerLD.observe(this, Observer { time ->
            text_exercise_timer.text = time
        })

        button_exercise_pause.setOnClickListener {
            viewModel.stopTimer()
            button_exercise_finish.visibility = View.VISIBLE
        }

        button_exercise_finish.setOnClickListener {
            // viewModel.insertUserExercise(UserExercise())
            requireActivity().finish()
        }

        viewModel.startTimer()
    }
}
