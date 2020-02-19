package at.spiceburg.roarfit.features.exercise

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.MyApplication
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseSpecification
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_exercise.*

class ExerciseActivity : AppCompatActivity() {

    private lateinit var viewModel: ExerciseViewModel
    private lateinit var service: ExerciseService
    private var bound = false
    private var disposableStopwatch: Disposable? = null

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as ExerciseService.LocalBinder
            this@ExerciseActivity.service = binder.getService()
            bound = true
            observeStopwatch()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "onServiceDisconnected")
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        Log.d(TAG, "onCreate called")

        // setup viewModel
        val appContainer = (application as MyApplication).appContainer
        viewModel = ViewModelProvider(
            this,
            ExerciseViewModel.Factory()
        ).get(ExerciseViewModel::class.java)

        val serviceIntent = Intent(this, ExerciseService::class.java)
        when {
            intent.hasExtra("specification") -> {
                val specification: ExerciseSpecification =
                    intent.getSerializableExtra("specification") as ExerciseSpecification

                val template: ExerciseTemplate = specification.exercise.template
                setupTemplateViews(template)

                setupSpecificationsViews(specification)

                serviceIntent.putExtra("templateName", template.name)
            }
            intent.hasExtra("template") -> {
                val template: ExerciseTemplate =
                    intent.getSerializableExtra("template") as ExerciseTemplate

                setupTemplateViews(template)

                serviceIntent.putExtra("templateName", template.name)
            }
            else -> throw RuntimeException("ExerciseActivity cannot be started. No valid intent extra has been passed")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        button_exercise_pause.setOnClickListener {
            if (bound) {
                if (service.pauseOrContinueStopwatch()) {
                    button_exercise_pause.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.ic_play_arrow_black_24dp,
                            null
                        )
                    )
                    disposableStopwatch?.dispose()
                } else {
                    button_exercise_pause.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.ic_pause_black_24dp,
                            null
                        )
                    )
                    observeStopwatch()
                }
            }
        }

        button_exercise_finish.setOnClickListener {
            unbindService(connection)
            stopService(Intent(this, ExerciseService::class.java))
            finish()
        }

        button_exercise_reset.setOnClickListener {
            if (bound) {
                service.resetStopWatch()
                if (!service.isStopWatchRunning()) {
                    text_exercise_stopwatch.text = "00:00"
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume called")

        val intent = Intent(this, ExerciseService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        if (bound) {
            observeStopwatch()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "pause isFinishing $isFinishing")
        Log.d(TAG, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
        unbindService(connection)
        disposableStopwatch?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.exercise_backpress_warning_title))
            .setMessage(getString(R.string.exercise_backpress_warning_msg))
            .setPositiveButton("Ok") { _, _ ->
                unbindService(connection)
                stopService(Intent(this, ExerciseService::class.java))
                super.onBackPressed()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun observeStopwatch() {
        disposableStopwatch =
            service.stopwatch?.observeOn(AndroidSchedulers.mainThread())?.subscribe { time ->
                Log.d(TAG, "Stopwatch: $time")
                text_exercise_stopwatch.text = time
            }
    }

    private fun setupTemplateViews(template: ExerciseTemplate) {
        text_exercise_name.text = template.name
        text_exercise_equipment.text = template.equipment
    }

    private fun setupSpecificationsViews(specification: ExerciseSpecification) {
        // set sets
        text_exercise_sets.text = getString(R.string.exerciseinfo_sets, specification.sets)
        // set reps
        text_exercise_reps.text = getString(R.string.exerciseinfo_reps, specification.reps)
        // set weight if available
        specification.weight?.let {
            text_exercise_weight.visibility = View.VISIBLE
            text_exercise_weight.text = getString(R.string.exerciseinfo_weight, it)
        }
        // set additional information from the trainer
        specification.info?.let {
            text_exercise_trainer_additionalinfo.visibility = View.VISIBLE
            text_exercise_trainer_additionalinfo.text = it
        }

        scrollview_exercise_specifications.visibility = View.VISIBLE
    }

    companion object {
        private val TAG = ExerciseActivity::class.java.simpleName
    }
}
