package com.japo.selfiepoc

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.japo.selfiepoc.client.RetrofitClient
import com.japo.selfiepoc.client.ServiceClient
import com.japo.selfiepoc.client.response.GetQuestionsResponse

class MyApplication : Application() {
    var verificationId: String? = null
    lateinit var service: ServiceClient
    var questions: List<GetQuestionsResponse> = emptyList()
    var answers = (0..3).map { "-1" }
    var currentQuestion = 0
    var consumerId: String = ""
    var baseUrl: String = "http://jairo:9002"

    companion object {
        lateinit var instance: com.japo.selfiepoc.MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fresco.initialize(this)
        updateService()
    }

    fun updateService() {
        service = RetrofitClient(baseUrl).client().create(ServiceClient::class.java)!!
    }


}
