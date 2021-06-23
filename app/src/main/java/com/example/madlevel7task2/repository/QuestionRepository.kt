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
        firestore.collection("questions")

    private val _questions: MutableLiveData<List<Question>> = MutableLiveData()

    val questions: LiveData<List<Question>>
        get() = _questions

    suspend fun getQuestion() {
        try {
            // Firestore has support for coroutines via the extra dependency we've added :)
            withTimeout(5_000) {
                val questions = ArrayList<Question>(1)
                questionDocument
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val question = document.getString("question").toString()
                            val answer1 = document.getString("answer1").toString()
                            val answer2 = document.getString("answer2").toString()
                            val answer3 = document.getString("answer3").toString()
                            val correctAnswer = document.getString("correctAnswer").toString()
                            val imageUri = document.getString("imageUri").toString()
                            questions.add(
                                Question(
                                    question,
                                    answer1,
                                    answer2,
                                    answer3,
                                    correctAnswer,
                                    imageUri
                                )
                            )
                        }
                    }
                    .await()

                _questions.value = questions
            }
        } catch (e: Exception) {
            throw QuestionRetrievalError("Retrieval-firebase-task was unsuccessful")
        }
    }

    class QuestionRetrievalError(message: String) : Exception(message)
}