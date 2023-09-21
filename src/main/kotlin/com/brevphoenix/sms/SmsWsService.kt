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

    fun handleReceivedSms(sms: Sms) {
        setOf(
            sms.from,
            sms.to,
        ).filterIsInstance<Address.InternationalNumber>().forEach {
            sendToAnyConnectedWsUser(it.phoneNumber.e164, sms)
        }
    }

    private fun sendToAnyConnectedWsUser(e164: String, sms: Sms) {
        val ctx = connectedWsUsers[e164]
        ctx?.send(sms)
    }
}
