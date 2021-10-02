package com.alexgrig.education.problemsolver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alexgrig.education.problemsolver.databinding.ActivityMainBinding
import com.alexgrig.education.problemsolver.fragments.Callbacks
import com.alexgrig.education.problemsolver.fragments.ProblemFragment
import com.alexgrig.education.problemsolver.fragments.ProblemListFragment
import java.util.*

class MainActivity : AppCompatActivity(), Callbacks {

    private lateinit var binding: ActivityMainBinding

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}