package at.spiceburg.roarfit.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.features.main.MainActivity
import at.spiceburg.roarfit.features.main.exercise.ExerciseFragmentArgs
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class ExerciseService : Service() {

    private lateinit var nm: NotificationManager
    private val messenger = Messenger(IncomingHandler())
    private var client: Messenger? = null

    private lateinit var contentIntent: PendingIntent
    private lateinit var contentTitle: String

    private var stopwatch: Disposable? = null
    private val lastTick = AtomicLong(1L)

    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_REGISTER -> client = msg.replyTo
                MSG_UNREGISTER -> {
                    client = null
                    stopwatch?.dispose()
                }
                MSG_STOPWATCH_PAUSE -> stopwatch?.dispose()
                MSG_STOPWATCH_CONTINUE -> startStopwatch()
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "binding")

        // create notification channel if android version is 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                TAG,
                getString(R.string.exercise_notification_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nm.createNotificationChannel(serviceChannel)
        }

        // setup notification
        val args = intent.getBundleExtra("args")!!
        val exerciseArgs = ExerciseFragmentArgs.fromBundle(args)
        contentTitle = exerciseArgs.template.name
        exerciseArgs.template.equipment?.let { equipment ->
            contentTitle += " - ${equipment.string}"
        }

        // create the DeepLink for notification click
        args.putBoolean("notification", true)
        contentIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.exerciseFragment)
            .setArguments(args)
            .createPendingIntent()

        // start notification
        val notification = buildNotification(contentIntent, contentTitle, "00:00")
        startForeground(NOTIFICATION_ID, notification)

        // start stopwatch
        startStopwatch()

        return messenger.binder
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        stopwatch?.dispose()
        stopForeground(true)
    }

    private fun startStopwatch() {
        val formatter = SimpleDateFormat("mm:ss", Locale.ENGLISH)
        stopwatch = Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { lastTick.getAndIncrement() }
            .subscribe { ticks ->
                val formattedTime = formatter.format(Date(ticks * 1000))

                Log.d(TAG, "Stopwatch: $formattedTime")

                // update view
                val msg = Message.obtain(null, MSG_STOPWATCH_UPDATE, formattedTime)
                try {
                    client?.send(msg)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

                // update notification
                val updatedNotification =
                    buildNotification(contentIntent, contentTitle, formattedTime)
                nm.notify(NOTIFICATION_ID, updatedNotification)
            }
    }

    private fun buildNotification(
        contentIntent: PendingIntent,
        contentTitle: String,
        formattedTime: String
    ): Notification {
        return NotificationCompat.Builder(this, TAG)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(contentTitle)
            .setContentText(formattedTime)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(contentIntent)
            // .addAction(R.drawable.ic_pause_black_24dp, "Pause", pauseIntent)
            /*.setLargeIcon(
                resources.getDrawable(
                    R.drawable.ic_launcher_foreground,
                    null
                ).toBitmap()
            )*/
            .build()
    }

    companion object {
        const val MSG_REGISTER = 1
        const val MSG_UNREGISTER = 2
        const val MSG_STOPWATCH_UPDATE = 3
        const val MSG_STOPWATCH_PAUSE = 4
        const val MSG_STOPWATCH_CONTINUE = 5

        private const val NOTIFICATION_ID = 1
        private val TAG = ExerciseService::class.java.simpleName
    }
}
