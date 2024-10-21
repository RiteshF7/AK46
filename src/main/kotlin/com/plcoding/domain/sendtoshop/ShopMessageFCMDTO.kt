package com.plcoding.domain.sendtoshop

import com.plcoding.data.Actions

data class ShopMessageFCMDTO(
    val action: Actions,
    val payload: Map<String, String>
)