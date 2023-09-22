package com.brevphoenix.sms

object SmsReceiverService : SmsReceiver {

    fun init() {
        SmsEventService.init()
        SmsEventService.addListener(this)
    }

    override fun onReceived(sms: Sms) {
        SmsService.handleObservedSms(sms)
    }
}
