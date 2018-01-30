package com.japo.selfiepoc

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.japo.selfiepoc.client.response.IdentityVerificationResponse
import kotlinx.android.synthetic.main.activity_initial.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InitialActivity : AppCompatActivity() {
    val app = MyApplication.instance

    val service = app.service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)
        button.setOnClickListener { createOrGetProcess() }
        consumerIdInput.setText(app.consumerId)
        apiHostInput.setText(app.baseUrl)
    }

    private fun createOrGetProcess() {
        val consumerId = consumerIdInput.text.toString()
        app.consumerId = consumerId
        if (apiHostInput.text.toString() != app.baseUrl) {
            app.baseUrl = apiHostInput.text.toString()
            app.updateService()
        }
        status.text = "Creating process..."
        progressBar.visibility = View.VISIBLE
        service.createProcess(consumerId).enqueue(object : Callback<IdentityVerificationResponse> {
            override fun onResponse(call: Call<IdentityVerificationResponse>?, response: Response<IdentityVerificationResponse>?) {
                if (response?.code() == 409) {
                    status.text = "Creation failed, getting..."
                    service.getProcess(consumerId).enqueue(object : Callback<IdentityVerificationResponse> {
                        override fun onResponse(call: Call<IdentityVerificationResponse>?, response: Response<IdentityVerificationResponse>?) {
                            app.verificationId = response?.body()?.verificationId
                            status.text = "ID: ${app.verificationId}, next step: ${response?.body()?.nextStep}"
                            nextIntent(response?.body()?.nextStep)
                        }

                        override fun onFailure(call: Call<IdentityVerificationResponse>?, t: Throwable?) {
                            status.text = "Failure"
                            progressBar.visibility = View.INVISIBLE
                        }
                    })

                } else {
                    app.verificationId = response?.body()?.verificationId
                    status.text = "ID: ${app.verificationId}, next step: ${response?.body()?.nextStep}"
                    progressBar.visibility = View.INVISIBLE
                    nextIntent(response?.body()?.nextStep)
                }
            }

            override fun onFailure(call: Call<IdentityVerificationResponse>?, t: Throwable?) {
                progressBar.visibility = View.INVISIBLE
                status.text = "Failure"
            }
        })
    }

    private fun nextIntent(nextStep: String?) {
        when (nextStep) {
            "QUESTION" -> questionIntent()
            "SELFIE" -> selfieIntent()
        }
    }

    private fun questionIntent() {
        app.currentQuestion = 0
        startActivity(Intent(this@InitialActivity, QuestionsActivity::class.java))
    }

    private fun selfieIntent() {
        startActivity(Intent(this@InitialActivity, SelfieActivity::class.java))

    }

}

