package at.spiceburg.roarfit.features.exercise

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.ErrorType
import at.spiceburg.roarfit.data.Response
import at.spiceburg.roarfit.data.entities.ExerciseSpecification
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_finish_exercise.*

class FinishExerciseFragment : Fragment() {

    private val viewModel: ExerciseViewModel by activityViewModels()
    private lateinit var activity: ExerciseActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finish_exercise, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        progress_finishexercise.hide()


        activity = (requireActivity() as ExerciseActivity)

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
                } else {
                    input.setText(getString(R.string.finishexercise_min_secs))
                }
            }
        }
        input_finishexercise_min.onFocusChangeListener = timeFocusChangeListener
        input_finishexercise_seconds.onFocusChangeListener = timeFocusChangeListener

        activity.specification?.let {
            input_finishexercise_sets.setText(it.sets)
            input_finishexercise_reps.setText(it.reps)
            input_finishexercise_weight.setText(it.weight)
        }

        constraintlayout_finishexercise.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hideKeyboard()
            }
        }

        button_finishexercise_complete.setOnClickListener {
            val sets: Int? = input_finishexercise_sets.text.toString().toIntOrNull()
            val reps: Int? = input_finishexercise_reps.text.toString().toIntOrNull()
            val weight: String? = input_finishexercise_weight.text.toString()
            val min: Int? = input_finishexercise_min.text.toString().toIntOrNull()
            val secs: Int? = input_finishexercise_seconds.text.toString().toIntOrNull()

            if (sets != null && reps != null && min != null && secs != null) {
                if (activity.specification != null) {
                    // workout exercise
                    val specification: ExerciseSpecification = activity.specification!!
                    addExercise(
                        sets, reps, weight,
                        min, secs, exerciseId = specification.exercise.id
                    )
                } else if (activity.template != null) {
                    // personal exercise
                    val template: ExerciseTemplate = activity.template!!
                    addExercise(sets, reps, weight, min, secs, templateId = template.id)
                }
            } else {
                hideKeyboard()
                displaySnackbar(getString(R.string.finishexercise_inputs_invalid))
            }
        }
    }

    private fun addExercise(
        sets: Int,
        reps: Int,
        weight: String?,
        min: Int,
        secs: Int,
        templateId: Int? = null,
        exerciseId: Int? = null
    ) {
        val sp = activity.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        val jwt: String = sp.getString(Constants.JWT, null)!!

        if (templateId != null) {
            viewModel.addPersonalExercise(
                jwt, templateId, "$min:$secs", sets, reps, weight
            ).observe(viewLifecycleOwner) { res -> handleNetworkResponse(res) }
        } else if (exerciseId != null) {
            viewModel.addWorkoutExercise(
                jwt, exerciseId, "$min:$secs", sets, reps, weight
            ).observe(viewLifecycleOwner) { res -> handleNetworkResponse(res) }
        }
    }

    private fun handleNetworkResponse(res: Response<Unit>) {
        when (res) {
            is Response.Success -> {
                progress_finishexercise.hide()
                activity.finishExercise()
            }
            is Response.Loading -> {
                progress_finishexercise.show()
            }
            is Response.Error -> {
                progress_finishexercise.hide()
                when (res.errorType) {
                    ErrorType.SERVER_UNREACHABLE -> displaySnackbar(getString(R.string.networkerror_server_unreachable))
                    ErrorType.TIMEOUT -> displaySnackbar(getString(R.string.networkerror_timeout))
                    ErrorType.INVALID_INPUT -> displaySnackbar(getString(R.string.finishexercise_inputs_invalid))
                    ErrorType.EXERCISE_ALREADY_COMPLETED -> {
                        displaySnackbar(getString(R.string.finishexercise_exercise_already_completed))
                        activity.finishExercise()
                    }
                    ErrorType.JWT_EXPIRED -> {
                        displayToast(getString(R.string.networkerror_jwt_expired))
                        activity.logout()
                    }
                    else -> {
                        displayToast(getString(R.string.networkerror_unexpected))
                        activity.logout()
                    }
                }
            }
        }
    }

    private fun displaySnackbar(text: String) {
        Snackbar.make(coordinatorlayout_finishexercise, text, Snackbar.LENGTH_LONG)
            .setAction("Dismiss") {}
            .setAnchorView(button_finishexercise_complete)
            .show()
    }

    private fun displayToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
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
