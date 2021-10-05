package com.alexgrig.education.problemsolver

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.alexgrig.education.problemsolver.databinding.ActivityMainBinding
import com.alexgrig.education.problemsolver.entities.Problem
import com.alexgrig.education.problemsolver.fragments.ProblemActionListener
import com.alexgrig.education.problemsolver.fragments.ProblemFragment
import com.alexgrig.education.problemsolver.fragments.ProblemListFragment
import com.alexgrig.education.problemsolver.utils.KeepingOrder
import com.alexgrig.education.problemsolver.viewmodels.ProblemDetailViewModel
import com.alexgrig.education.problemsolver.viewmodels.ProblemListViewModel
import java.util.*

const val ORDER_PREFERENCES = "order preferences"
const val SET_ORDERED_IDs_VALUE = "ordered ids"
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), ProblemActionListener {

    private lateinit var binding: ActivityMainBinding
    private val problemDetailViewModel by viewModels<ProblemDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //при первом создании активити он не найдёт фрвгмент, т.к. он ещё не добавлен в стек
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, ProblemListFragment.newInstance())
                .commit()
        }

    }

    override fun onProblemSelected(problemId: UUID) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, ProblemFragment.newInstance(problemId))
            .addToBackStack(null)
            .commit()
    }

    override fun onProblemDelete(problem: Problem) {

        val preferences = getSharedPreferences(ORDER_PREFERENCES, Context.MODE_PRIVATE)
        //получаем наши preferences удаляем id переданного problem и перезаписываем обновлённые preferences
        val list = preferences.getString(SET_ORDERED_IDs_VALUE, "")!!.split(", ").toMutableList()
        list.remove(problem.id.toString())
        preferences.edit()
            .putString(SET_ORDERED_IDs_VALUE, list.joinToString())
            .apply()

        problemDetailViewModel.deleteProblem(problem)
    }
}