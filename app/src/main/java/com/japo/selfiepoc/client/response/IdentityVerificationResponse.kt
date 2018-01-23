package com.japo.selfiepoc.client.response

import com.google.gson.annotations.SerializedName

class IdentityVerificationResponse(
        @SerializedName("verification_id")
        val verificationId: String,
        @SerializedName("consumer_id")
        val consumerId: String,
        @SerializedName("next_step")
        val nextStep: String
)
