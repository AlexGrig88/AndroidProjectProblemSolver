package com.alexgrig.education.problemsolver

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentResultListener
import com.alexgrig.education.problemsolver.databinding.ActivityMainBinding
import com.alexgrig.education.problemsolver.entities.Problem
import com.alexgrig.education.problemsolver.fragments.*
import com.alexgrig.education.problemsolver.viewmodels.ProblemDetailViewModel
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
        //удаление problem происходит в обработчике события диалога (в классе ConfirmationDialogFragment)
        val dialogFragment = ConfirmationDialogFragment.newInstance(problem)
        dialogFragment.show(supportFragmentManager, ConfirmationDialogFragment.TAG)
    }

}