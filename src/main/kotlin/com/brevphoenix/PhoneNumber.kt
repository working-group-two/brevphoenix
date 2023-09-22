package com.brevphoenix

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.io.Serializable
import com.wgtwo.api.v0.common.PhoneNumberProto.PhoneNumber as PhoneNumberProto

class PhoneNumber private constructor(number: Phonenumber.PhoneNumber) : Serializable {
    val e164: String = util.format(number, PhoneNumberUtil.PhoneNumberFormat.E164)

    fun toProto(): PhoneNumberProto = PhoneNumberProto.newBuilder().setE164(e164).build()
    override fun toString(): String = e164
    override fun hashCode(): Int = e164.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PhoneNumber
        return e164 == other.e164
    }

    companion object {
        private const val serialVersionUID: Long = 107667120127L
        private val util = PhoneNumberUtil.getInstance()

        fun parse(number: String): PhoneNumber {
            val normalized = if (number.trim().startsWith("+")) number.trim() else "+${number.trim()}"
            val googlePhoneNumber = util.parse("+$normalized", "NO")
            require(util.isValidNumber(googlePhoneNumber)) { "Invalid phone number" }
            return PhoneNumber(googlePhoneNumber)
        }
    }
}
