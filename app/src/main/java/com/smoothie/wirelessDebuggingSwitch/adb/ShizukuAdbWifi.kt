package com.smoothie.wirelessDebuggingSwitch.adb

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import com.smoothie.wirelessDebuggingSwitch.ShizukuUtilities
import rikka.shizuku.Shizuku

class ShizukuAdbWifi(private val context: Context) : AdbWifi(context) {

    override fun getEnabled(): Boolean {
        val result = ShizukuUtilities.executeCommand(ADB_WIFI_PORT_GET_STATUS)
        return result.isNotBlank() && result.toInt() == 1
    }

    override fun setEnabled(value: Boolean) {
        val command = buildPutCommand(if (value) 1 else 0)
        ShizukuUtilities.executeCommand(command)
    }

    override fun getPort(): String = ShizukuUtilities.getWirelessAdbPort()

    override fun hasSufficientPrivileges(): Boolean = ShizukuUtilities.hasShizukuPermission()

    override fun requestPrivileges() {
        Shizuku.addRequestPermissionResultListener(object :
            Shizuku.OnRequestPermissionResultListener {
            override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                Shizuku.removeRequestPermissionResultListener(this)
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Shizuku permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Shizuku permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        })

        Shizuku.requestPermission(ShizukuUtilities.REQUEST_CODE)
    }
}
