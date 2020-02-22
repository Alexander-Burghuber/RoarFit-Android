package at.spiceburg.roarfit.features.main.dashboard

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseSpecification
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d(TAG, "onActivityCreated called")

        val activity = (requireActivity() as MainActivity)
        val sp = activity.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        val jwt: String = sp.getString(Constants.JWT, null)!!

        val onExerciseClicked: (exercise: ExerciseSpecification) -> Unit = {
            val action = DashboardFragmentDirections.actionDashboardToExerciseInfo(null, it)
            findNavController().navigate(action)
        }

        val adapter = WorkoutsAdapter(activity, onExerciseClicked)
        list_dashboard_workoutplans.adapter = adapter
        list_dashboard_workoutplans.layoutManager = LinearLayoutManager(activity)

        refresher_dashboard.setColorSchemeColors(resources.getColor(R.color.primary, null))

        viewModel.getWorkoutPlans(jwt).observe(viewLifecycleOwner) { res ->
            when {
                res.isSuccess() -> {
                    val workoutPlans: Array<WorkoutPlan> = res.data!!
                    if (workoutPlans.isNotEmpty()) {
                        val workoutPlan = workoutPlans[0]
                        text_dashboard_name.text = workoutPlan.name
                        adapter.addWorkouts(workoutPlan.workouts)

                        constraintlayout_dashboard.visibility = View.VISIBLE
                        constraintlayout_dashboard_empty.visibility = View.INVISIBLE
                    } else {
                        constraintlayout_dashboard.visibility = View.INVISIBLE
                        constraintlayout_dashboard_empty.visibility = View.VISIBLE
                    }
                    // to remove flickering animation
                    Handler().postDelayed({
                        refresher_dashboard?.isRefreshing = false
                    }, 750)
                }
                res.isLoading() -> {
                    refresher_dashboard.isRefreshing = true
                }
                else -> {
                    refresher_dashboard.isRefreshing = false
                    constraintlayout_dashboard.visibility = View.INVISIBLE
                    activity.handleNetworkError(res.error!!)
                }
            }
        }

        refresher_dashboard.setOnRefreshListener {
            viewModel.loadWorkoutPlans(jwt)
        }
    }

    companion object {
        private val TAG = DashboardFragment::class.java.simpleName
    }
}
