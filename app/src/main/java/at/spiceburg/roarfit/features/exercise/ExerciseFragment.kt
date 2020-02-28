package at.spiceburg.roarfit.features.exercise

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseSpecification
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import kotlinx.android.synthetic.main.fragment_exercise.*

class ExerciseFragment : Fragment() {

    private val viewModel: ExerciseViewModel by activityViewModels()
    private lateinit var activity: ExerciseActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d(TAG, "onActivityCreated called")

        activity = requireActivity() as ExerciseActivity

        if (activity.specification != null) {
            val specification: ExerciseSpecification = activity.specification!!
            setupTemplateViews(specification.exercise.template)
            setupSpecificationsViews(specification)
        } else if (activity.template != null) {
            setupTemplateViews(activity.template!!)
        }

        viewModel.isStopWatchPaused.observe(viewLifecycleOwner) { isPaused ->
            val iconId: Int = if (isPaused) {
                R.drawable.ic_play_arrow_black_24dp
            } else {
                R.drawable.ic_pause_black_24dp
            }
            val icon = resources.getDrawable(iconId, null)
            button_exercise_pause.setImageDrawable(icon)
        }

        viewModel.stopWatch.observe(viewLifecycleOwner) { time ->
            text_exercise_stopwatch.text = time
        }

        button_exercise_pause.setOnClickListener {
            activity.fragPauseClick()
        }

        button_exercise_finish.setOnClickListener {
            activity.fragFinishClick()
        }

        button_exercise_reset.setOnClickListener {
            activity.fragResetClick()
        }
    }

    private fun setupTemplateViews(template: ExerciseTemplate) {
        text_exercise_name.text = template.name
        text_exercise_equipment.text = template.equipment
    }

    private fun setupSpecificationsViews(specification: ExerciseSpecification) {
        // set sets
        text_exercise_sets.text = getString(R.string.exerciseinfo_sets, specification.sets)

        // set reps
        text_exercise_reps.text = getString(R.string.exerciseinfo_reps, specification.reps)

        // set weight if available
        val weight: Float = specification.weight
        if (weight != 0.0f) {
            text_exercise_weight.visibility = View.VISIBLE
            text_exercise_weight.text = getString(R.string.exerciseinfo_weight, weight)
        }

        // set additional information from the trainer
        specification.info?.let {
            text_exercise_trainer_additionalinfo.visibility = View.VISIBLE
            text_exercise_trainer_additionalinfo.text = it
        }

        scrollview_exercise_specifications.visibility = View.VISIBLE
    }

    companion object {
        private val TAG = ExerciseFragment::class.java.simpleName
    }
}
