package com.japo.selfiepoc.client.response

import com.google.gson.annotations.SerializedName

class PostSelfieResponse(
        @SerializedName("img_url")
        val imgUrl: String
)

