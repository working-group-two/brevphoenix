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

    fun handleReceivedSms(sms: Sms) {
        val subscriberAddress = when (sms.direction) {
            Sms.Direction.FROM_SUBSCRIBER -> sms.from
            Sms.Direction.TO_SUBSCRIBER -> sms.to
        }
        val subscriberPhoneNumber = (subscriberAddress as? Address.InternationalNumber)?.phoneNumber ?: return
        val e164 = subscriberPhoneNumber.e164
        messages.compute(e164) { _, list -> list?.plus(sms) ?: listOf(sms) }
    }
}
