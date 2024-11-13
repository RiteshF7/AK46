package com.plcoding.domain

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.plcoding.data.MessageType
import com.plcoding.toMessage

class FCMMessageHelper {
    //todo fcm sms id
    val smsServerFcmToken = ""

    fun buildMessage(fcmToken: String, data: String): Message? {
        return Message.builder().putAllData(
            mapOf(
                "data" to data,
            )
        ).apply {
            setToken(fcmToken)
        }.build()

    }

    fun sendFCMMessage(fcmToken: String, type: MessageType, data: String) {
        val firebaseMessaging = FirebaseMessaging.getInstance()
        val message = buildMessage(fcmToken, data)
        firebaseMessaging.send(message)

    }
}