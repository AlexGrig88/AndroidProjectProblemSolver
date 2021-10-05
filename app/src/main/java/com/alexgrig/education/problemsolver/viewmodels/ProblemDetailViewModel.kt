package com.alexgrig.education.problemsolver.viewmodels

import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.alexgrig.education.problemsolver.ProblemRepository
import com.alexgrig.education.problemsolver.entities.Problem
import java.util.*

class ProblemDetailViewModel : ViewModel() {

    private val problemRepository = ProblemRepository.get()
    private val mutableProblemIdLiveData = MutableLiveData<UUID>()
    var problemLiveData: LiveData<Problem?> =
        Transformations.switchMap(mutableProblemIdLiveData) { problemId ->
            problemRepository.getProblem(problemId)
        }

    fun loadProblem(id: UUID) {
        mutableProblemIdLiveData.value = id
    }

    fun saveProblem(problem: Problem) {
        problemRepository.updateProblem(problem)
    }

    fun deleteProblem(problem: Problem) {
        problemRepository.deleteProblem(problem)
    }

}