package com.plcoding.data

import com.google.gson.Gson
import java.util.*


data class ActionMessageDTO(
    val fcmToken: String,
    val action: Actions,
    val payload: Map<String, String> = mapOf(),
    val sendOffline: Boolean = false,
    val requestId: String = UUID.randomUUID().toString(),

    )


fun ActionMessageDTO.fromJsonToDTO(json: String): ActionMessageDTO? {
    val actionMessageDTO = Gson().fromJson(json, ActionMessageDTO::class.java)
    return actionMessageDTO
}

fun ActionMessageDTO.fromDTOToString(): String? {
    val json = Gson().toJson(this)
    return json
}
