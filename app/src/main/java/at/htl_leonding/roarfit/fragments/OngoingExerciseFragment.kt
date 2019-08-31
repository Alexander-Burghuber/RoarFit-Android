package at.htl_leonding.roarfit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import at.htl_leonding.roarfit.R
import kotlinx.android.synthetic.main.fragment_ongoing_exercise.*
import java.text.SimpleDateFormat
import java.util.*

class OngoingExerciseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ongoing_exercise, container, false)
    }

    override fun onStart() {
        super.onStart()
        val args = OngoingExerciseFragmentArgs.fromBundle(requireArguments())
        val equipment = args.equipment
        ongoing_exercise_title.text = equipment.toString()

        Timer().scheduleAtFixedRate(object : TimerTask() {
            val startTime = System.currentTimeMillis()
            val formatter = SimpleDateFormat("mm:ss", Locale.ENGLISH)
            override fun run() {
                ongoing_exercise_timer.post {
                    ongoing_exercise_timer.text = formatter.format(Date().time - startTime)
                }
            }
        }, 0, 1000)
    }
}