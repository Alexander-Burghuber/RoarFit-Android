package at.spiceburg.roarfit.features.main.exercise

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import at.spiceburg.roarfit.MyApplication
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.services.ExerciseService
import kotlinx.android.synthetic.main.fragment_exercise.*

class ExerciseFragment : Fragment() {

    private lateinit var viewModel: ExerciseViewModel
    private var service: Messenger? = null
    private val messenger = Messenger(IncomingHandler())
    private var bound = false

    private var isRunning = true

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, ibinder: IBinder) {
            service = Messenger(ibinder)
            bound = true
            Log.d(TAG, "onServiceConnected")

            try {
                val msg = Message.obtain(null, ExerciseService.MSG_REGISTER)
                msg.replyTo = messenger
                service?.send(msg)
            } catch (e: RemoteException) {
                Log.e(TAG, "Register Service exception", e)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            service = null
            bound = false
            Log.d(TAG, "onServiceDisconnected")
        }
    }

    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                ExerciseService.MSG_STOPWATCH_UPDATE -> {
                    text_exercise_stopwatch.text = msg.obj as String
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise, container, false)
    }

    override fun onStart() {
        super.onStart()

        // setup viewModel
        val appContainer = (requireActivity().application as MyApplication).appContainer
        viewModel = ViewModelProviders.of(this, appContainer.exerciseViewModelFactory)
            .get(ExerciseViewModel::class.java)

        // set view elements
        val args = requireArguments()
        val exerciseArgs = ExerciseFragmentArgs.fromBundle(args)
        text_exercise_equipment.text = exerciseArgs.template.equipment?.string
        text_exercise_name.text = exerciseArgs.template.name

        // bind to service
        if (!args.getBoolean("notification", false)) {
            if (!bound) {
                args.remove("notification")
                val context = requireContext()
                val intent = Intent(context, ExerciseService::class.java)
                    .putExtra("args", args)
                context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }

        // setup click listeners
        button_exercise_pause.setOnClickListener {
            if (bound) {
                if (isRunning) {
                    val msg = Message.obtain(null, ExerciseService.MSG_STOPWATCH_PAUSE)
                    try {
                        service?.send(msg)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                    isRunning = false
                    button_exercise_pause.text = getString(R.string.exercise_button_continue)
                } else {
                    val msg = Message.obtain(null, ExerciseService.MSG_STOPWATCH_CONTINUE)
                    try {
                        service?.send(msg)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                    isRunning = true
                    button_exercise_pause.text = getString(R.string.exercise_button_pause)
                }
            }
        }
        button_exercise_finish.setOnClickListener {
            unbindService()
            findNavController().popBackStack(R.id.dashboardFragment, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService()
    }

    private fun unbindService() {
        if (bound) {
            if (service != null) {
                try {
                    val msg = Message.obtain(null, ExerciseService.MSG_UNREGISTER)
                    msg.replyTo = messenger
                    service?.send(msg)
                } catch (e: RemoteException) {
                    Log.e(TAG, "Unregister Service exception", e)
                }
            }
            requireContext().unbindService(connection)
            bound = false
        }
    }

    companion object {
        private val TAG = ExerciseFragment::class.java.simpleName
    }
}
