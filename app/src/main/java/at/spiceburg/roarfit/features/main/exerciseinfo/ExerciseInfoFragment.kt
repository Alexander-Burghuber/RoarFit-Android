package at.spiceburg.roarfit.features.main.exerciseinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import at.spiceburg.roarfit.R
import kotlinx.android.synthetic.main.fragment_exercise_info.*

class ExerciseInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_info, container, false)
    }

    override fun onStart() {
        super.onStart()
        val args = ExerciseInfoFragmentArgs.fromBundle(requireArguments())
        val exerciseTemplate = args.template
        text_exerciseinfo.text = exerciseTemplate.name

        button_exerciseinfo_start.setOnClickListener {
            val action =
                ExerciseInfoFragmentDirections.actionExerciseInfoFragmentToExerciseFragment(
                    exerciseTemplate
                )
            findNavController().navigate(action)
        }
    }
}
