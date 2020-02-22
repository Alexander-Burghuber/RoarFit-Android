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
import at.spiceburg.roarfit.data.NetworkError
import at.spiceburg.roarfit.data.Result
import at.spiceburg.roarfit.data.dto.PersonalExerciseDTO
import at.spiceburg.roarfit.data.dto.WorkoutExerciseDTO
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
            input_finishexercise_secs.setText(units[1])
        }

        val timeFocusListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val input = v as EditText
                val text = input.text
                if (!text.isNullOrBlank()) {
                    val number = text.toString().toInt()
                    if (validateMinOrSecs(number)) {
                        val blackColor = resources.getColor(R.color.black, null)
                        input.setTextColor(blackColor)
                    } else {
                        val errorColor = resources.getColor(R.color.error, null)
                        input.setTextColor(errorColor)
                    }
                    if (number < 10) {
                        input.setText(getString(R.string.finishexercise_time_add_zero, number))
                    }
                } else {
                    input.setText(getString(R.string.finishexercise_min_secs))
                }
            }
        }
        input_finishexercise_min.onFocusChangeListener = timeFocusListener
        input_finishexercise_secs.onFocusChangeListener = timeFocusListener

        if (activity.specification != null) {
            val specification: ExerciseSpecification = activity.specification!!

            textinputlayout_finishexercise_sets.helperText =
                getString(R.string.finishexercise_exercise_helpertext_sets, specification.sets)
            textinputlayout_finishexercise_reps.helperText =
                getString(R.string.finishexercise_exercise_helpertext_reps, specification.reps)

            if (specification.weight != null) {
                textinputlayout_finishexercise_weight.helperText = getString(
                    R.string.finishexercise_exercise_helpertext_weight,
                    specification.weight
                )
            } else {
                input_finishexercise_weight.visibility = View.GONE
            }

            setupTemplateViews(specification.exercise.template)
        } else if (activity.template != null) {
            setupTemplateViews(activity.template!!)
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
            val secs: Int? = input_finishexercise_secs.text.toString().toIntOrNull()

            if (sets != null) {
                textinputlayout_finishexercise_sets.isErrorEnabled = false
                textinputlayout_finishexercise_sets.isHelperTextEnabled = true

                if (reps != null) {
                    textinputlayout_finishexercise_reps.isErrorEnabled = false
                    textinputlayout_finishexercise_reps.isHelperTextEnabled = true

                    if (input_finishexercise_weight.visibility == View.GONE || !weight.isNullOrBlank()) {
                        textinputlayout_finishexercise_weight.isErrorEnabled = false
                        textinputlayout_finishexercise_weight.isHelperTextEnabled = true

                        if (min != null && validateMinOrSecs(min)) {
                            val blackColor = resources.getColor(R.color.black, null)
                            input_finishexercise_min.setTextColor(blackColor)

                            if (secs != null && validateMinOrSecs(secs)) {
                                input_finishexercise_secs.setTextColor(blackColor)

                                if (activity.specification != null) {
                                    // workout exercise
                                    val specification: ExerciseSpecification =
                                        activity.specification!!
                                    addExercise(
                                        sets, reps, weight,
                                        min, secs, exerciseId = specification.exercise.id
                                    )
                                } else if (activity.template != null) {
                                    // personal exercise
                                    val template: ExerciseTemplate = activity.template!!
                                    addExercise(
                                        sets,
                                        reps,
                                        weight,
                                        min,
                                        secs,
                                        templateId = template.id
                                    )
                                }
                            } else {
                                val errorColor = resources.getColor(R.color.error, null)
                                input_finishexercise_secs.setTextColor(errorColor)
                            }
                        } else {
                            val errorColor = resources.getColor(R.color.error, null)
                            input_finishexercise_min.setTextColor(errorColor)
                        }
                    } else {
                        textinputlayout_finishexercise_weight.error =
                            getString(R.string.finishexercise_exercise_error)
                    }
                } else {
                    textinputlayout_finishexercise_reps.error =
                        getString(R.string.finishexercise_exercise_error)
                }
            } else {
                textinputlayout_finishexercise_sets.error =
                    getString(R.string.finishexercise_exercise_error)
            }
            hideKeyboard()
        }
    }

    private fun validateMinOrSecs(number: Int): Boolean {
        return number <= 59
    }

    private fun setupTemplateViews(template: ExerciseTemplate) {
        text_finishexercise_exercisename.text = template.name

        val equipment: String? = template.equipment
        if (equipment != null) {
            text_finishexercise_equipment.text = equipment
        } else {
            text_finishexercise_equipment.setTextColor(resources.getColor(R.color.grey, null))
            text_finishexercise_equipment.text = getString(R.string.exerciseinfo_no_equipment)
        }

        // set body part(s)
        val bodyParts = template.bodyParts
        var bodyPartsStr = ""
        for (i in bodyParts.indices) {
            bodyPartsStr += if (i == 0) bodyParts[i] else ", ${bodyParts[i]}"
        }
        text_finishexercise_bodyparts.text =
            getString(R.string.finishexercise_exercise_bodyparts, bodyPartsStr)
    }

    private fun addExercise(
        sets: Int, reps: Int, weight: String?, min: Int, secs: Int,
        templateId: Int? = null, exerciseId: Int? = null
    ) {
        val sp = activity.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        val jwt: String = sp.getString(Constants.JWT, null)!!

        if (templateId != null) {
            val dto = PersonalExerciseDTO(templateId, "$min:$secs", sets, reps, weight)

            viewModel.addPersonalExercise(jwt, dto)
                .observe(viewLifecycleOwner) { handleNetworkResponse(it) }
        } else if (exerciseId != null) {
            val dto = WorkoutExerciseDTO(exerciseId, "$min:$secs", sets, reps, weight)
            viewModel.addWorkoutExercise(jwt, dto)
                .observe(viewLifecycleOwner) { res -> handleNetworkResponse(res) }
        }
    }

    private fun handleNetworkResponse(res: Result<Unit>) {
        when {
            res.isSuccess() -> {
                progress_finishexercise.hide()
                activity.finishExercise()
            }
            res.isLoading() -> {
                progress_finishexercise.show()
            }
            else -> {
                progress_finishexercise.hide()
                when (res.error) {
                    NetworkError.SERVER_UNREACHABLE -> displaySnackbar(getString(R.string.networkerror_server_unreachable))
                    NetworkError.TIMEOUT -> displaySnackbar(getString(R.string.networkerror_timeout))
                    NetworkError.EXERCISE_ALREADY_COMPLETED -> {
                        displayToast(getString(R.string.finishexercise_exercise_already_completed))
                        activity.finishExercise()
                    }
                    NetworkError.JWT_EXPIRED -> {
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
