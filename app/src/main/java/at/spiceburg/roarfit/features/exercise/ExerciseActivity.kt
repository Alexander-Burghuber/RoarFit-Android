package at.spiceburg.roarfit.features.exercise

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.MyApplication
import at.spiceburg.roarfit.R
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

        // setup viewModel
        val appContainer = (application as MyApplication).appContainer
        viewModel = ViewModelProvider(
            this,
            ExerciseViewModel.Factory()
        ).get(ExerciseViewModel::class.java)

        // set view elements
        val exerciseTemplate: ExerciseTemplate =
            intent.getSerializableExtra("template") as ExerciseTemplate
        text_exercise_equipment.text = exerciseTemplate.equipment
        text_exercise_name.text = exerciseTemplate.name

        val serviceIntent = Intent(this, ExerciseService::class.java).apply {
            putExtra("templateName", exerciseTemplate.name)
        }

        startService(serviceIntent)

        button_exercise_pause.setOnClickListener {
            if (bound) {
                if (service.pauseOrContinueStopwatch()) {
                    button_exercise_pause.text = getString(R.string.exercise_button_continue)
                    disposableStopwatch?.dispose()
                } else {
                    button_exercise_pause.text = getString(R.string.exercise_button_pause)
                    observeStopwatch()
                }
            }
        }

        button_exercise_finish.setOnClickListener {
            unbindService(connection)
            stopService(Intent(this, ExerciseService::class.java))
            finish()
        }

        Handler().postDelayed({
            button_exercise_finish.isEnabled = true
        }, 500)
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, ExerciseService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        if (bound) {
            observeStopwatch()
        }
    }

    override fun onPause() {
        super.onPause()
        disposableStopwatch?.dispose()
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Are you sure you want to stop the exercise?")
            .setMessage("The progress will NOT be saved.")
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

    companion object {
        private val TAG = ExerciseActivity::class.java.simpleName
    }
}
