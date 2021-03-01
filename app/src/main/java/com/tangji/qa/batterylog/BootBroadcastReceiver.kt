package com.tangji.qa.batterylog


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.preference.PreferenceManager

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION) {
            val service = Intent(context, BatteryLogService::class.java)
            //            context.startActivity(welcomeIntent);
            Log.d("TAG", (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_boot_run", true)
                .toString()))
            service.putExtra("testTitle",System.currentTimeMillis())
            if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_boot_run", false)){
                context.startForegroundService(service)
            }
        }
    }

    companion object {
        const val ACTION = "android.intent.action.BOOT_COMPLETED"
    }
}