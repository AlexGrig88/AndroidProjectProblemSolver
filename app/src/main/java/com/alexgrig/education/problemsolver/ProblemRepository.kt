package com.alexgrig.education.problemsolver

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.alexgrig.education.problemsolver.database.ProblemDatabase
import com.alexgrig.education.problemsolver.entities.Problem
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class ProblemRepository private constructor(context: Context) {

    private val database: ProblemDatabase = Room.databaseBuilder(
        context.applicationContext,
        ProblemDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val problemDao = database.problemDao()
    private val executor = Executors.newSingleThreadExecutor()
    private val allProblems: LiveData<List<Problem>>

    init {
        allProblems = problemDao.getProblems()
    }


    fun getProblems(): LiveData<List<Problem>> = allProblems

    fun getProblem(id: UUID): LiveData<Problem?> = problemDao.getProblem(id)

    fun updateProblem(problem: Problem) {
        executor.execute {
            problemDao.updateProblem(problem)
        }
    }

    fun addProblem(problem: Problem) {
        executor.execute {
            problemDao.addProblem(problem)
        }
    }

    fun deleteProblem(problem: Problem) {
        executor.execute {
            problemDao.deleteProblem(problem)
        }
    }

    //не берём размер списка из allProblems, потому что нам не нужен доступ к LiveData
    //Нам нужно получить статическую инфу при начальной загрузке база данных
    fun getSizeRepository(): Int {
        var size = 0
        val future = executor.submit(Callable<Int> { problemDao.getSizeDatabase() })
        size = future.get()
        return size
    }



    companion object {

        private const val DATABASE_NAME = "problem-database"

        private var instance: ProblemRepository? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = ProblemRepository(context)
            }
        }

        fun get(): ProblemRepository = instance
            ?: throw IllegalStateException("ProblemRepository must be initialize")

    }
}