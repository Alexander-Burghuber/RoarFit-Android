package at.spiceburg.roarfit.features.main.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.Status
import at.spiceburg.roarfit.features.main.MainActivity
import at.spiceburg.roarfit.features.main.MainViewModel
import at.spiceburg.roarfit.utils.Constants
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onStart() {
        super.onStart()

        val activity = (requireActivity() as MainActivity)
        viewModel = activity.viewModel
        val sp = activity.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        val jwt: String = sp.getString("jwt", null)!!

        val adapter = WorkoutsAdapter(activity)
        list_dashboard_workoutplans.adapter = adapter
        list_dashboard_workoutplans.layoutManager = LinearLayoutManager(activity)

        viewModel.workoutPlans.observe(this) { workoutPlans ->
            if (workoutPlans != null && workoutPlans.isNotEmpty()) {
                val workoutPlan = workoutPlans[0]
                text_dashboard_name.text = workoutPlan.name
                viewModel.getWorkoutsOfPlan(workoutPlan.id).observe(this) { workouts ->

                    workouts?.forEach { workout ->
                        viewModel.getAllExerciseTemplates().observe(this) { templates ->
                            viewModel.getExercisesOfWorkout(workout.id)
                                .observer(this) { exercises ->
                                    exercises?.foreach {
                                        val template =
                                            templates.find { it.id == exercise.templateId }

                                    }
                                }
                        }
                    }

                    if (workouts != null) {

                        adapter.setWorkoutPlans(workouts)
                    }
                }
            }
        }

        viewModel.loadWorkoutPlans(jwt).observe(this) { status ->
            when (status) {
                is Status.Success -> progress_dashboard.visibility = View.GONE
                is Status.Loading -> progress_dashboard.visibility = View.VISIBLE
                is Status.Error -> {
                }
            }
        }
    }

    companion object {
        private val TAG = DashboardFragment::class.java.simpleName
    }
}
