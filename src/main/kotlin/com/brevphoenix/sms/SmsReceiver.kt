package com.brevphoenix.sms

interface SmsReceiver {
    fun onReceived(sms: Sms)
}