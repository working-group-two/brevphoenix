package com.brevphoenix.sms

import com.brevphoenix.PhoneNumber
import com.brevphoenix.auth.signedInUser
import io.javalin.http.Context
import io.javalin.http.pathParamAsClass
import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsConnectContext
import io.javalin.websocket.WsContext
import io.javalin.websocket.WsErrorContext

object SmsController {

    fun getSms(ctx: Context) {
        val user = ctx.signedInUser
        val smsList = SmsService.getSms(user)
        val smsBySender: Map<String, List<Sms>> = smsList.groupBy {
            when {
                (it.from as? Address.InternationalNumber)?.phoneNumber == user && (it.to as? Address.InternationalNumber)?.phoneNumber == user -> it.from.phoneNumber.e164
                (it.to as? Address.InternationalNumber)?.phoneNumber != user -> (it.to as Address.InternationalNumber).phoneNumber.e164
                it.from is Address.TextAddress -> it.from.text
                (it.from as? Address.InternationalNumber)?.phoneNumber != user -> (it.from as Address.InternationalNumber).phoneNumber.e164
                else -> null
            }
        }.filterKeys { it != null }
            .mapKeys { it.key!! }
        ctx.json(smsBySender)
    }

    fun sendSms(ctx: Context) {
        val user = ctx.signedInUser
        val text = ctx.body()
        val to = ctx.pathParamAsClass<PhoneNumber>("to").get()
        val id = SmsService.sendSms(from = user, to = to, text)
        ctx.json(id)
    }

    fun handleWsConnect(ctx: WsContext) {
        val e164 = ctx.signedInUser.e164
        SmsWsService.handleWsConnect(ctx as WsConnectContext, e164)
    }

    fun handleWsClose(ctx: WsContext) {
        val e164 = ctx.signedInUser.e164
        SmsWsService.handleWsClose(ctx as WsCloseContext, e164)
    }

    fun handleWsError(ctx: WsContext) {
        val e164 = ctx.signedInUser.e164
        SmsWsService.handleWsError(ctx as WsErrorContext, e164)
    }
}
