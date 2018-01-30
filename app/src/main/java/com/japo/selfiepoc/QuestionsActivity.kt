package com.japo.selfiepoc

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.japo.selfiepoc.client.payload.PutAnswersPayload
import com.japo.selfiepoc.client.response.GetQuestionsResponse
import com.japo.selfiepoc.client.response.IdentityVerificationResponse
import kotlinx.android.synthetic.main.activity_question.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionsActivity : AppCompatActivity() {

    private val app = MyApplication.instance
    private val service = app.service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        if (app.verificationId == null) {
            back()
        } else {
            service.getQuestion(app.verificationId!!).enqueue(object : Callback<List<GetQuestionsResponse>> {
                override fun onResponse(call: Call<List<GetQuestionsResponse>>?, response: Response<List<GetQuestionsResponse>>?) {
                    app.questions = response?.body()!!
                    fragmentManager.beginTransaction().add(R.id.questionFragment, QuestionsFragment()).commit()
                    progressBar.visibility = View.INVISIBLE
                }

                override fun onFailure(call: Call<List<GetQuestionsResponse>>?, t: Throwable?) {
                    back()
                    progressBar.visibility = View.INVISIBLE
                }
            })
        }

        backBtn.setOnClickListener { back() }
        nextBtn.setOnClickListener { next() }

    }

    private fun replaceFragment() {
        val transaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.questionFragment, QuestionsFragment())
        transaction.commit()
    }

    private fun back() {
        if (app.currentQuestion == 0) {
            startActivity(Intent(this@QuestionsActivity, InitialActivity::class.java))
        } else {
            app.currentQuestion = app.currentQuestion - 1
            replaceFragment()
        }
    }

    private fun next() {
        if (app.currentQuestion < 3) {
            app.currentQuestion = app.currentQuestion + 1
            replaceFragment()
        } else {
            progressBar.visibility = View.VISIBLE
            service.postAnswers(app.verificationId!!, PutAnswersPayload(app.answers)).enqueue(object : Callback<IdentityVerificationResponse> {
                override fun onResponse(call: Call<IdentityVerificationResponse>?, response: Response<IdentityVerificationResponse>?) {
                    if (response?.body()?.nextStep == "SELFIE") {
                        startActivity(Intent(this@QuestionsActivity, SelfieActivity::class.java))
                    }
                }

                override fun onFailure(call: Call<IdentityVerificationResponse>?, t: Throwable?) {
                    progressBar.visibility = View.INVISIBLE
                }
            })
        }
    }

}
