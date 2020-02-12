package at.spiceburg.roarfit.features.main.exerciselist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.Response
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.features.main.MainActivity
import at.spiceburg.roarfit.features.main.MainViewModel
import at.spiceburg.roarfit.features.main.exerciseinfo.ExerciseInfoFragment
import at.spiceburg.roarfit.utils.Constants
import kotlinx.android.synthetic.main.fragment_exercise_list.*

class ExerciseListFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_list, container, false)
    }

    override fun onStart() {
        super.onStart()

        val args = ExerciseListFragmentArgs.fromBundle(requireArguments())
        val equipment: String = args.equipment

        text_exerciselist_title.text = equipment

        val onExerciseClicked: (exerciseTemplate: ExerciseTemplate) -> Unit = { exerciseTemplate ->
            val action =
                ExerciseListFragmentDirections.actionExerciseListToExerciseInfo(exerciseTemplate)
            findNavController().navigate(action)
        }

        val adapter = ExerciseListAdapter(onExerciseClicked, requireContext())
        recyclerview_exerciselist_exercises.adapter = adapter
        recyclerview_exerciselist_exercises.layoutManager = LinearLayoutManager(requireContext())

        val activity = (requireActivity() as MainActivity)
        val sp = activity.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        val jwt: String = sp.getString(Constants.JWT, null)!!

        viewModel.getExerciseTemplates(jwt, equipment).observe(this) { res ->
            when (res) {
                is Response.Success -> {
                    val templates: Array<ExerciseTemplate> = res.data!!
                    adapter.setExerciseTemplates(templates)
                }
                is Response.Loading -> {
                    // todo
                }
                is Response.Error -> {
                    // todo
                }
            }
        }

        /*val onExerciseClicked: (exerciseTemplate: ExerciseTemplate) -> Unit = { exerciseTemplate ->
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
        } */
    }

    companion object {
        private val TAG = ExerciseInfoFragment::class.java.simpleName
    }
}
