package com.brevphoenix.sms

import com.brevphoenix.GrpcShared
import com.wgtwo.api.v0.events.EventsProto
import com.wgtwo.api.v0.events.EventsServiceGrpc
import org.slf4j.LoggerFactory
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import com.google.protobuf.util.Durations
import io.grpc.stub.StreamObserver

object SmsEventService {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val listeners = Collections.newSetFromMap(ConcurrentHashMap<SmsReceiver, Boolean>())
    private val stub = EventsServiceGrpc.newStub(GrpcShared.channel).withInterceptors(GrpcShared.authInterceptor)

    fun init() {
        val request = subscribeEventsRequest()
        stub.subscribe(request, streamObserver(request))
    }

    fun addListener(listener: SmsReceiver) = listeners.add(listener)

    private fun handleSmsEvent(event: EventsProto.Event) {
        val sms: Sms = SmsMapper.parseEvent(event)
        logger.info("Processing received SMS. sms.id=${sms.eventId}")
        listeners.forEach { smsReceiver ->
            try {
                smsReceiver.onReceived(sms)
            } catch (e: Exception) {
                logger.warn("Uncaught exception from SMS listener", e)
            }
        }
    }

    private fun subscribeEventsRequest(): EventsProto.SubscribeEventsRequest =
        EventsProto.SubscribeEventsRequest.newBuilder()
            .addType(EventsProto.EventType.SMS_EVENT)
            .setManualAck(
                EventsProto.ManualAckConfig.newBuilder()
                    .setEnable(true)
                    .setTimeout(Durations.fromSeconds(15))
                    .build()
            )
            .build()

    private fun streamObserver(request: EventsProto.SubscribeEventsRequest): StreamObserver<EventsProto.SubscribeEventsResponse> =
        object : StreamObserver<EventsProto.SubscribeEventsResponse> {
            override fun onNext(response: EventsProto.SubscribeEventsResponse) {
                handleSmsEvent(response.event)
                acknowledge(response.event)
            }

            override fun onError(throwable: Throwable) {
                logger.warn("Got error: ${throwable.message}")
                subscribe()
            }

            override fun onCompleted() {
                logger.info("Connection closed by the server")
                subscribe()
            }

            private fun subscribe() {
                val newRequest = request.newBuilderForType().build()
                val newStreamObserver = streamObserver(newRequest)
                stub.subscribe(newRequest, newStreamObserver)
            }
        }


    private fun acknowledge(event: EventsProto.Event) {
        val request = ackRequest(event)
        stub.ack(request, object : StreamObserver<EventsProto.AckResponse> {
            override fun onNext(response: EventsProto.AckResponse) {
                logger.debug("Event successfully acknowledged")
            }

            override fun onError(throwable: Throwable) {
                logger.warn("Error acknowledging event: ${throwable.message}")
            }

            override fun onCompleted() {}
        })
    }

    private fun ackRequest(event: EventsProto.Event): EventsProto.AckRequest =
        EventsProto.AckRequest.newBuilder()
            .setSequence(event.metadata.sequence)
            .setInbox(event.metadata.ackInbox)
            .build()
}
