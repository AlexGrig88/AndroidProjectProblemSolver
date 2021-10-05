package com.alexgrig.education.problemsolver.fragments

import android.content.Context
import android.content.SharedPreferences
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
import com.alexgrig.education.problemsolver.ORDER_PREFERENCES
import com.alexgrig.education.problemsolver.SET_ORDERED_IDs_VALUE
import com.alexgrig.education.problemsolver.utils.KeepingOrder
import java.util.*
import kotlin.collections.ArrayList

class ProblemListFragment : Fragment(), ProblemItemMovable {

    lateinit var binding: FragmentProblemListBinding
    private val problemListViewModel by viewModels<ProblemListViewModel>()

    private var actionListenerAsContext: ProblemActionListener? = null
    private var adapter: ProblemAdapter = ProblemAdapter(emptyList())
    private lateinit var problemRecyclerView: RecyclerView

    private var counterProblems = 0
    private var isFirstCreatingView = false
    private lateinit var orderedPreferences: SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        actionListenerAsContext = context as ProblemActionListener
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

        Log.d(TAG, "========onCreateView() called==============")
        problemRecyclerView = binding.problemRecyclerView
        problemRecyclerView.layoutManager = LinearLayoutManager(context)

        //не забыть передать в адаптер callback полученный при прикреплении фрагмента
        adapter.actionListener = actionListenerAsContext
        problemRecyclerView.adapter = adapter

        binding.emptyMsg.visibility = if (counterProblems == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }

        orderedPreferences = requireActivity().getSharedPreferences(ORDER_PREFERENCES, Context.MODE_PRIVATE)
 //       orderedPrefer.edit().clear().apply()
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
                actionListenerAsContext?.onProblemSelected(problem.id)
                counterProblems++
            }
        }
    }

    private fun updateUI(problemList: List<Problem>) {

        problemListViewModel.orderedList = KeepingOrder.restoreOrder(orderedPreferences, problemList)
        Log.i(TAG, "orderedList = ${problemListViewModel.orderedList}\n")
        adapter = ProblemAdapter(problemListViewModel.orderedList).apply { actionListener =
            this@ProblemListFragment.actionListenerAsContext
        }
        adapter.problemMovable = this
        problemRecyclerView.adapter = adapter
    }

    private fun printOrderFromPreferences() {
        val setPrefer = orderedPreferences.getString(SET_ORDERED_IDs_VALUE, "")
        Log.i(TAG, "item = $setPrefer\n")
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
                actionListenerAsContext?.onProblemSelected(problem.id)
                counterProblems++
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDetach() {
        super.onDetach()
        actionListenerAsContext = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "========onSaveInstanceState() called==============")
    }

    companion object {

        private const val TAG = "Problem list fragment"

        @JvmStatic
        fun newInstance(): ProblemListFragment {
            return ProblemListFragment()
        }
    }

    override fun onProblemMove(problem: Problem, moveTo: Int) {
        //получаем изменяемый упорядоченный список, двигая элемент списка,
        //меняем местами элементы, сохраняем состояние в preferences
        val problemList = problemListViewModel.orderedList
        val oldIndex = problemList.indexOfFirst { it.id == problem.id }
        if (oldIndex == -1) return
        val newIndex = oldIndex + moveTo
        if (newIndex < 0 || newIndex >= problemList.size) return
        Collections.swap(problemList, oldIndex, newIndex)

        //Сохраняем упорядоченный список id в виде строки в sharedPreference
        val ids = problemList.joinToString { p -> p.id.toString() }
        orderedPreferences.edit()
            .putString(SET_ORDERED_IDs_VALUE, ids)
            .apply()

        updateUI(problemList)
    }
}