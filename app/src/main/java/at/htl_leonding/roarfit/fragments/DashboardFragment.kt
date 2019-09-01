package at.htl_leonding.roarfit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import at.htl_leonding.roarfit.R
import at.htl_leonding.roarfit.adapters.FitnessScheduleListAdapter
import at.htl_leonding.roarfit.model.FitnessSchedule
import at.htl_leonding.roarfit.viewmodels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*

/**
 * A simple [Fragment] subclass.
 *
 */
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
        val adapter = FitnessScheduleListAdapter(requireContext())
        fitness_schedule_list.adapter = adapter
        fitness_schedule_list.layoutManager = LinearLayoutManager(requireContext())

        adapter.setFitnessSchedules(
            listOf(
                FitnessSchedule("Fitness schedule example 1"),
                FitnessSchedule("Fitness schedule example 2")
            )
        )
    }
}