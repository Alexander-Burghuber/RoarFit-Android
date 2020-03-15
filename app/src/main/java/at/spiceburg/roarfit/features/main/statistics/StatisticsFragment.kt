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
    private lateinit var viewPager: ViewPager
    private val dateFormatter = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewPager = requireView().findViewById(R.id.pager_statistics)
        viewPager.adapter = PagerAdapter(childFragmentManager)

        val tabs: TabLayout = requireView().findViewById(R.id.tabs_statistics)
        tabs.setupWithViewPager(viewPager)

        button_statistics_left.setOnClickListener {
            viewModel.updateCalendarWeekOfYear(-1)
        }

        button_statistics_right.setOnClickListener {
            viewModel.updateCalendarWeekOfYear(1)
        }

        viewModel.calendar.observe(viewLifecycleOwner) { calendar ->
            setWeekLabel(calendar)
        }
    }

    private fun setWeekLabel(calendar: Calendar) {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val monday: String = dateFormatter.format(calendar.time)

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val sunday: String = dateFormatter.format(calendar.time)

        text_statistics_week.text =
            getString(R.string.statistics_timespent_week_divider, monday, sunday)
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
