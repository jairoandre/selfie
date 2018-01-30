package com.japo.selfiepoc

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import kotlinx.android.synthetic.main.question_view.*

class QuestionsFragment : Fragment() {

    private val app = MyApplication.instance
    private lateinit var radioButtons: List<RadioButton>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (container == null) return null
        return inflater?.inflate(R.layout.question_view, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        radioButtons = listOf(answer0, answer1, answer2, answer3)
        radioButtons.forEach { it.setOnClickListener { changeAnswer(it as RadioButton) } }
        prepareForm()
    }

    private fun changeAnswer(btn: RadioButton) {
        app.answers = app.answers.mapIndexed { idx, answer ->
            if (idx == app.currentQuestion) btn.text.toString() else answer
        }
    }

    private fun prepareForm() {
        val currentQuestion = app.questions[app.currentQuestion]
        questionText.text = currentQuestion.question
        questionText.visibility = View.VISIBLE
        radioButtons.forEachIndexed { idx, btn ->
            btn.text = currentQuestion.options[idx]
            btn.visibility = View.VISIBLE
            if (app.answers[app.currentQuestion] == btn.text.toString()) {
                answerGroup.check(btn.id)
            }
        }
    }
}
