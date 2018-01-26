package com.japo.selfiepoc.client

import com.japo.selfiepoc.client.response.IdentityVerificationResponse
import com.japo.selfiepoc.client.response.PostSelfieResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ServiceClient {

    @POST("/identity/verifications")
    fun createProcess(@Header("consumer_id") consumerId: String) : Call<IdentityVerificationResponse>

    @GET("/identity/verifications/me")
    fun getProcess(@Header("consumer_id") consumerId: String) : Call<IdentityVerificationResponse>

    @Multipart
    @PUT("/identity/verifications/{verification_id}/selfie")
    fun postSelfie(
            @Path("verification_id") verificationId: String,
            @Part("image\"; filename=\"image.png\" ") image: RequestBody): Call<PostSelfieResponse>
}
