package at.spiceburg.roarfit.features.main.statistics

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.features.main.MainActivity
import at.spiceburg.roarfit.features.main.MainViewModel
import at.spiceburg.roarfit.utils.Constants
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class StatisticsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = (requireActivity() as MainActivity)
        val sp = activity.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        val jwt: String = sp.getString(Constants.JWT, null)!!

        val barChart: BarChart = requireView().findViewById(R.id.statistics_timespent)

        val entries: List<BarEntry> =
            arrayListOf(BarEntry(0f, 1f), BarEntry(1f, 3f), BarEntry(2f, 2f))
        val dataSet = BarDataSet(entries, "BarDataSet")
        barChart.data = BarData(dataSet)

        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)
        barChart.setViewPortOffsets(60f, 0f, 50f, 150f)

        val xAxis = barChart.xAxis
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f

        val weekDays: Array<String> = resources.getStringArray(R.array.statistics_weekdays)
        val formatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                return weekDays[barEntry?.x?.toInt() ?: 0]
            }
        }
        xAxis.valueFormatter = formatter

        barChart.invalidate()
        //barChart.animateXY(250, 250)
    }
}
