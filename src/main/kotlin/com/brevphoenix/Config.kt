package com.brevphoenix

import com.sksamuel.hoplite.env.Environment
import com.zaxxer.hikari.HikariDataSource
import java.io.File

data class Config(
    val port: Int,
    val environment: Environment,
    val apiTarget: String,
    val oAuth: OAuthCredentials,
    val appDatabase: HikariDataSource,
    val sessionDatabase: HikariDataSource,
    val databaseKeyPairConfig: KeyPairConfig,
)

data class OAuthCredentials(
    val clientId: String,
    val clientSecret: String,
)

data class KeyPairConfig(
    val publicKeyFile: File,
    val privateKeyFile: File,
    val password: String?,
)
