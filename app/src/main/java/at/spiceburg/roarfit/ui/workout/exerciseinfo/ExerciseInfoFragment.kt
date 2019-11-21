package at.spiceburg.roarfit.ui.workout.exerciseinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import at.spiceburg.roarfit.R

class ExerciseInfoFragment : Fragment() {
    private lateinit var viewModel: ExerciseInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(ExerciseInfoViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_info, container, false)
    }

    /*override fun onStart() {
        super.onStart()
        val args = ExerciseInfoFragmentArgs.fromBundle(requireArguments())
        val exerciseTemplate = args.exerciseTemplate

        exerciseinfo_text.text = exerciseTemplate.name
    }*/
}
