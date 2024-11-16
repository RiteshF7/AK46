package com.plcoding

import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.messaging.FirebaseMessaging
import com.plcoding.data.AndroidConfig
import com.plcoding.domain.FCMController
import com.trex.rexnetwork.data.UpdateTokenRequest
import io.ktor.http.*
import kotlin.random.Random
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
    route("/health") {
        get {
            call.respondText("OK", contentType = ContentType.Text.Plain)
        }
    }
    route("/updateDeviceFcmToken") {
        post {
            val updateTokenRequest = call.receiveNullable<UpdateTokenRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val firestore = FirestoreClient.getFirestore()

            println("received message body: ${updateTokenRequest.shopId}")
            try {
                withContext(Dispatchers.IO) {
                    val docRef =
                        firestore.document("shops/${updateTokenRequest.shopId}/devices/${updateTokenRequest.deviceId}")

                    // First, check if the document already exists
                    val documentSnapshot = docRef.get().get()
                    if (documentSnapshot.exists()) {
                        docRef.update(mapOf(NewDevice::fcmToken.name to updateTokenRequest.token))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Device doesn't exists!.")
                    }
                }

                call.respond(HttpStatusCode.OK, "Device saved successfully")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error saving token: ${e.message}")
            }


            call.respond(HttpStatusCode.OK)
        }
    }

    route("/verifycode") {
        get {
            val code = call.request.queryParameters["code"]
            val shopId = call.request.queryParameters["shopId"]
            val deviceId = call.request.queryParameters["deviceId"]

            if (code == null || shopId == null || deviceId == null) {
                call.respond(false)
                return@get
            }

            val firestore = FirestoreClient.getFirestore()

            try {
                withContext(Dispatchers.IO) {
                    val docRef = firestore.document("shops/${shopId}/devices/${deviceId}")
                    val documentSnapshot = docRef.get().get()

                    if (documentSnapshot.exists()) {
                        val originalCode = documentSnapshot.getString("unlockCode")

                        if (originalCode == code) {
                            // Generate new random code
                            val newCode = Random.nextInt(100000, 999999).toString()

                            // Update the unlock code
                            docRef.update("unlockCode", newCode).get()
                            call.respond(true)
                            return@withContext
                        }
                    }
                    call.respond(false)
                }
            } catch (e: Exception) {
                println("Error verifying code: ${e.message}")
                call.respond(false)
            }
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

    route("sendActionMessage") {
        post {
            FCMController(call).control()
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