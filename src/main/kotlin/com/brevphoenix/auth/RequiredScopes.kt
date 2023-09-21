package com.brevphoenix.auth

object RequiredScopes {
    val scopes = setOf(
        "sms.text:send_to_subscriber",
        "sms.text:send_from_subscriber",
        "events.sms.subscribe",
    )
}
