package com.smoothie.wirelessDebuggingSwitch.adb

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.smoothie.wirelessDebuggingSwitch.KdeConnect
import com.smoothie.wirelessDebuggingSwitch.R
import com.smoothie.wirelessDebuggingSwitch.copyText
import com.topjohnwu.superuser.Shell

class RootAdbWifi(private val context: Context) : AdbWifi(context) {

    companion object {
        private const val TAG = "RootAdbWifi"
    }

    override fun getEnabled(): Boolean {
        val result = Shell.cmd(ADB_WIFI_PORT_GET_STATUS).exec().out.joinToString()
        return result.isNotBlank() && result.toInt() == 1
    }

    override fun setEnabled(value: Boolean) {
        val state = if (value) 1 else 0
        val command = buildPutCommand(state)
        Shell.cmd(command).exec()
    }

    override fun getPort(): String {
        return Shell.cmd("getprop $ADB_WIFI_PORT_KEY").exec().out.first().toString()
    }

    override fun syncConnectionData() {
        val connectionInfo: String
        try {
            connectionInfo = getConnectionData()
        } catch (exception: Exception) {
            Log.e(TAG, "Unable to get connection address and port.")
            exception.printStackTrace()
            return
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        var preferenceKey = context.getString(R.string.key_enable_kde_connect)
        val kdeIntegrationEnabled = preferences.getBoolean(preferenceKey, true)

        if (!KdeConnect.isInstalled(context) || !kdeIntegrationEnabled)
            return

        preferenceKey = context.getString(R.string.key_prefix_connection_data)
        val prefixConnectionData = preferences.getBoolean(preferenceKey, true)

        preferenceKey = context.getString(R.string.key_connection_data_prefix)
        val defaultPrefix = context.getString(R.string.default_connection_data_prefix)
        val connectionDataPrefix = preferences.getString(preferenceKey, defaultPrefix)

        val connectionData =
            if (prefixConnectionData)
                connectionDataPrefix + connectionInfo
            else
                connectionInfo

        copyText(context, "Data for KDE Connect", connectionData)
        val result = KdeConnect.sendClipboard()

        if (!result.isSuccess) {
            val message = context.getString(R.string.message_failed_sending_clipboard)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            Log.w(TAG, result.toString())
        }
    }

    override fun hasSufficientPrivileges(): Boolean = Shell.isAppGrantedRoot() == true

    override fun requestPrivileges() {
        Shell.isAppGrantedRoot()
    }

}
