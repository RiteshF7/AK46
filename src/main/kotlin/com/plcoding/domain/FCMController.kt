package com.plcoding.domain

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.gson.Gson
import com.plcoding.data.ActionMessageDTO
import com.plcoding.data.MessageType
import com.plcoding.data.fromDTOToString
import com.plcoding.domain.sendtoclient.ClientMessageRequestDTO
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class FCMController(
    private val call: RoutingCall,
) {
    private val smsFcmToken = ""
    suspend fun control() {
        val actionMessageDTO = call.receiveNullable<ActionMessageDTO>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@control
        }
        val fcmToken = if (actionMessageDTO.sendOffline) smsFcmToken  else actionMessageDTO.fcmToken


        val firebaseMessaging = FirebaseMessaging.getInstance()
        val actionMessageDTOString = actionMessageDTO.fromDTOToString()
        println(actionMessageDTOString)
        val message = Message.builder().putData(
            "ActionMessageDTO",actionMessageDTOString
        ).apply {
            setToken(fcmToken)
        }.build()
        firebaseMessaging.send(message)

    }


}