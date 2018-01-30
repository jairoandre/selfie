package com.japo.selfiepoc.client

import com.japo.selfiepoc.client.payload.PutAnswersPayload
import com.japo.selfiepoc.client.response.GetQuestionsResponse
import com.japo.selfiepoc.client.response.IdentityVerificationResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ServiceClient {

    @POST("/identity/verifications")
    fun createProcess(@Header("consumer_id") consumerId: String): Call<IdentityVerificationResponse>

    @GET("/identity/verifications/me")
    fun getProcess(@Header("consumer_id") consumerId: String): Call<IdentityVerificationResponse>

    @Multipart
    @PUT("/identity/verifications/{verification_id}/selfie")
    fun postSelfie(
        @Path("verification_id") verificationId: String,
        @Part("image\"; filename=\"image.png\" ") image: RequestBody): Call<IdentityVerificationResponse>

    @GET("/identity/verifications/{verification_id}/questions")
    fun getQuestion(
        @Path("verification_id") verificationId: String): Call<List<GetQuestionsResponse>>

    @PUT("/identity/verifications/{verification_id}/questions")
    fun postAnswers(
        @Path("verification_id") verificationId: String,
        @Body payload: PutAnswersPayload): Call<IdentityVerificationResponse>

}
