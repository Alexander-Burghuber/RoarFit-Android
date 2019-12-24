package at.spiceburg.roarfit.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.features.exercise.ExerciseActivity
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
    private lateinit var pendingIntent: PendingIntent
    private lateinit var contentTitle: String

    private var stopwatch: Disposable? = null
    private val lastTick = AtomicLong(1L)

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
                MSG_PAUSE -> {
                    stopwatch?.dispose()
                }
                MSG_CONTINUE -> {
                    startStopwatch()
                }
                MSG_CHANGE_STATE -> {
                    // get the status of the stopwatch by checking if the stopwatch is null or disposed
                    val isRunning = !(stopwatch?.isDisposed ?: true)
                    if (isRunning) {
                        // pause the stopwatch
                        stopwatch?.dispose()
                    } else {
                        // continue the stopwatch
                        startStopwatch()
                    }

                    val returnMsg = Message.obtain(null, MSG_CHANGE_STATE, isRunning)
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
        Log.d(TAG, "onCreate")
        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
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
        val exerciseTemplate = intent.getSerializableExtra("template") as ExerciseTemplate
        contentTitle = exerciseTemplate.name
        exerciseTemplate.equipment?.let {
            contentTitle += " - ${it.string}"
        }

        // create pending intent
        val notificationIntent = Intent(this, ExerciseActivity::class.java)
            // .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra("template", exerciseTemplate)
        pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // start notification
        val notification = buildNotification(contentTitle, "00:00", pendingIntent)
        startForeground(NOTIFICATION_ID, notification)

        // start stopwatch
        startStopwatch()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind")
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

                // update fragment
                val msg = Message.obtain(null, MSG_UPDATE, formattedTime)
                try {
                    client?.send(msg)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

                // update notification
                val updatedNotification =
                    buildNotification(contentTitle, formattedTime, pendingIntent)
                nm.notify(NOTIFICATION_ID, updatedNotification)
            }
    }

    private fun buildNotification(
        contentTitle: String,
        formattedTime: String,
        pendingIntent: PendingIntent
    ): Notification {
        return NotificationCompat.Builder(this, TAG)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setContentTitle(contentTitle)
            .setContentText(formattedTime)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
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
        const val MSG_UPDATE = 3
        const val MSG_PAUSE = 4
        const val MSG_CONTINUE = 5
        const val MSG_ISRUNNING = 6
        const val MSG_CHANGE_STATE = 7

        private const val NOTIFICATION_ID = 1
        private val TAG = ExerciseService::class.java.simpleName
    }
}
