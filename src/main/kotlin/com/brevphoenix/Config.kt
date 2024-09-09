package com.brevphoenix

data class Config(
    val port: Int,
    val apiTarget: String,
    val oAuth: OAuthCredentials,
)

data class OAuthCredentials(
    val clientId: String,
    val clientSecret: String,
)
