package at.spiceburg.roarfit.features.main.exerciseinfo

import android.content.Intent
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

    override fun onStart() {
        super.onStart()
        val args = ExerciseInfoFragmentArgs.fromBundle(requireArguments())
        when {
            args.specification != null -> {
                val specification: ExerciseSpecification = args.specification!!
                button_exerciseinfo_start.setOnClickListener {
                    val intent = Intent(requireContext(), ExerciseActivity::class.java)
                        .putExtra("specification", specification)
                    startExercise(intent)
                }
            }
            args.template != null -> {
                val template: ExerciseTemplate = args.template!!
                text_exerciseinfo.text = template.name
                button_exerciseinfo_start.setOnClickListener {
                    val intent = Intent(requireContext(), ExerciseActivity::class.java)
                        .putExtra("template", template)
                    startExercise(intent)
                }
            }
            else -> {
                throw RuntimeException("No argument is specified for Exercise Info Fragment")
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
