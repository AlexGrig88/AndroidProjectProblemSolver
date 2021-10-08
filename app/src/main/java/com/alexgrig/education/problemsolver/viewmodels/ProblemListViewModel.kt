package com.alexgrig.education.problemsolver.viewmodels

import androidx.lifecycle.ViewModel
import com.alexgrig.education.problemsolver.ProblemRepository
import com.alexgrig.education.problemsolver.entities.Problem
import com.alexgrig.education.problemsolver.fragments.ProblemItemMovable
import java.util.*
import kotlin.collections.ArrayList

class ProblemListViewModel : ViewModel() {

    private val problemRepository = ProblemRepository.get()
    val problemListLiveData = problemRepository.getProblems()

    fun getSize(): Int = problemRepository.getSizeRepository()
    var orderedList: MutableList<Problem> = ArrayList<Problem>()

    fun addProblem(problem: Problem) {
        problemRepository.addProblem(problem)
        //counterProblems++
    }

}