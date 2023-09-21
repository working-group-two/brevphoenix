package com.brevphoenix.sms

import com.brevphoenix.PhoneNumber
import com.brevphoenix.auth.signedInUser
import io.javalin.http.Context
import io.javalin.http.pathParamAsClass
import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsConnectContext
import io.javalin.websocket.WsContext

object SmsController {


    fun getSms(ctx: Context) {
        val user = ctx.signedInUser
        val smsList = SmsService.getSms(user)
        val smsBySender = smsList.groupBy(Sms::from).mapKeys { (k, _) ->
            when(k) {
                is Address.InternationalNumber -> k.phoneNumber.e164
                is Address.TextAddress -> k.text
            }
        }
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
}
