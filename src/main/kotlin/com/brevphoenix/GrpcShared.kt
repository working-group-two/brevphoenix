package com.brevphoenix

import com.brevphoenix.auth.AuthInterceptor
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.util.concurrent.TimeUnit

object GrpcShared {

    val authInterceptor = AuthInterceptor(config.oAuth.clientId, config.oAuth.clientSecret)

    val channel: ManagedChannel = ManagedChannelBuilder.forAddress(config.apiTarget, 443)
        .useTransportSecurity()
        .keepAliveTime(30, TimeUnit.SECONDS)
        .keepAliveTimeout(10, TimeUnit.SECONDS)
        .keepAliveWithoutCalls(true)
        .build()

    fun close() {
        channel.shutdown()
        channel.awaitTermination(3, TimeUnit.SECONDS)
    }
}
