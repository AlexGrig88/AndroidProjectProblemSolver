package com.alexgrig.education.problemsolver.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexgrig.education.problemsolver.R
import com.alexgrig.education.problemsolver.databinding.FragmentProblemListBinding
import com.alexgrig.education.problemsolver.entities.Problem
import com.alexgrig.education.problemsolver.viewmodels.ProblemListViewModel
import androidx.lifecycle.Observer
import java.util.*

interface Callbacks {
    fun onProblemSelected(crimeId: UUID)
}

class ProblemListFragment : Fragment() {

    lateinit var binding: FragmentProblemListBinding
    private val problemListViewModel by viewModels<ProblemListViewModel>()

    private var callbacksAsContext: Callbacks? = null
    private var adapter: ProblemAdapter = ProblemAdapter(emptyList())
    private lateinit var problemRecyclerView: RecyclerView
    private var counterProblems = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacksAsContext = context as Callbacks
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "========onCreate() called==============")
        super.onCreate(savedInstanceState)
        counterProblems = problemListViewModel.getSize()
        Log.i(TAG, "Size repo:  ${problemListViewModel.getSize()}")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProblemListBinding.inflate(layoutInflater, container, false)

        problemRecyclerView = binding.problemRecyclerView
        problemRecyclerView.layoutManager = LinearLayoutManager(context)
        //не забыть передать в адаптер callback полученный при прикреплении фрагмента
        adapter.callbacks = callbacksAsContext
        problemRecyclerView.adapter = adapter

        binding.emptyMsg.visibility = if (counterProblems == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        problemListViewModel.problemListLiveData.observe(
            viewLifecycleOwner,
            Observer {
                problems -> problems?.let {
                Log.i(TAG, "Got problems ${problems.size}")
                counterProblems = problems.size
                updateUI(problems)
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        binding.apply {
            addCrimeButtonFloating.setOnClickListener {
                val problem = Problem()
                problemListViewModel.addProblem(problem)
                callbacksAsContext?.onProblemSelected(problem.id)
                counterProblems++
            }
        }
    }

    private fun updateUI(problemList: List<Problem>) {
        //добавить функцию создающую список для отображения в сохранённом порядке
        //(порядок хранится в sharedPreferences)
        adapter = ProblemAdapter(problemList).apply { callbacks = callbacksAsContext }
        problemRecyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_problem_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.newProblem -> {
                val problem = Problem()
                problemListViewModel.addProblem(problem)
                callbacksAsContext?.onProblemSelected(problem.id)
                counterProblems++
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacksAsContext = null
    }

    companion object {

        private const val TAG = "Problem list fragment"

        @JvmStatic
        fun newInstance(): ProblemListFragment {
            return ProblemListFragment()
        }
    }
}