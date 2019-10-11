package at.spiceburg.roarfit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.adapters.WorkoutPlanListAdapter
import at.spiceburg.roarfit.viewmodels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onStart() {
        super.onStart()
        val adapter = WorkoutPlanListAdapter(requireContext())
        list_dashboard_workoutplans.adapter = adapter
        list_dashboard_workoutplans.layoutManager = LinearLayoutManager(requireContext())

        // adapter.setWorkoutPlans()
    }
}
