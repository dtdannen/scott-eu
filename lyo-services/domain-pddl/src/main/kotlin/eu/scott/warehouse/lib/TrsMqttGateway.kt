package eu.scott.warehouse.lib

import eu.scott.warehouse.domains.trs.TrsXConstants
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import org.apache.jena.rdf.model.Model
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

/**
 * TBD
 *
 * @version $version-stub$
 * @since FIXME
 */
class TrsMqttGateway @Throws(MqttException::class) constructor(brokerURL: String, clientID: String,
                                                               options: MqttConnectOptions,
                                                               callback: MqttCallbackExtended?,
                                                               private val registrationAgent: RegistrationAgent) {
    val mqttClient: MqttClient = MqttClient(brokerURL, clientID)
    val executorService: ExecutorService = Executors.newSingleThreadScheduledExecutor()

    val clientId: String
        get() = mqttClient.clientId

    init {
        if (callback != null) {
            mqttClient.setCallback(callback)
        }
        val lastWill = registrationAgent.lastWill
        if (lastWill != null) {
            options.setWill(lastWill.topic, lastWill.messageBytes, 2, false)
        }
        mqttClient.connect(options)
        this.registrationAgent.register(this)
    }

    @Throws(MqttException::class)
    fun disconnect() {
        registrationAgent.unregister(this)
        mqttClient.disconnect()
    }

    @Throws(MqttException::class)
    fun subscribe(topic: String, listener: IMqttMessageListener) {
        mqttClient.subscribe(topic, listener)
    }

    @Throws(MqttException::class)
    fun publish(topic: String, message: MqttMessage) {
        mqttClient.publish(topic, message)
    }

    @Throws(MqttException::class)
    fun publish(topic: String, messageModel: Model) {
        publish(topic,
            MqttHelper.msgFromModel(TrsXConstants.rdfFormat, messageModel))
    }
}
