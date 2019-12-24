package at.spiceburg.roarfit.features.exercise

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import at.spiceburg.roarfit.MyApplication
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.services.ExerciseService
import kotlinx.android.synthetic.main.activity_exercise.*

class ExerciseActivity : AppCompatActivity() {

    private lateinit var viewModel: ExerciseViewModel

    private var service: Messenger? = null
    private val messenger = Messenger(IncomingHandler())
    private var bound = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, ibinder: IBinder) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.

            service = Messenger(ibinder)
            bound = true

            Log.d(TAG, "onServiceConnected")

            try {
                val msg = Message.obtain(null, ExerciseService.MSG_REGISTER)
                msg.replyTo = messenger
                service?.send(msg)
            } catch (e: RemoteException) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.

            service = null
            bound = false

            Log.d(TAG, "onServiceDisconnected")
        }
    }

    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                ExerciseService.MSG_UPDATE -> {
                    text_exercise_stopwatch.text = msg.obj as String
                }
                ExerciseService.MSG_CHANGE_STATE -> {
                    // true if the stopwatch was paused, false if it continues
                    val state = msg.obj as Boolean

                    // set new button string
                    val stringId = if (state) {
                        R.string.exercise_button_continue
                    } else {
                        R.string.exercise_button_pause
                    }
                    button_exercise_pause.text = getString(stringId)
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        Log.d(TAG, "onCreate")

        // setup viewModel
        val appContainer = (application as MyApplication).appContainer
        viewModel = ViewModelProviders.of(this, appContainer.exerciseViewModelFactory)
            .get(ExerciseViewModel::class.java)

        // set view elements
        val exerciseTemplate = intent.getSerializableExtra("template") as ExerciseTemplate
        text_exercise_equipment.text = exerciseTemplate.equipment?.string
        text_exercise_name.text = exerciseTemplate.name

        // start service
        val startIntent = Intent(this, ExerciseService::class.java)
            .putExtra("template", exerciseTemplate)
        startService(startIntent)

        // bind to service
        if (!bound) {
            Log.d(TAG, "Binding service")
            val bindIntent = Intent(this, ExerciseService::class.java)
            bindService(bindIntent, connection, Context.BIND_AUTO_CREATE)
        }

        // setup click listeners
        button_exercise_pause.setOnClickListener {
            if (bound) {
                val msg = Message.obtain(null, ExerciseService.MSG_CHANGE_STATE)
                try {
                    service?.send(msg)
                } catch (e: RemoteException) {
                    Log.e(TAG, "Unexpected dead ExerciseService", e)
                    doUnbindService()
                }
            }
        }
        button_exercise_finish.setOnClickListener {
            doStopService()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        doStopService()
    }

    private fun doUnbindService() {
        if (bound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (service != null) {
                try {
                    val msg = Message.obtain(null, ExerciseService.MSG_UNREGISTER)
                    msg.replyTo = messenger
                    service?.send(msg)
                } catch (e: RemoteException) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }
            unbindService(connection)
            bound = false
        }
    }

    private fun doStopService() {
        doUnbindService()
        val intent = Intent(this, ExerciseService::class.java)
        stopService(intent)
    }

    companion object {
        private val TAG = ExerciseActivity::class.java.simpleName
    }
}
