package com.alexgrig.education.problemsolver.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alexgrig.education.problemsolver.R
import com.alexgrig.education.problemsolver.databinding.ItemListProblemBinding
import com.alexgrig.education.problemsolver.entities.Problem
import com.alexgrig.education.problemsolver.utils.StateOfProblem
import java.util.*



//===================DiffUtil.Callback====================================
class CrimesDiffCallback(
    private val oldList: List<Problem>,
    private val newList: List<Problem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldProblem = oldList[oldItemPosition]
        val newProblem = newList[newItemPosition]
        return oldProblem.id == newProblem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}
//===================DiffUtil.Callback====================================

class ProblemAdapter(_problems: List<Problem>) :
    RecyclerView.Adapter<ProblemAdapter.ProblemHolder>() {

    var callbacks: Callbacks? = null

    private var problems: List<Problem> = _problems
        set(newValue) {
            val diffCallback = CrimesDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
            //this.notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProblemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListProblemBinding.inflate(inflater, parent, false)
        return ProblemHolder(binding)
    }

    override fun onBindViewHolder(holder: ProblemHolder, position: Int) {
       holder.bind(problems[position])
    }

    override fun getItemCount(): Int = problems.size

    //======================ViewHolder class===================================
    inner class ProblemHolder(private val binding: ItemListProblemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private lateinit var problem: Problem

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(problem: Problem) {
            this.problem = problem
            binding.problemTitleItem.text = problem.title
            binding.problemDateItem.text = problem.getSimpleDate()
            when(problem.state) {
                StateOfProblem.Waiting -> binding.problemStateImg.setImageResource(R.drawable.ic_waiting_24)
                StateOfProblem.Solved -> binding.problemStateImg.setImageResource(R.drawable.ic_solved_24)
                StateOfProblem.Failed -> binding.problemStateImg.setImageResource(R.drawable.ic_failed_24)
            }
        }

        override fun onClick(v: View?) {
            callbacks?.onProblemSelected(this.problem.id)
        }

    }
    //=========================================================================
}