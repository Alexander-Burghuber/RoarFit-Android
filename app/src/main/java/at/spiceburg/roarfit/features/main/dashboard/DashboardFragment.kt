package at.spiceburg.roarfit.features.main.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import at.spiceburg.roarfit.R
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onStart() {
        super.onStart()
        val adapter = WorkoutPlansAdapter(requireContext())
        list_dashboard_workoutplans.adapter = adapter
        list_dashboard_workoutplans.layoutManager = LinearLayoutManager(requireContext())

        // adapter.setWorkoutPlans()
    }
}
