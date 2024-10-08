package com.plcoding

import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.messaging.FirebaseMessaging
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    route("/send") {
        post {
            val body = call.receiveNullable<SendMessageDto>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            println("received message body: ${body.action}")
            //uncomment this to send message
            FirebaseMessaging.getInstance().send(body.toMessage())

            call.respond(HttpStatusCode.OK)
        }
    }
}