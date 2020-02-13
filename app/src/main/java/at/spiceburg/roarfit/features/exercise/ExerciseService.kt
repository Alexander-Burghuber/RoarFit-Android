package at.spiceburg.roarfit.features.exercise

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.utils.Constants
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observables.ConnectableObservable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ExerciseService : IntentService("ExerciseService") {

    var stopwatch: ConnectableObservable<String>? = null
    private var lastTick = 0L
    private val binder = LocalBinder()
    private var disposableStopwatch: Disposable? = null
    private var connectedStopWatch: Disposable? = null
    private lateinit var pendingIntent: PendingIntent

    inner class LocalBinder : Binder() {
        fun getService(): ExerciseService = this@ExerciseService
    }

    override fun onCreate() {
        super.onCreate()
        val activityIntent = Intent(this, ExerciseActivity::class.java)
        pendingIntent =
            PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onHandleIntent(intent: Intent?) {
        startStopwatch()
        val notification = buildNotification("00:00", pendingIntent)
        startForeground(Constants.NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
        disposableStopwatch?.dispose()
        stopwatch = null
        stopForeground(true)
    }

    /**
     * Returns true if the stopwatch was paused and false if it was continued
     */
    fun pauseOrContinueStopwatch(): Boolean {
        return if ((disposableStopwatch?.isDisposed) == false) {
            disposableStopwatch?.dispose()
            connectedStopWatch?.dispose()
            true
        } else {
            startStopwatch()
            false
        }
    }

    private fun startStopwatch() {
        val formatter = SimpleDateFormat("mm:ss", Locale.US)
        val nm = NotificationManagerCompat.from(this)

        stopwatch = Observable.interval(1, TimeUnit.SECONDS)
            .map { ++lastTick }
            .map { ticks -> formatter.format(Date(ticks * 1000)) }
            .publish()
        connectedStopWatch = stopwatch?.connect()

        disposableStopwatch = stopwatch?.subscribe { time ->
            Log.d(TAG, "Stopwatch: $time")

            // update notification
            val updatedNotification = buildNotification(time, pendingIntent)
            nm.notify(Constants.NOTIFICATION_ID, updatedNotification)
        }
    }

    private fun buildNotification(time: String, pendingIntent: PendingIntent): Notification {
        return NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setContentTitle("Hello there!")
            .setContentText(time)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent) // intent for on notification click
            .setPriority(NotificationCompat.PRIORITY_HIGH) // for Android 7.1 and lower
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT) // media transport playback
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // no sensitive content
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .build()
    }

    companion object {
        private val TAG = ExerciseService::class.java.simpleName
    }
}
