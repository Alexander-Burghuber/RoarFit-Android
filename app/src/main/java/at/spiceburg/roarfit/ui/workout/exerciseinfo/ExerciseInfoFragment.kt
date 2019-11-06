package at.spiceburg.roarfit.ui.workout.exerciseinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.ui.workout.WorkoutActivity
import kotlinx.android.synthetic.main.fragment_exercise_info.*

class ExerciseInfoFragment : Fragment() {
    private lateinit var viewModel: ExerciseInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(
            this,
            ExerciseInfoViewModel.Factory(
                requireContext()
            )
        )
            .get(ExerciseInfoViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as WorkoutActivity).setupExerciseFragment()
    }

    override fun onStart() {
        super.onStart()
        val args =
            ExerciseInfoFragmentArgs.fromBundle(requireArguments())
        val equipment = args.equipment

        text_exerciseinfo_title.text = equipment.string

        val onExerciseClicked: (exerciseTemplate: ExerciseTemplate) -> Unit = { exerciseTemplate ->

        }

        val adapter = ExercisesAdapter(
            requireContext(),
            onExerciseClicked
        )
        recyclerview_exerciseinfo_exercises.adapter = adapter
        recyclerview_exerciseinfo_exercises.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getExerciseTemplates(equipment).observe(this, Observer { exerciseTemplates ->
            adapter.setExerciseTemplates(exerciseTemplates)
        })

        /* button_exerciseinfo_start.setOnClickListener {
             val action =
                 ExerciseInfoFragmentDirections.actionExerciseInfoFragmentToExerciseFragment(
                     equipment
                 )
             findNavController().navigate(action)
         }*/
    }

    companion object {
        private val TAG = ExerciseInfoFragment::class.java.simpleName
    }
}
