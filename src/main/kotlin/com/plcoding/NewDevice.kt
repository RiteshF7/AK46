package com.plcoding

data class NewDevice(
    var shopId: String = "dump_shop",
    val fcmToken: String,
    val manufacturer: String = "",
    val brand: String = "",
    val modelNumber: String = "",
    val androidVersion: String = "",
    val imeiOne: String = "11111111",
    val imeiTwo: String = "11111111",
    val isRegComplete: Boolean = false,
    val deviceCode: String = "",
    val costumerName: String = "",
    val costumerPhone: String = "",
    val emiPerMonth: String = "",
    val dueDate: String = "",
    val durationInMonths: String = "",
    val timeStamp: Any = Any(),
)
