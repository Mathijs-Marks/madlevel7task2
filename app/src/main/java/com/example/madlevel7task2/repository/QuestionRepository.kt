package com.example.madlevel7task2.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.madlevel7task2.model.Question
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class QuestionRepository {

    // Retrieve an instance of Firestore.
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    // Specify the collection used from Firestore.
    private var questionDocument =
        firestore.collection("questions")

    private val _questions: MutableLiveData<List<Question>> = MutableLiveData()

    /**
     * Expose non MutableLiveData via getter
     * Encapsulation :)
     */
    val questions: LiveData<List<Question>>
        get() = _questions

    /**
     * This function is responsible for retrieving the list of questions from Firebase.
     * A get request is executed, when this is successful, a for loop is used to go through the
     * collection and retrieve the necessary values.
     * These values are then converted to a Question object.
     * After completion, the MutableLiveData list of questions is updated with the new values.
     */
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