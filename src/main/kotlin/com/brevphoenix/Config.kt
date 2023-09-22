package com.brevphoenix

data class Config(
    val port: Int = System.getenv("PORT")?.toInt() ?: 8080,
    val apiClientId: String = System.getenv("API_CLIENT_ID") ?: "3c234115-3023-44aa-9529-1af41f3ebab4",
    val apiClientSecret: String = System.getenv("API_CLIENT_SECRET"),
    val apiTarget: String = System.getenv("API_TARGET") ?: "api.wgtwo.com",
)
