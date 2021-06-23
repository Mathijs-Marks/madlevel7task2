package com.example.madlevel7task2.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.madlevel7task2.R
import com.example.madlevel7task2.databinding.FragmentQuestionBinding
import com.example.madlevel7task2.model.Question
import com.example.madlevel7task2.viewmodel.QuestionViewModel
import kotlinx.android.synthetic.main.fragment_question.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var questions: List<Question>
    private var questionCount = 0

    private val viewModel: QuestionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeQuestions()

        binding.btnConfirm.setOnClickListener { confirmAnswer() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeQuestions() {
        viewModel.getQuestion()
        viewModel.questions.observe(viewLifecycleOwner, {
            questions = it
            loadQuestions(questions[questionCount])
        })
    }

    private fun loadQuestions(question: Question) {
        binding.tvQuestionDescription.text = question.question
        binding.rbAnswer1.text = question.answer1
        binding.rbAnswer2.text = question.answer2
        binding.rbAnswer3.text = question.answer3
        context?.let { Glide.with(it).load(question.imageUri).into(binding.ivHvaLocation) }
        binding.tvQuestionProgressCount.text = getString(R.string.question_progress_count, questionCount + 1, questions.size)
    }

    private fun confirmAnswer() {
        /*
        Because you can't easily check whether a radio button is selected or not, you have to
        manually search the radio group if there is any radio button checked. If so, you can
        retrieve  the Id of that radio button. Then, using that Id, you can search the corresponding
        radio button and put the result (checked radio button) in a variable.
        */
        val selectedAnswerId = binding.rgAnswers.checkedRadioButtonId
        val selectedAnswer = view?.findViewById<RadioButton>(selectedAnswerId)

        if (selectedAnswer?.text == questions[questionCount].correctAnswer && questionCount < questions.size) {
            questionCount++
            if (questionCount >= questions.size) {
                findNavController().popBackStack()
                Toast.makeText(requireContext(), getString(R.string.quest_complete), Toast.LENGTH_LONG).show()
            } else {
                loadQuestions(questions[questionCount])
            }
        } else {
            Toast.makeText(requireContext(), R.string.wrong_answer, Toast.LENGTH_LONG).show()
        }
        binding.rgAnswers.clearCheck()
    }
}