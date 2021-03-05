package com.tangji.qa.batterylog

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.BatteryManager
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.tangji.qa.batterylog.database.BatteryInfo
import com.tangji.qa.batterylog.database.BatteryInfoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BatteryLogService : Service() {
    private val TAG = "BatteryLogService"
    private val ID = "channel_1"
    private val NAME = "前台服务"
    private var testTitle: Long=0
    private var startTime:Long = 0
    private lateinit var batteryInfoRepository: BatteryInfoRepository
    private lateinit var wakeLock: WakeLock
    private lateinit var applicationScope:CoroutineScope
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    @SuppressLint("InvalidWakeLockTag")
    override fun onCreate() {
        super.onCreate()
        val pm = this.getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.ON_AFTER_RELEASE
                    or PowerManager.PARTIAL_WAKE_LOCK, "Tag"
        )
        //申请WakeLock
        wakeLock.acquire()
        Log.d(TAG, "onCreate executed")
        setForeground()
        batteryInfoRepository= (application as BatteryLogApplication).repository
        applicationScope = (application as BatteryLogApplication).applicationScope
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        startTime=SystemClock.elapsedRealtime()
        registerReceiver(mBroadcastReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            testTitle = intent.getLongExtra("testTitle", 0)
        }
        return START_REDELIVER_INTENT
    }

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action
            if (action == Intent.ACTION_BATTERY_CHANGED) {
                notificationLayout.setTextViewText(R.id.notification_level,intent.getIntExtra("level", 0).toString()+"%")
                notificationLayout.setTextViewText(R.id.notification_temperature,(intent.getIntExtra("temperature", 0).toFloat()/10).toString()+"℃")
                manager.notify(1,notification)
                applicationScope.launch(Dispatchers.IO) {
                    batteryInfoRepository.insert(
                        BatteryInfo(
                            0,
                            this@BatteryLogService.testTitle,
                            SystemClock.elapsedRealtime() - startTime,
                            intent.getIntExtra("status", 0),
                            intent.getIntExtra("health", 0),
                            intent.getBooleanExtra("present", false),
                            intent.getIntExtra("level", 0),
                            intent.getIntExtra("scale", 0),
                            intent.getIntExtra("iconsmall", 0),
                            intent.getIntExtra("plugged", 0),
                            intent.getIntExtra("voltage", 0),
                            intent.getIntExtra("temperature", 0),
                            intent.getStringExtra("technology").toString()
                        )
                    )
                }
                if (intent.getIntExtra("status", 0)== BatteryManager.BATTERY_STATUS_FULL){
                    this@BatteryLogService.stopSelf()
                }
            }
        }
    }



    private lateinit var notification: Notification
    private lateinit var manager:NotificationManager
    private lateinit var notificationLayout:RemoteViews
    private fun setForeground() {
        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(channel)
        notificationLayout = RemoteViews(packageName, R.layout.notification)

        // Create an Intent for the activity you want to start
        val startActivityIntent = Intent(this, MainActivity::class.java)
        val startActivityPendingIntent: PendingIntent? = PendingIntent.getActivity(applicationContext,0,startActivityIntent,0)

        notification = Notification.Builder(this, ID)
            //.setContentTitle(getString(R.string.service_runing_notification))
            //.setContentText(getString(R.string.open_app))
            .setSmallIcon(R.drawable.ic_notification)
            //.setStyle(Notification.DecoratedCustomViewStyle())
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_notification))
            .setColor(getColor(R.color.purple_500))
            //.setCustomBigContentView(notificationLayout)
            .setCustomContentView(notificationLayout)
            .setContentIntent(startActivityPendingIntent)
            .setOnlyAlertOnce(true)
            .build()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        unregisterReceiver(mBroadcastReceiver)
        wakeLock.release()
        super.onDestroy()
    }
}