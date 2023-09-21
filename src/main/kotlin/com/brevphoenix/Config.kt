package com.brevphoenix

data class Config(
    val port: Int = System.getenv("PORT")?.toInt() ?: 8080,
    val apiClientId: String = System.getenv("API_CLIENT_ID") ?: "2e4274af-f257-47ed-9396-29a23e292dc0",
    val apiClientSecret: String = System.getenv("API_CLIENT_SECRET") ?: "",
    val apiTarget: String = System.getenv("API_TARGET") ?: "api.wgtwo.com",
)
