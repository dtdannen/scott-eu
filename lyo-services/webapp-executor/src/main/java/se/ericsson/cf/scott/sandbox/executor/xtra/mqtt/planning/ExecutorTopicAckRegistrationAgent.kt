package se.ericsson.cf.scott.sandbox.executor.xtra.mqtt.planning

import eu.scott.warehouse.lib.ChangeEventMqttMessageListener
import eu.scott.warehouse.lib.LastWillMessage
import eu.scott.warehouse.lib.MqttHelper
import eu.scott.warehouse.lib.MqttTopics
import eu.scott.warehouse.lib.TopicAckRegistrationAgent
import eu.scott.warehouse.lib.TrsMqttGateway
import eu.scott.warehouse.lib.PlanChangeEventListener
import eu.scott.warehouse.domains.trs.TrsServerAck
import eu.scott.warehouse.domains.trs.TrsServerAnnouncement
import eu.scott.warehouse.domains.trs.TrsXConstants
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper
import org.eclipse.lyo.oslc4j.provider.jena.LyoJenaModelException
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.ws.rs.core.UriBuilder

// FIXME Andrew@2019-04-16: delete this class
@Deprecated("Twin registration shall be done via REST calls")
class ExecutorTopicAckRegistrationAgent() :
        TopicAckRegistrationAgent<TrsServerAnnouncement, TrsServerAck>() {
    val log = LoggerFactory.getLogger(javaClass)
    override val lastWill: LastWillMessage
        get() = TODO()

    override fun register(gateway: TrsMqttGateway) {
        val latch = CountDownLatch(1)
        try {
            gateway.subscribe(MqttTopics.REGISTRATION_ACK,
                    IMqttMessageListener { _, message ->
                        completeRegistration(message, latch, gateway)
                    })
        } catch (e: MqttException) {
            log.error("Something went wrong with the REG_ACK subscription", e)
        }

        try {
            while (latch.count > 0) {
                log.trace("Posting the registration message")
                gateway.publish(MqttTopics.REGISTRATION_ANNOUNCE,
                        getExecutorRegistrationMessage(gateway.clientId))
                latch.await(5, TimeUnit.SECONDS)
                if (latch.count > 0) {
                    log.warn("Failed to register the PE with the WHC, attempting again")
                }
            }
        } catch (e: MqttException) {
            log.error("Failed to publish the twin registration message")
            log.debug("Failed to publish the twin registration message", e)
        } catch (e: InterruptedException) {
            log.error("The program was interrupted before the latch reached zero")
        }
    }

    // TODO should be defined here, I expect a ctor param for the base and += '/lwt' here
    private val lwtTopic: String
        get() = TODO()

    private fun getExecutorRegistrationMessage(id: String): MqttMessage {
        val trsUri = UriBuilder.fromUri(OSLC4JUtils.getServletURI()).path("trs").build()
        val announcement = TrsServerAnnouncement(id, TrsXConstants.TYPE_EXECUTOR, trsUri, lwtTopic, false)
        return MqttHelper.msgFromResources(TrsXConstants.rdfFormat, announcement)
    }

    private lateinit var trsTopic: String

    private fun completeRegistration(message: MqttMessage, latch: CountDownLatch,
                                     gateway: TrsMqttGateway) {
        val id = gateway.clientId
        val model = MqttHelper.extractModelFromMessage(message)
        try {
            val serverAck = JenaModelHelper.unmarshalSingle(model, TrsServerAck::class.java)
            if (id == serverAck.adaptorId) {
                log.debug("The WHC registration of the {} has been confirmed", id)
                trsTopic = serverAck.trsTopic
                log.info("Using the '{}' topic to monitor new plans", trsTopic)
                subscribeToPlans(trsTopic, gateway)
                latch.countDown()
            }
        } catch (e: LyoJenaModelException) {
            log.error("Error unmarshalling TrsServerAck from the server response", e)
        }
    }


    override fun unregister(gateway: TrsMqttGateway) {
        gateway.publish(lwtTopic, lastWill.message)
    }

    private fun subscribeToPlans(trsTopic: String, gateway: TrsMqttGateway) {
        gateway.subscribe(trsTopic, ChangeEventMqttMessageListener(
            PlanChangeEventListener(gateway.executorService)))
    }

}
