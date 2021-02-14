package com.mhapp.event.service

import com.mhapp.event.model.PushNotification
import com.mhapp.event.utils.Constants.Companion.CONTENT_TYPE
import com.mhapp.event.utils.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationService {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun pushNotification(@Body notification: PushNotification): Response<ResponseBody>
}