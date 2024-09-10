package com.brevphoenix

import com.zaxxer.hikari.HikariDataSource

data class Config(
    val port: Int,
    val apiTarget: String,
    val oAuth: OAuthCredentials,
    val databaseApp: HikariDataSource,
)

data class OAuthCredentials(
    val clientId: String,
    val clientSecret: String,
)
