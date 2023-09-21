package com.brevphoenix.sms

import com.brevphoenix.PhoneNumber
import java.time.Instant
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.MINIMAL_CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class",
)
sealed interface Address {
    data class InternationalNumber(val phoneNumber: PhoneNumber) : Address
    data class TextAddress(val text: String) : Address
}

data class Sms(
    val id: String,
    val sub: String,
    val direction: Direction,
    val from: Address,
    val to: Address,
    val content: String,
    val timestamp: Instant,
) {
    enum class Direction {
        FROM_SUBSCRIBER,
        TO_SUBSCRIBER,
    }
}
