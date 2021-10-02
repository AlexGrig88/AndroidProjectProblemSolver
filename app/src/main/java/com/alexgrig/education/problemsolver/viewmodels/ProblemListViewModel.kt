package com.alexgrig.education.problemsolver.viewmodels

import androidx.lifecycle.ViewModel
import com.alexgrig.education.problemsolver.ProblemRepository
import com.alexgrig.education.problemsolver.entities.Problem

class ProblemListViewModel : ViewModel() {

    private val problemRepository = ProblemRepository.get()
    val problemListLiveData = problemRepository.getProblems()

    fun getSize(): Int = problemRepository.getSizeRepository()
    //var counterProblems = 0

    fun addProblem(problem: Problem) {
        problemRepository.addProblem(problem)
        //counterProblems++
    }


}