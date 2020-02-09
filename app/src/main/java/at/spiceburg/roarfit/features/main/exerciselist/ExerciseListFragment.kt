package at.spiceburg.roarfit.features.main.exerciselist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import at.spiceburg.roarfit.MyApplication
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.features.main.exerciseinfo.ExerciseInfoFragment
import kotlinx.android.synthetic.main.fragment_exercise_list.*

class ExerciseListFragment : Fragment() {

    private lateinit var viewModel: ExerciseListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appContainer = (requireActivity().application as MyApplication).appContainer
        viewModel = ViewModelProvider(
            this,
            ExerciseListViewModel.Factory()
        ).get(ExerciseListViewModel::class.java)

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

        val adapter = ExerciseListAdapter(onExerciseClicked, requireContext())
        recyclerview_exerciselist_exercises.adapter = adapter
        recyclerview_exerciselist_exercises.layoutManager = LinearLayoutManager(requireContext())

        // fixme
        viewModel.getExerciseTemplates(equipment)?.observe(this) { exerciseTemplates ->
            adapter.setExerciseTemplates(exerciseTemplates)
        }
    }

    companion object {
        private val TAG = ExerciseInfoFragment::class.java.simpleName
    }
}
