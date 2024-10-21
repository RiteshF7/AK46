package com.plcoding.domain.sendtoshop

import com.plcoding.data.Actions

data class ShopMessageRequestDTO(
    val action: Actions,
    val payload: Map<String, String>,
    val shopFCMToken: String,
    val getViaSms: Boolean = false,
)
