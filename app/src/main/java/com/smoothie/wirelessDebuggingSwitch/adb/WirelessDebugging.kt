package com.smoothie.wirelessDebuggingSwitch.adb

interface IAdbWifi {
    fun getEnabled(): Boolean
    fun setEnabled(value: Boolean)
    fun toggle()

    fun getAddress(): String
    fun getPort(): String

    fun getConnectionData(): String
    fun copyConnectionData()
    fun syncConnectionData()
}

interface PrivilegeCheck {
    fun hasSufficientPrivileges(): Boolean
    fun requestPrivileges()
}
