package at.spiceburg.roarfit.features.main.exerciseinfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseSpecification
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.features.exercise.ExerciseActivity
import kotlinx.android.synthetic.main.fragment_exercise_info.*

class ExerciseInfoFragment : Fragment() {

    private var startingExercise = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = ExerciseInfoFragmentArgs.fromBundle(requireArguments())
        when {
            args.specification != null -> specificationIsSet(args.specification!!)
            args.template != null -> templateIsSet(args.template!!)
            else -> throw RuntimeException("No argument is specified for Exercise Info Fragment")
        }
    }

    private fun specificationIsSet(specification: ExerciseSpecification) {
        setupTemplate(specification.exercise.template)

        // set sets
        text_exerciseinfo_sets.text = getString(R.string.exerciseinfo_sets, specification.sets)

        // set reps
        text_exerciseinfo_reps.text = getString(R.string.exerciseinfo_reps, specification.reps)

        // set weight if available
        val weight: Float = specification.weight
        if (weight != 0.0f) {
            text_exerciseinfo_weight.visibility = View.VISIBLE
            text_exerciseinfo_weight.text = getString(R.string.exerciseinfo_weight, weight)
        }

        // set additional information from the trainer
        specification.info?.let {
            text_exerciseinfo_trainer_additionalinfo.visibility = View.VISIBLE
            text_exerciseinfo_trainer_additionalinfo.text = it
        }
        constraintlayout_exerciseinfo_trainer.visibility = View.VISIBLE

        button_exerciseinfo_start.setOnClickListener {
            val intent = Intent(requireContext(), ExerciseActivity::class.java)
                .putExtra("specification", specification)
            startExercise(intent)
        }
    }

    private fun templateIsSet(template: ExerciseTemplate) {
        setupTemplate(template)

        button_exerciseinfo_start.setOnClickListener {
            val intent = Intent(requireContext(), ExerciseActivity::class.java)
                .putExtra("template", template)
            startExercise(intent)
        }
    }

    private fun setupTemplate(template: ExerciseTemplate) {
        // set name
        text_exerciseinfo_exercisename.text = template.name

        // set description if available
        template.description?.let {
            text_exerciseinfo_desc.visibility = View.VISIBLE
            text_exerciseinfo_desc.text = it
        }

        // set equipment if available
        if (template.equipment != null) {
            text_exerciseinfo_equipment.text = template.equipment
        } else {
            text_exerciseinfo_equipment.setTextColor(resources.getColor(R.color.grey, null))
            text_exerciseinfo_equipment.text = getString(R.string.exerciseinfo_no_equipment)
        }

        // set body part(s)
        val bodyParts = template.bodyParts
        var bodyPartsStr = ""
        for (i in bodyParts.indices) {
            bodyPartsStr += if (i == 0) bodyParts[i] else ", ${bodyParts[i]}"
        }
        text_exerciseinfo_bodyparts.text = bodyPartsStr

        template.videoUrl?.let {
            button_exerciseinfo_video.visibility = View.VISIBLE
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(it)
            }
            button_exerciseinfo_video.setOnClickListener {
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
    }

    private fun startExercise(intent: Intent) {
        startingExercise = true
        startActivity(intent)
        Handler().postDelayed({
            findNavController().popBackStack(R.id.dashboardFragment, false)
        }, 250)
    }
}
