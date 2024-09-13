package com.brevphoenix.sms

import com.brevphoenix.GrpcShared
import com.wgtwo.api.v1.sms.SmsProto
import com.wgtwo.api.v1.sms.SmsServiceGrpc
import org.slf4j.LoggerFactory

object SmsSendService {
    private const val SENDER_NAME = "BrevPhoenix"
    private val logger by lazy { LoggerFactory.getLogger(javaClass) }
    private val smsStub =
        SmsServiceGrpc.newBlockingStub(GrpcShared.channel).withInterceptors(GrpcShared.authInterceptor)

    fun sendFromSubscriber(from: String, to: String, content: String): String {
        val request = SmsProto.SendTextFromSubscriberRequest.newBuilder().apply {
            this.fromSubscriber = from
            this.toAddress = to
            this.content = content
        }.build()

        return sendTextFromSubscriber(request)
    }

    private fun sendTextFromSubscriber(request: SmsProto.SendTextFromSubscriberRequest): String {
        val response = smsStub.sendTextFromSubscriber(request)
        when (response.status) {
            SmsProto.SendMessageResponse.SendStatus.SEND_STATUS_OK -> return response.messageId
            SmsProto.SendMessageResponse.SendStatus.SEND_STATUS_UNSPECIFIED,
            SmsProto.SendMessageResponse.SendStatus.SEND_STATUS_REJECT,
            SmsProto.SendMessageResponse.SendStatus.SEND_STATUS_ERROR,
            SmsProto.SendMessageResponse.SendStatus.UNRECOGNIZED,
                null,
            -> throw RuntimeException(response.description)
        }
    }

    fun sendToSubscriber(from: String, to: String, content: String) {
        val request = SmsProto.SendTextToSubscriberRequest.newBuilder().apply {
            this.fromAddress = from
            this.toSubscriber = to
            this.content = content
        }.build()

        sendTextToSubscriber(request)
    }

    private fun sendTextToSubscriber(request: SmsProto.SendTextToSubscriberRequest): String {
        val response = smsStub.sendTextToSubscriber(request)
        when (response.status) {
            SmsProto.SendMessageResponse.SendStatus.SEND_STATUS_OK -> return response.messageId
            SmsProto.SendMessageResponse.SendStatus.SEND_STATUS_UNSPECIFIED,
            SmsProto.SendMessageResponse.SendStatus.SEND_STATUS_REJECT,
            SmsProto.SendMessageResponse.SendStatus.SEND_STATUS_ERROR,
            SmsProto.SendMessageResponse.SendStatus.UNRECOGNIZED,
                null,
            -> throw RuntimeException(response.description)
        }
    }
}
