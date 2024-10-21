package com.plcoding.domain.sendtoclient

import com.google.gson.Gson
import com.plcoding.data.MessageType
import com.plcoding.domain.FCMMessageHelper
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class SendToClientController(private val call: RoutingCall) {
    private val fcmHelper = FCMMessageHelper();
    suspend fun control() {
        val clientMessageRequestDTO = call.receiveNullable<ClientMessageRequestDTO>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@control
        }

        val sendViaSMS = clientMessageRequestDTO.sendViaSMS
        val clientMessageFCMDTO = ClientMessageFCMDTO(
            clientMessageRequestDTO.action,
            clientMessageRequestDTO.getViaSMS,
            clientMessageRequestDTO.payload,
            clientMessageRequestDTO.shopFCMToken
        )

        val clientMessageFCMDTOString = Gson().toJson(clientMessageFCMDTO)
        val smsServerFcmToken = ""
        val fcmToken = if (sendViaSMS) clientMessageRequestDTO.clientFCMToken else smsServerFcmToken
        fcmHelper.sendFCMMessage(fcmToken, MessageType.CLIENT, clientMessageFCMDTOString)

    }

}