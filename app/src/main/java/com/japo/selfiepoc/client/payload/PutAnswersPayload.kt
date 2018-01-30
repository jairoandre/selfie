package com.japo.selfiepoc.client.payload

import com.google.gson.annotations.SerializedName

class PutAnswersPayload(
    @SerializedName("answers")
    val answers: List<String>
)

