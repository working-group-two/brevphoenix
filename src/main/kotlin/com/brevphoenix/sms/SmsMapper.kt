package com.brevphoenix.sms

import com.brevphoenix.PhoneNumber
import com.google.protobuf.Timestamp
import com.wgtwo.api.v0.common.PhoneNumberProto
import com.wgtwo.api.v0.events.EventsProto
import com.wgtwo.api.v0.events.EventsProto.SmsEvent.FromAddressCase
import com.wgtwo.api.v0.events.EventsProto.SmsEvent.ToAddressCase
import org.slf4j.LoggerFactory
import java.time.Instant

object SmsMapper {
    private val logger by lazy { LoggerFactory.getLogger(javaClass) }

    fun parseEvent(event: EventsProto.Event): Sms {
        if (!event.hasSmsEvent()) {
            throw IllegalArgumentException("Event is not of type SMS")
        }
        val smsEvent = event.smsEvent

        val from = fromAddress(smsEvent)
        val to = toAddress(smsEvent)

        val direction = when (smsEvent.direction) {
            EventsProto.SmsEvent.Direction.FROM_SUBSCRIBER -> Sms.Direction.FROM_SUBSCRIBER
            EventsProto.SmsEvent.Direction.TO_SUBSCRIBER -> Sms.Direction.TO_SUBSCRIBER
            else -> throw IllegalArgumentException("Message must have direction")
        }
        logger.info("Sub: ${event.owner.sub} From: $from To: $to Direction: $direction")

        return Sms(
            id = smsEvent.id,
            sub = event.owner.sub,
            direction = direction,
            from = from,
            to = to,
            content = smsEvent.text,
            timestamp = event.timestamp.toInstant(),
        )
    }

    private fun Timestamp.toInstant() = Instant.ofEpochSecond(seconds, nanos.toLong())

    private fun fromAddress(event: EventsProto.SmsEvent) = when (event.fromAddressCase) {
        FromAddressCase.FROM_E164 -> asAddress(event.fromE164)
        FromAddressCase.FROM_NATIONAL_PHONE_NUMBER -> asAddress(event.fromNationalPhoneNumber)
        FromAddressCase.FROM_TEXT_ADDRESS -> asAddress(event.fromTextAddress)
        else -> throw IllegalArgumentException("Invalid from address type.")
    }

    private fun toAddress(event: EventsProto.SmsEvent) = when (event.toAddressCase) {
        ToAddressCase.TO_E164 -> asAddress(event.toE164)
        ToAddressCase.TO_NATIONAL_PHONE_NUMBER -> asAddress(event.toNationalPhoneNumber)
        ToAddressCase.TO_TEXT_ADDRESS -> asAddress(event.toTextAddress)
        else -> throw IllegalArgumentException("Invalid to address type.")
    }

    private fun asAddress(textAddress: PhoneNumberProto.TextAddress): Address {
        return Address.TextAddress(textAddress.textAddress)
    }

    private fun asAddress(internationalNumber: PhoneNumberProto.PhoneNumber): Address {
        val phoneNumber = PhoneNumber.parse(internationalNumber.e164)
        return Address.InternationalNumber(phoneNumber)
    }

    private fun asAddress(nationalNumber: PhoneNumberProto.NationalPhoneNumber): Address {
        val phoneNumber = PhoneNumber.parse(nationalNumber.nationalPhoneNumber)
        return Address.InternationalNumber(phoneNumber)
    }
}
