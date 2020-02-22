package at.spiceburg.roarfit.features.exercise

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.MyApplication
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseSpecification
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.features.auth.AuthActivity
import at.spiceburg.roarfit.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class ExerciseActivity : AppCompatActivity() {

    lateinit var viewModel: ExerciseViewModel
    private lateinit var service: ExerciseService
    private var bound = false
    private var stopWatchObserver: Disposable? = null

    var specification: ExerciseSpecification? = null
    var template: ExerciseTemplate? = null

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected called")
            val binder = service as ExerciseService.LocalBinder
            this@ExerciseActivity.service = binder.getService()
            bound = true
            observeStopwatch()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "onServiceDisconnected called")
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
            ExerciseViewModel.Factory(appContainer.workoutRepository)
        ).get(ExerciseViewModel::class.java)

        val serviceIntent = Intent(this, ExerciseService::class.java)
        when {
            intent.hasExtra("specification") -> {
                val specification: ExerciseSpecification =
                    intent.getSerializableExtra("specification") as ExerciseSpecification

                this.specification = specification

                serviceIntent.putExtra("templateName", specification.exercise.template.name)
            }
            intent.hasExtra("template") -> {
                val template: ExerciseTemplate =
                    intent.getSerializableExtra("template") as ExerciseTemplate

                this.template = template

                serviceIntent.putExtra("templateName", template.name)
            }
            else -> throw RuntimeException("ExerciseActivity cannot be started. No valid intent extra has been passed")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume called")

        val intent = Intent(this, ExerciseService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
        doUnbindService()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")

        if (isFinishing) {
            doStopService()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.exercise_backpress_warning_title))
                .setMessage(getString(R.string.exercise_backpress_warning_msg))
                .setPositiveButton("Ok") { _, _ ->
                    doUnbindService()
                    stopService(Intent(this, ExerciseService::class.java))
                    super.onBackPressed()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .show()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    fun fragPauseClick() {
        if (bound) {
            if (service.isStopWatchRunning()) {
                // pause the stopWatch
                service.pauseStopWatch()
                stopWatchObserver?.dispose()
                viewModel.isStopWatchPaused.value = true
            } else {
                service.continueStopWatch()
                observeStopwatch()
                viewModel.isStopWatchPaused.value = false
            }
        }
    }

    fun fragFinishClick() {
        if (bound) {
            if (service.isStopWatchRunning()) {
                // pause the stopWatch
                service.pauseStopWatch()
                stopWatchObserver?.dispose()
                viewModel.isStopWatchPaused.value = true
            }
        }

        openFinishExerciseFragment()
    }

    fun fragResetClick() {
        resetStopWatch()
    }

    fun finishExercise() {
        doStopService()
        finish()
    }

    fun logout() {
        doStopService()
        getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE).edit()
            .remove(Constants.USERNAME)
            .remove(Constants.JWT)
            .remove(Constants.USER_ID)
            .remove(Constants.ENCRYPTED_PWD)
            .remove(Constants.INITIALIZATION_VECTOR)
            .apply()
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun observeStopwatch() {
        Log.d(TAG, "observeStopWatch called")

        stopWatchObserver =
            service.stopwatch?.observeOn(AndroidSchedulers.mainThread())?.subscribe { time ->
                viewModel.stopWatch.value = time
            }
    }

    private fun doUnbindService() {
        stopWatchObserver?.dispose()
        if (bound) {
            unbindService(connection)
            bound = false
        }
    }

    private fun doStopService() {
        doUnbindService()
        stopService(Intent(this, ExerciseService::class.java))
    }

    private fun resetStopWatch() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.exercise_reset_warning_title))
            .setPositiveButton(getString(R.string.exercise_reset_warning_yes)) { _, _ ->
                if (bound) {
                    service.resetStopWatch()
                    if (!service.isStopWatchRunning()) {
                        viewModel.stopWatch.value = getString(R.string.exercise_time)
                    }
                }
            }
            .setNegativeButton(getString(R.string.exercise_reset_warning_no)) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun openFinishExerciseFragment() {
        val finishExerciseFragment = FinishExerciseFragment()
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_exercise_container, finishExerciseFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private val TAG = ExerciseActivity::class.java.simpleName
    }
}
