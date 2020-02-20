package at.spiceburg.roarfit.features.exercise

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseSpecification
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import kotlinx.android.synthetic.main.fragment_finish_exercise.*

class FinishExerciseFragment : Fragment() {

    private val viewModel: ExerciseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finish_exercise, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity: ExerciseActivity = (requireActivity() as ExerciseActivity)

        // setup toolbar
        val toolbar = view?.findViewById<Toolbar>(R.id.toolbar_exercise)
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            activity.onBackPressed()
        }

        viewModel.stopWatch.observe(viewLifecycleOwner) { time ->
            val units: List<String> = time.split(":")
            input_finishexercise_min.setText(units[0])
            input_finishexercise_seconds.setText(units[1])
        }

        val timeFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val input = v as EditText
                val text = input.text
                if (!text.isNullOrBlank()) {
                    val number = text.toString().toInt()
                    if (number > 59) {
                        val errorColor = resources.getColor(R.color.error, null)
                        input.setTextColor(errorColor)
                    } else {
                        val blackColor = resources.getColor(R.color.black, null)
                        input.setTextColor(blackColor)
                    }

                    if (number < 10) {
                        input.setText(getString(R.string.finishexercise_time_add_zero, number))
                    }
                }
            }
        }
        input_finishexercise_min.onFocusChangeListener = timeFocusChangeListener
        input_finishexercise_seconds.onFocusChangeListener = timeFocusChangeListener

        val intent = activity.intent
        when {
            intent.hasExtra("specification") -> {
                val specification: ExerciseSpecification =
                    intent.getSerializableExtra("specification") as ExerciseSpecification
                val template: ExerciseTemplate = specification.exercise.template

                input_finishexercise_sets.setText(specification.sets)
                input_finishexercise_reps.setText(specification.reps)
                input_finishexercise_weight.setText(specification.weight)
            }
            intent.hasExtra("template") -> {
                val template: ExerciseTemplate =
                    intent.getSerializableExtra("template") as ExerciseTemplate
            }
            else -> throw RuntimeException("ExerciseActivity cannot be started. No valid intent extra has been passed")
        }

        constraintlayout_finishexercise.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        val activity = requireActivity()
        val inputManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    companion object {
        private val TAG = FinishExerciseFragment::class.java.simpleName
    }
}
