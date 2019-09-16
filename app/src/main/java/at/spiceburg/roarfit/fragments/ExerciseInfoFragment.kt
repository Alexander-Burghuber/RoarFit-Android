package at.spiceburg.roarfit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.activities.WorkoutActivity
import kotlinx.android.synthetic.main.fragment_exercise_info.*

class ExerciseInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as WorkoutActivity).setupExerciseFragment()
    }

    override fun onStart() {
        super.onStart()
        val args = ExerciseInfoFragmentArgs.fromBundle(requireArguments())
        val equipment = args.equipment
        exercise_info_title.text = equipment.toString()

        exercise_info_button_start.setOnClickListener {
            val action =
                ExerciseInfoFragmentDirections.actionExerciseInfoFragmentToOngoingExerciseFragment(
                    equipment
                )
            findNavController().navigate(action)
        }
    }
}