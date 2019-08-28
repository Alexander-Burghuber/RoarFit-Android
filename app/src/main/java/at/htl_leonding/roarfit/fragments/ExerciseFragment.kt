package at.htl_leonding.roarfit.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import at.htl_leonding.roarfit.R
import at.htl_leonding.roarfit.activities.WorkoutActivity
import kotlinx.android.synthetic.main.fragment_exercise.*

/**
 * A simple [Fragment] subclass.
 */
class ExerciseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as WorkoutActivity).setupExerciseFragment()
    }

    override fun onStart() {
        super.onStart()
        val args = ExerciseFragmentArgs.fromBundle(requireArguments())
        exercise_title.text = args.equipment.toString()
    }

}
