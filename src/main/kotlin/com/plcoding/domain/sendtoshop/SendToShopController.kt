package com.plcoding.domain.sendtoshop

import com.google.gson.Gson
import com.plcoding.data.MessageType
import com.plcoding.domain.FCMMessageHelper
import com.plcoding.domain.sendtoclient.ClientMessageFCMDTO
import com.plcoding.domain.sendtoclient.ClientMessageRequestDTO
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class SendToShopController(private val call: RoutingCall) {

    private val fcmHelper = FCMMessageHelper()

    suspend fun control() {

        val shopMessageRequestDTO = call.receiveNullable<ShopMessageRequestDTO>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@control
        }

        val getViaSMS = shopMessageRequestDTO.getViaSms
        val shopMessageFCMDTO = ShopMessageFCMDTO(
            shopMessageRequestDTO.action,
            shopMessageRequestDTO.payload
        )


        val clientMessageFCMDTOString = Gson().toJson(shopMessageFCMDTO)
        val fcmToken = if (getViaSMS) shopMessageRequestDTO.shopFCMToken else fcmHelper.smsServerFcmToken
        fcmHelper.sendFCMMessage(fcmToken, MessageType.CLIENT, clientMessageFCMDTOString)


    }
}