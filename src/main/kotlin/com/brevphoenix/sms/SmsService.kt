package com.brevphoenix.sms

import com.brevphoenix.PhoneNumber
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

object SmsService {

    private val logger by lazy { LoggerFactory.getLogger(javaClass) }
    private val messages = ConcurrentHashMap<String, List<Sms>>()

    fun init() {
        SmsReceiverService.init()
    }

    fun sendSms(from: PhoneNumber, to: PhoneNumber, text: String): String {
        logger.info("Sending SMS. from=$from, to=$to")
        return SmsSendService.sendFromSubscriber(from = from.e164, to = to.e164, text)
    }

    fun getSms(phoneNumber: PhoneNumber): List<Sms> {
        return messages[phoneNumber.e164] ?: emptyList()
    }

    fun handleObservedSms(sms: Sms) {
        val e164 = getSmsSubE164(sms) ?: return
        messages.compute(e164) { _, list -> list?.plus(sms) ?: listOf(sms) }
        SmsWsService.handleReceivedSms(e164, sms)
    }

    fun getSmsSubE164(sms: Sms): String? {
        val sub = when (sms.direction) {
            Sms.Direction.FROM_SUBSCRIBER -> sms.from
            Sms.Direction.TO_SUBSCRIBER -> sms.to
        }
        return when (sub) {
            is Address.InternationalNumber -> sub.phoneNumber.e164
            is Address.TextAddress -> null
        }
    }
}
