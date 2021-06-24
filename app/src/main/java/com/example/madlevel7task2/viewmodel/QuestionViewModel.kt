package com.example.madlevel7task2.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.madlevel7task2.model.Question
import com.example.madlevel7task2.repository.QuestionRepository
import kotlinx.coroutines.launch

class QuestionViewModel(application: Application) : AndroidViewModel(application) {

    // This tag is used to quickly identify Firestore errors.
    private val TAG = "FIRESTORE"
    // In order to execute the Get request, an instance of the QuestionRepository is needed.
    private val questionRepository: QuestionRepository = QuestionRepository()

    // A LiveData list of questions is retrieved from the repository that can be used in the Fragments.
    val questions: LiveData<List<Question>> = questionRepository.questions

    private val _errorText: MutableLiveData<String> = MutableLiveData()
    val errorText: LiveData<String>
        get() = _errorText

    /**
     * This function tells the QuestionRepository to retrieve the questions from Firebase.
     */
    fun getQuestion() {
        viewModelScope.launch {
            try {
                questionRepository.getQuestion()
            } catch (ex: QuestionRepository.QuestionRetrievalError) {
                val errorMsg = "Something went wrong while retrieving the questions"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }
}