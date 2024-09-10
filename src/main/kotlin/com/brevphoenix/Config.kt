package com.brevphoenix

import com.sksamuel.hoplite.env.Environment
import com.zaxxer.hikari.HikariDataSource

data class Config(
    val port: Int,
    val environment: Environment,
    val apiTarget: String,
    val oAuth: OAuthCredentials,
    val appDatabase: HikariDataSource,
    val sessionDatabase: HikariDataSource,
)

data class OAuthCredentials(
    val clientId: String,
    val clientSecret: String,
)
