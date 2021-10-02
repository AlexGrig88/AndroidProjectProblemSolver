package com.alexgrig.education.problemsolver.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.alexgrig.education.problemsolver.entities.Problem
import java.util.*

@Dao
interface ProblemDao {

    @Query("SELECT * FROM problem")
    fun getProblems(): LiveData<List<Problem>>

    @Query("SELECT * FROM problem WHERE id=(:id)")
    fun getProblem(id: UUID): LiveData<Problem?>

    @Insert
    fun addProblem(problem: Problem)

    @Update
    fun updateProblem(problem: Problem)

    @Delete
    fun deleteProblem(problem: Problem)

    @Query("SELECT COUNT(*) FROM problem")
    fun getSizeDatabase(): Int
}