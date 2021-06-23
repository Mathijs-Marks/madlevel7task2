package com.example.madlevel7task2.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.madlevel7task2.model.Question
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class QuestionRepository {

    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var questionDocument =
        firestore.collection("questions").document("question")

    private val _question: MutableLiveData<Question> = MutableLiveData()

    val question: LiveData<Question>
        get() = _question

    suspend fun getQuestion() {
        try {
            // Firestore has support for coroutines via the extra dependency we've added :)
            withTimeout(5_000) {
                val data = questionDocument
                    .get()
                    .await()

                val question = data.getString("question").toString()
                val answer1 = data.getString("answer1").toString()
                val answer2 = data.getString("answer2").toString()
                val answer3 = data.getString("answer3").toString()
                val imageUri = data.getString("imageUri").toString()

                _question.value = Question(question, answer1, answer2, answer3, imageUri)
            }
        } catch (e: Exception) {
            throw QuestionRetrievalError("Retrieval-firebase-task was unsuccessful")
        }
    }

    class QuestionRetrievalError(message: String) : Exception(message)
}