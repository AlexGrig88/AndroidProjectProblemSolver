package com.alexgrig.education.problemsolver

import android.app.Application

class AppProblemSolver: Application() {

    override fun onCreate() {
        super.onCreate()
        ProblemRepository.initialize(this)
    }

}