package com.plcoding.domain.sendtoclient

import com.plcoding.data.Actions

data class ClientMessageFCMDTO(
    val action: Actions,
    val getViaSMS: Boolean = false,
    val payload: Map<String, String>,
    val shopFCMToken: String
)