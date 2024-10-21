package com.plcoding.domain.sendtoclient

import com.plcoding.data.Actions


data class ClientMessageRequestDTO(
    val clientFCMToken: String,
    val sendViaSMS: Boolean = false,
    val getViaSMS: Boolean = false,
    val action: Actions,
    val payload: Map<String, String>,
    val shopFCMToken: String
)

