package at.spiceburg.roarfit.features.main.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.features.main.MainViewModel
import com.google.android.material.tabs.TabLayout

class StatisticsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var viewPager: ViewPager

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
    }

    private inner class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> TimeSpentFragment()
                else -> WeightFragment() // todo
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.statistics_tab_timespent)
                else -> "N/A"
            }
        }
    }
}
