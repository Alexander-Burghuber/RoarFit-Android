package at.spiceburg.roarfit.features.main.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.Response
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import at.spiceburg.roarfit.features.main.MainActivity
import at.spiceburg.roarfit.features.main.MainViewModel
import at.spiceburg.roarfit.utils.Constants
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

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
        val sp = activity.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        val jwt: String = sp.getString(Constants.JWT, null)!!

        val adapter = WorkoutsAdapter(activity)
        list_dashboard_workoutplans.adapter = adapter
        list_dashboard_workoutplans.layoutManager = LinearLayoutManager(activity)

        viewModel.getWorkoutPlans(jwt).observe(this) { res ->
            when (res) {
                is Response.Success -> {
                    progress_dashboard.visibility = View.GONE

                    val workoutPlan: WorkoutPlan = res.data!!
                    text_dashboard_name.text = workoutPlan.name

                    workoutPlan.workouts.forEach { workout ->
                        viewModel.getExercisesOfWorkout(jwt, workout.id).observe(this) { res ->
                            if (res is Response.Success) {
                                workout.userExercises = res.data!!
                                adapter.addWorkout(workout)
                            } else if (res is Response.Error) {
                                if (res.logout == true) {
                                    activity.logout(true)
                                } else {
                                    activity.displaySnackbar(res.message!!)
                                }
                            }
                        }
                    }
                }
                is Response.Loading -> {
                    progress_dashboard.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    if (res.logout == true) {
                        activity.logout(true)
                    } else {
                        activity.displaySnackbar(res.message!!)
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = DashboardFragment::class.java.simpleName
    }
}
