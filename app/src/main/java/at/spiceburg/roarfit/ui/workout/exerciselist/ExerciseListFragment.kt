package at.spiceburg.roarfit.ui.workout.exerciselist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.ui.workout.exerciseinfo.ExerciseInfoFragment
import kotlinx.android.synthetic.main.fragment_exercise_list.*

class ExerciseListFragment : Fragment() {

    private lateinit var viewModel: ExerciseListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, ExerciseListViewModel.Factory(requireContext()))
            .get(ExerciseListViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        val args = ExerciseListFragmentArgs.fromBundle(requireArguments())
        val equipment = args.equipment
        text_exerciselist_title.text = equipment.string

        val onExerciseClicked: (exerciseTemplate: ExerciseTemplate) -> Unit = { exerciseTemplate ->
            val action =
                ExerciseListFragmentDirections.actionExerciseListFragmentToExerciseInfoFragment(
                    exerciseTemplate
                )
            findNavController().navigate(action)
        }

        val adapter = ExerciseListAdapter(requireContext(), onExerciseClicked)
        recyclerview_exerciselist_exercises.adapter = adapter
        recyclerview_exerciselist_exercises.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getExerciseTemplates(equipment).observe(this, Observer { exerciseTemplates ->
            adapter.setExerciseTemplates(exerciseTemplates)
        })
    }

    companion object {
        private val TAG = ExerciseInfoFragment::class.java.simpleName
    }
}
