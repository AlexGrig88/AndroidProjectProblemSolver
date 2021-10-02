package com.alexgrig.education.problemsolver.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alexgrig.education.problemsolver.databinding.FragmentProblemBinding
import com.alexgrig.education.problemsolver.entities.Problem
import com.alexgrig.education.problemsolver.viewmodels.ProblemDetailViewModel
import androidx.lifecycle.Observer
import com.alexgrig.education.problemsolver.R
import com.alexgrig.education.problemsolver.utils.StateOfProblem
import java.util.*

class ProblemFragment: Fragment(), DatePickerDialogFragment.Callbacks {

    private lateinit var binding: FragmentProblemBinding
    lateinit var problem: Problem
    private val problemDetailViewModel by viewModels<ProblemDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        problem = Problem()
        val problemId: UUID = arguments?.getSerializable(ARG_PROBLEM_ID) as UUID
        //live data обновятся
        problemDetailViewModel.loadProblem(problemId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProblemBinding.inflate(inflater, container, false)
        binding.waitingState.isChecked = true

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //наблюдаем: если состояние объекта Problem изменилось, перезаписываем глобальный
        //problem и обноаляем вьюху
        problemDetailViewModel.problemLiveData.observe(
            viewLifecycleOwner,
            Observer { problem ->
                problem?.let {
                    this.problem = problem
                    updateUI()
                }
            }
        )
    }

    private fun updateUI() {
        binding.apply {
            problemTitle.setText(problem.title)
            problemDateButton.text = problem.getSimpleDate()
            when (problem.state) {
                StateOfProblem.Waiting -> waitingState.isChecked = true
                StateOfProblem.Solved -> solvedState.isChecked = true
                StateOfProblem.Failed -> failedState.isChecked = true
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // empty
            }

            override fun onTextChanged(sequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                problem.title = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                // empty
            }

        }

        binding.problemTitle.addTextChangedListener(titleWatcher)
        binding.radioGroupState.setOnCheckedChangeListener { group, id ->
            onCheckChanged(id)
        }

        binding.problemDateButton.setOnClickListener {
            Toast.makeText(requireContext(), "Tap", Toast.LENGTH_SHORT).show()
        }

        binding.problemDateButton.setOnClickListener {
            DatePickerDialogFragment.newInstance(problem.date).apply {
                setTargetFragment(this@ProblemFragment, REQUEST_DATE_CODE)
                show(this@ProblemFragment.parentFragmentManager, DIALOG_DATE)
            }
        }
    }

    //обрабатываем события изменения положения радио батона
    private fun onCheckChanged(id: Int) {
        when (id) {
            R.id.waitingState -> problem.state = StateOfProblem.Waiting
            R.id.solvedState -> problem.state = StateOfProblem.Solved
            R.id.failedState -> problem.state = StateOfProblem.Failed
        }
    }
    override fun onDateSelected(date: Date) {
        problem.date = date
        updateUI()
    }


    override fun onStop() {
        super.onStop()
        problemDetailViewModel.saveProblem(problem)
    }

    ////////////////////////  STATIC CONTEXT  /////////////////////////////////
    companion object {
        private const val ARG_PROBLEM_ID = "problem id argument"
        private const val DIALOG_DATE = "DialogDate"
        private const val DATE_FORMAT = "EEE, MMM, dd"
        private const val REQUEST_DATE_CODE = 0

        @JvmStatic
        fun newInstance(problemId: UUID): ProblemFragment {
            val args = Bundle().apply {
                putSerializable(ARG_PROBLEM_ID, problemId)
            }
            return ProblemFragment().apply {
                arguments = args
            }
        }

    }

}