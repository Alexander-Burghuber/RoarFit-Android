package at.spiceburg.roarfit.features.main.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.viewpager.widget.ViewPager
import at.spiceburg.roarfit.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {

    private val viewModel: StatisticsViewModel by activityViewModels()
    private val dateFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup the tab bar
        viewPager = view.findViewById(R.id.pager_statistics)
        viewPager.adapter = PagerAdapter(childFragmentManager)

        val tabs: TabLayout = view.findViewById(R.id.tabs_statistics)
        tabs.setupWithViewPager(viewPager)

        // change month buttons
        button_statistics_left.setOnClickListener {
            viewModel.updateCalendarMonth(-1)
        }

        button_statistics_right.setOnClickListener {
            viewModel.updateCalendarMonth(1)
        }

        // observe if the month has been changed
        viewModel.calendar.observe(viewLifecycleOwner) { calendar ->
            // load the exercises of this week
            viewModel.loadExercisesOfMonth(calendar.time)
            setMonthLabel(calendar)
        }
    }

    private fun setMonthLabel(calendar: Calendar) {
        text_statistics_month.text = dateFormatter.format(calendar.time)
    }

    private inner class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    tabs_statistics.getTabAt(position)
                        ?.setIcon(R.drawable.ic_access_time_black_24dp)
                    TimeSpentFragment()
                }
                else -> {
                    tabs_statistics.getTabAt(position)
                        ?.setIcon(R.drawable.ic_fitness_center_black_24dp)
                    WeightFragment()
                }
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.statistics_tab_timespent)
                1 -> getString(R.string.statistics_tab_weightdev)
                else -> "N/A"
            }
        }
    }
}
