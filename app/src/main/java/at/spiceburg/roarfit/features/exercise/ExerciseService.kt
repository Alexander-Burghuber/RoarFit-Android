package at.spiceburg.roarfit.features.exercise

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class ExerciseService : Service() {

    private var client: Messenger? = null
    private val messenger = Messenger(IncomingHandler())

    private lateinit var nm: NotificationManager
    private lateinit var contentPendingIntent: PendingIntent
    private lateinit var contentTitle: String

    private var stopwatch: Disposable? = null
    private val lastTick = AtomicLong(1L)
    private val formatter = SimpleDateFormat("mm:ss", Locale.ENGLISH)

    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_REGISTER -> {
                    client = msg.replyTo
                }
                MSG_UNREGISTER -> {
                    client = null
                    stopwatch?.dispose()
                }
                MSG_PAUSE_CONTINUE -> {
                    val isRunning = isRunning()
                    if (isRunning) {
                        // pause the stopwatch
                        stopwatch?.dispose()
                    } else {
                        // continue the stopwatch
                        runStopWatch()
                    }
                    val returnMsg = Message.obtain(null, MSG_PAUSE_CONTINUE, isRunning)
                    try {
                        client?.send(returnMsg)
                    } catch (e: RemoteException) {
                        // The client is dead. Remove it.
                        client = null
                        stopwatch?.dispose()
                    }
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onCreate() {
        Log.d(CHANNEL_ID, "onCreate called")
        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(CHANNEL_ID, "onBind called")

        // create notification channel if android version is 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.exercise_notification_name),
                NotificationManager.IMPORTANCE_LOW
            )
            nm.createNotificationChannel(channel)
        }

        // setup notification text
        val template = intent.getSerializableExtra("template") as ExerciseTemplate
        contentTitle = template.name

        // show the equipment name if it exists
        /* fixme template.equipment?.let {
            contentTitle += " - ${it.string}"
        }*/

        // create on notification click pending intent
        val contentIntent = Intent(this, ExerciseActivity::class.java)
        contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // create on pause/continue click pending intent
        /* val pauseIntent = Intent(this, MyBroadcastReceiver::class.java)
         pauseIntent.action = getString(R.string.exercise_action_pause)
         pausePendingIntent = PendingIntent.getBroadcast(
             this,
             0,
             pauseIntent,
             0
         )*/

        // start notification
        val notification = buildNotification("00:00")
        startForeground(NOTIFICATION_ID, notification)

        // start stopwatch
        runStopWatch()

        return messenger.binder
    }

    override fun onDestroy() {
        Log.d(CHANNEL_ID, "onDestroy called")
        stopwatch?.dispose()
        stopForeground(true)
    }

    private fun runStopWatch() {
        stopwatch = Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { lastTick.getAndIncrement() }
            .subscribe { ticks ->
                val formattedTime = formatter.format(Date(ticks * 1000))

                Log.d(CHANNEL_ID, "Stopwatch: $formattedTime")

                // update view
                val msg = Message.obtain(null, MSG_UPDATE, formattedTime)
                try {
                    client?.send(msg)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

                // update notification
                val updatedNotification = buildNotification(formattedTime)
                nm.notify(NOTIFICATION_ID, updatedNotification)
            }
    }

    private fun buildNotification(formattedTime: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(resources.getColor(R.color.primary, null))
            .setColorized(true)
            .setShowWhen(false)
            .setContentTitle(contentTitle)
            .setContentText(formattedTime)
            .setContentIntent(contentPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            /* .addAction(NotificationCompat.Action(
                R.drawable.ic_pause_black_24dp,
                "Pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            ))*/
            // .addAction(R.drawable.ic_pause_black_24dp, "Pause", pausePendingIntent)
            /* .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0)
            )*/
            .build()
    }

    /**
     * Returns the status of the stopwatch by checking if the stopwatch is null or disposed
     */
    private fun isRunning(): Boolean {
        return !(stopwatch?.isDisposed ?: true)
    }

    companion object {
        const val MSG_REGISTER = 1
        const val MSG_UNREGISTER = 2
        const val MSG_UPDATE = 3
        const val MSG_PAUSE_CONTINUE = 4

        private const val NOTIFICATION_ID = 1
        private val CHANNEL_ID = ExerciseService::class.java.simpleName
    }
}
