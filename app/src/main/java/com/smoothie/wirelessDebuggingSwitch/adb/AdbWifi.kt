package com.smoothie.wirelessDebuggingSwitch.adb

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.smoothie.wirelessDebuggingSwitch.copyText

abstract class AdbWifi(private val context: Context) : IAdbWifi, PrivilegeCheck {

    companion object {
        private const val TAG = "AdbWifi"
        const val ADB_WIFI_ENABLED_KEY = "adb_wifi_enabled"
        const val ADB_WIFI_PORT_KEY = "service.adb.tls.port"

        const val ADB_WIFI_PORT_GET_STATUS = "settings get global $ADB_WIFI_PORT_KEY"

        fun getPrivilegeMethod(context: Context): AdbWifi? {
            val rootlessAdbWifi = RootlessAdbWifi(context)
            if (rootlessAdbWifi.hasSufficientPrivileges()) return rootlessAdbWifi

            val rootAdbWifi = RootAdbWifi(context)
            if (rootAdbWifi.hasSufficientPrivileges()) return rootAdbWifi

            val shizukuAdbWifi = ShizukuAdbWifi(context)
            if (shizukuAdbWifi.hasSufficientPrivileges()) return shizukuAdbWifi

            return null
        }
    }

    protected fun buildPutCommand(state: Int) = "settings put --user current global $ADB_WIFI_ENABLED_KEY $state"

    override fun toggle() {
        setEnabled(!getEnabled())
    }

    override fun getAddress(): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectionInfo = wifiManager.connectionInfo
        val ipAddress = connectionInfo.ipAddress
        return Formatter.formatIpAddress(ipAddress)
    }

    override fun getConnectionData(): String = "${getAddress()}:${getPort()}"

    override fun copyConnectionData() {
        copyText(context, "Wireless debugging connection data", getConnectionData())
    }

    override fun syncConnectionData() = Unit
}

fun AdbWifi?.hasSufficientPrivileges(): Boolean = this?.hasSufficientPrivileges() ?: false
