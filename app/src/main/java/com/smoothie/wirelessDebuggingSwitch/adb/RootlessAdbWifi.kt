package com.smoothie.wirelessDebuggingSwitch.adb

import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast

class RootlessAdbWifi(private val context: Context) : AdbWifi(context) {

    companion object {
        const val WRITE_SECURE_SETTINGS = "android.permission.WRITE_SECURE_SETTINGS"
    }

    override fun getEnabled(): Boolean = Settings.Global.getInt(context.contentResolver, ADB_WIFI_ENABLED_KEY, 0) == 1

    override fun setEnabled(value: Boolean) {
        Settings.Global.putInt(context.contentResolver, ADB_WIFI_ENABLED_KEY, if (value) 1 else 0)
    }

    override fun getPort(): String {
        return "-1"
    }

    override fun hasSufficientPrivileges(): Boolean {
        return context.checkSelfPermission(WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPrivileges() {
        // todo
        Toast.makeText(context, "Requesting WRITE_SECURE_SETTINGS permission", Toast.LENGTH_SHORT).show()
    }
}
