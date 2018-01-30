package com.japo.selfiepoc.client.response

import com.google.gson.annotations.SerializedName

class GetQuestionsResponse(
    @SerializedName("question")
    val question: String,
    @SerializedName("options")
    val options: List<String>
)
