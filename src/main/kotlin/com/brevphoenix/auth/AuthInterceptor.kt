package com.brevphoenix.auth

import com.wgtwo.auth.WgtwoAuth
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.MethodDescriptor

class AuthInterceptor(clientId: String, clientSecret: String) : ClientInterceptor {

    private val tokenSource = WgtwoAuth.builder(clientId, clientSecret).build()
        .clientCredentials.newTokenSource(RequiredScopes.scopes.joinToString(" "))

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel,
    ): ClientCall<ReqT, RespT> {
        return next.newCall(method, callOptions.withCallCredentials(tokenSource.callCredentials()))
    }

}
