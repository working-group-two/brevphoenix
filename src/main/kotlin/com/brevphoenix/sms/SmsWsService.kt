package com.brevphoenix.sms

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsConnectContext
import io.javalin.websocket.WsContext
import java.util.concurrent.ConcurrentHashMap

object SmsWsService {
    private val connectedWsUsers = ConcurrentHashMap<String, WsContext>()

    fun handleWsConnect(ctx: WsConnectContext, e164: String) {
        connectedWsUsers[e164] = ctx
    }

    fun handleWsClose(ctx: WsCloseContext, e164: String) {
        connectedWsUsers.remove(e164)
    }

    fun handleReceivedSms(e164: String, sms: Sms) {
        sendToAnyConnectedWsUser(e164, sms)
    }

    private fun sendToAnyConnectedWsUser(e164: String, sms: Sms) {
        connectedWsUsers[e164]?.send(sms)
    }
}
