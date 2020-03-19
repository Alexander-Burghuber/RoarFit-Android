package at.spiceburg.roarfit.features.main.statistics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.Exercise
import at.spiceburg.roarfit.features.main.MainActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.activity_main.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimeSpentFragment : Fragment() {

    private val viewModel: StatisticsViewModel by activityViewModels()
    private val timeFormatter = SimpleDateFormat("mm:ss", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_spent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barChart: BarChart = requireView().findViewById(R.id.barchart_statistics_timespent)
        configureChart(barChart)

        viewModel.calendar.observe(viewLifecycleOwner) { calendar ->
            barChart.data = null
            barChart.invalidate()
            viewModel.loadExercisesOfWeek(calendar.time)
        }

        val activity = (requireActivity() as MainActivity)

        viewModel.exercises.observe(viewLifecycleOwner) { res ->
            when {
                res.isSuccess() -> {
                    activity.progress_main?.hide()
                    val exercises: Array<Exercise> = res.data!!
                    if (exercises.isNotEmpty()) {
                        val barData: BarData = createBarChartData(exercises)
                        barChart.data = barData
                        barChart.invalidate()
                    }
                }
                res.isLoading() -> {
                    activity.progress_main?.show()
                }
                else -> {
                    activity.progress_main?.hide()
                    activity.handleNetworkError(res.error!!)
                }
            }
        }
    }

    private fun createBarChartData(exercises: Array<Exercise>): BarData {
        val secondsPerDay = IntArray(7)

        val calendar = Calendar.getInstance()

        exercises.forEach { exercise ->
            Log.d(TAG, exercise.toString())

            // get day of week and use as index
            calendar.timeInMillis = exercise.completedDate ?: return@forEach

            // -2 because arrays start with zero and the week starts on sunday with the calendar class
            val index: Int = calendar.get(Calendar.DAY_OF_WEEK) - 2

            // get amount of seconds trained in this exercise
            val date: Date = try {
                timeFormatter.parse(exercise.time)
            } catch (e: ParseException) {
                return@forEach
            }
            calendar.time = date
            val seconds =
                calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND)

            secondsPerDay[index] += seconds
        }

        val entries: List<BarEntry> = secondsPerDay.mapIndexed { i, v ->
            return@mapIndexed BarEntry(i.toFloat(), v.toFloat())
        }

        val dataSet = BarDataSet(entries, "Time spent")
        dataSet.color = resources.getColor(R.color.primaryLight, null)
        dataSet.setDrawValues(false)

        return BarData(dataSet)
    }

    private fun configureChart(barChart: BarChart) {
        // general configuration
        barChart.legend.isEnabled = false
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.setExtraOffsets(16f, 0f, 16f, 16f)
        barChart.setNoDataText(getString(R.string.statististics_nodatatext))
        barChart.setNoDataTextColor(resources.getColor(R.color.darkGrey, null))

        // interaction
        barChart.isDoubleTapToZoomEnabled = false
        barChart.setPinchZoom(false)
        barChart.setScaleEnabled(false)

        barChart.axisRight.isEnabled = false
        val axisLeft = barChart.axisLeft
        axisLeft.gridLineWidth = 0.8f
        axisLeft.setDrawZeroLine(false)
        axisLeft.setDrawAxisLine(false)
        axisLeft.textSize = 14f
        axisLeft.valueFormatter = YValueFormatter()

        // xAxis
        val xAxis = barChart.xAxis
        xAxis.setDrawLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.granularity = 1f
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = XValueFormatter()
        xAxis.textSize = 14f
    }

    inner class XValueFormatter : ValueFormatter() {
        private val weekDays: Array<String> = resources.getStringArray(R.array.statistics_weekdays)

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return weekDays[value.toInt()]
        }
    }

    inner class YValueFormatter : ValueFormatter() {
        private val calendar = Calendar.getInstance()

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            calendar.clear()
            calendar.set(Calendar.SECOND, value.toInt())
            return timeFormatter.format(calendar.time)
        }
    }

    companion object {
        private val TAG = TimeSpentFragment::class.java.simpleName
    }
}
