package com.plcoding

import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.messaging.FirebaseMessaging
import com.plcoding.data.AndroidConfig
import com.plcoding.domain.sendtoclient.SendToClientController
import com.plcoding.domain.sendtoshop.SendToShopController
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

fun Route.sendNotification() {

    route("/") {
        get {
            call.respondText("Hello World!")
        }
    }

    route("/regdevice") {
        post {
            val device = call.receiveNullable<NewDevice>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            println("received message body: ${device.shopId}")
            val firestore = FirestoreClient.getFirestore()
            try {
                withContext(Dispatchers.IO) {
                    val docRef = firestore.document("shops/${device.shopId}/devices/${device.imeiOne}")

                    // First, check if the document already exists
                    val documentSnapshot = docRef.get().get()

                    if (!documentSnapshot.exists()) {
                        // Document doesn't exist, create it
                        docRef.set(device).get()
                    } else {
                        // Optionally handle the case where the document already exists
                        println("Device with IMEI ${device.imeiOne} already exists.")
                    }
                }

                call.respond(HttpStatusCode.OK, "Device saved successfully")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error saving device: ${e.message}")
            }


            call.respond(HttpStatusCode.OK)
        }
    }

    get("/file") {
        val filePath = "C:\\Users\\rites\\AndroidStudioProjects\\RASC\\app\\release\\file.apk"
        val file = File(filePath)

        if (file.exists()) {
            call.respondFile(file)
        } else {
            call.respond(HttpStatusCode.NotFound, "File not found")
        }
    }

    route("/sendtoclient") {
        post {
            SendToClientController(call).control()
        }

    }
    route("/sendtoshop") {
        post {
            SendToShopController(call).control()
        }

    }

    route("/send") {
        post {
            val body = call.receiveNullable<SendMessageDto>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val message = body.copy(android = AndroidConfig())
            println("received message body: $message")
            message.to?.let { token ->
                if (token.isNotBlank()) {
                    val firebaseMessaging = FirebaseMessaging.getInstance()
                    val messageID = firebaseMessaging.send(message.toMessage())


                } else {
                    println("Token is empty")
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}