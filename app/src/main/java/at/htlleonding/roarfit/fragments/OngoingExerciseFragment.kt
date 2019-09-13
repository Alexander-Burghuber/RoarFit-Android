package at.htlleonding.roarfit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import at.htlleonding.roarfit.R
import at.htlleonding.roarfit.viewmodels.OngoingExerciseViewModel
import kotlinx.android.synthetic.main.fragment_ongoing_exercise.*

class OngoingExerciseFragment : Fragment() {
    private lateinit var viewModel: OngoingExerciseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(OngoingExerciseViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ongoing_exercise, container, false)
    }

    override fun onStart() {
        super.onStart()
        val args = OngoingExerciseFragmentArgs.fromBundle(requireArguments())
        val equipment = args.equipment
        ongoing_exercise_title.text = equipment.toString()

        viewModel.timerLD.observe(this, Observer { time ->
            ongoing_exercise_timer.text = time
        })

        viewModel.startTimer()

        ongoing_exercise_button_stop.setOnClickListener {
            viewModel.stopTimer()
            ongoing_exercise_button_finish.visibility = View.VISIBLE
        }

        ongoing_exercise_button_finish.setOnClickListener {
            requireActivity().finish()
        }
    }
}