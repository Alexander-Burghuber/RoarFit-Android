package at.spiceburg.roarfit.features.main.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.Exercise
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.features.main.MainActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_weight.*

class WeightFragment : Fragment() {

    private val viewModel: StatisticsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weight, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = (requireActivity() as MainActivity)

        val lineChart: LineChart = requireView().findViewById(R.id.linechart_statistics_weight)
        configureChart(lineChart)

        viewModel.exercises.observe(viewLifecycleOwner) { res ->
            when {
                res.isSuccess() -> {
                    val exercises: Array<Exercise> = res.data!!
                    if (exercises.isNotEmpty()) {

                        chipgroup_statisitics.removeAllViews()

                        val distinctTemplates: List<ExerciseTemplate> =
                            exercises.filter { it.weight != 0f }.map { it.template }.distinct()

                        val inflater = LayoutInflater.from(requireContext())
                        for (i in distinctTemplates.indices) {
                            inflater.inflate(R.layout.chip_weight, chipgroup_statisitics, true)
                        }

                        chipgroup_statisitics.children.forEachIndexed { i, view ->
                            val template = distinctTemplates[i]
                            (view as Chip).text = template.name
                        }

                        val lineData: LineData = createLineChartData(exercises)
                        lineChart.data = lineData
                        lineChart.invalidate()
                    }
                    activity.progress_main?.hide()
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

    private fun createLineChartData(exercises: Array<Exercise>): LineData {

        val entry = Entry(5f, 5f)

        val dataSet = LineDataSet(listOf(entry), "Time spent")
        dataSet.color = resources.getColor(R.color.primaryLight, null)
        dataSet.setDrawValues(false)

        return LineData(dataSet)
    }


    private fun configureChart(lineChart: LineChart) {
        // general configuration
        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.setExtraOffsets(16f, 0f, 16f, 16f)
        lineChart.setNoDataText(getString(R.string.statististics_nodatatext))
        lineChart.setNoDataTextColor(resources.getColor(R.color.darkGrey, null))

        // interaction
        lineChart.isDoubleTapToZoomEnabled = false
        lineChart.setPinchZoom(false)
        lineChart.setScaleEnabled(false)

        lineChart.axisRight.isEnabled = false
        val axisLeft = lineChart.axisLeft
        axisLeft.gridLineWidth = 0.8f
        axisLeft.setDrawZeroLine(false)
        axisLeft.setDrawAxisLine(false)
        axisLeft.textSize = 14f
        // axisLeft.valueFormatter = YValueFormatter()

        // xAxis
        val xAxis = lineChart.xAxis
        xAxis.setDrawLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.granularity = 1f
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        //xAxis.valueFormatter = XValueFormatter()
        xAxis.textSize = 14f
    }

    companion object {
        private val TAG = WeightFragment::class.java.simpleName
    }
}
