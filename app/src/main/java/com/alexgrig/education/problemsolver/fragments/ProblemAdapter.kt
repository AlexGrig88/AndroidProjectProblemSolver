package com.alexgrig.education.problemsolver.fragments

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alexgrig.education.problemsolver.R
import com.alexgrig.education.problemsolver.databinding.ItemListProblemBinding
import com.alexgrig.education.problemsolver.entities.Problem
import com.alexgrig.education.problemsolver.utils.StateOfProblem
import java.util.*

interface ProblemActionListener {

    fun onProblemDelete(problem: Problem)

    fun onProblemSelected(problemId: UUID)
}

interface ProblemItemMovable {
    fun onProblemMove(problem: Problem, moveTo: Int)
}

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
    RecyclerView.Adapter<ProblemAdapter.ProblemHolder>(), View.OnClickListener {

    var problemMovable: ProblemItemMovable? = null
    var actionListener: ProblemActionListener? = null

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

        binding.root.setOnClickListener(this)
        binding.moreImageView.setOnClickListener(this)

        return ProblemHolder(binding)
    }

    override fun onBindViewHolder(holder: ProblemHolder, position: Int) {
        val problem = problems[position]

        holder.itemView.tag = problem
        with(holder.binding) {
            moreImageView.tag = problem
            problemTitleItem.text = problem.title
            problemDateItem.text = problem.getSimpleDate()
            val suspectName = if (problem.suspect.isNotBlank()) problem.suspect else "not chosen"
            suspectNameForList.text = suspectNameForList.text.toString() + suspectName

            when(problem.state) {
                StateOfProblem.Waiting -> problemStateInnerImage.setImageResource(R.drawable.ic_waiting_24)
                StateOfProblem.Solved -> problemStateInnerImage.setImageResource(R.drawable.ic_solved_24)
                StateOfProblem.Failed -> problemStateInnerImage.setImageResource(R.drawable.ic_failed_24)
            }
        }
    }

    override fun getItemCount(): Int = problems.size

    //======================ViewHolder class===================================
    inner class ProblemHolder(val binding: ItemListProblemBinding) :
        RecyclerView.ViewHolder(binding.root)
    //=========================================================================

    override fun onClick(view: View) {
        val problem = view.tag as Problem
        when (view.id) {
            R.id.moreImageView -> {
                showPopupMenu(view)
                //Toast.makeText(view.context, "Tuppppp", Toast.LENGTH_SHORT).show()
            }
            else -> {
                actionListener?.onProblemSelected(problem.id)
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val context = view.context
        val problem = view.tag as Problem
        val position = problems.indexOfFirst { it.id == problem.id }

        popupMenu.menu.add(1, ID_MOVE_UP, Menu.NONE, context.getString(R.string.move_up)).apply {
            isEnabled = position > 0
        }
        popupMenu.menu.add(3, ID_MOVE_DOWN, Menu.NONE, context.getString(R.string.move_down)).apply {
            isEnabled = position < problems.size - 1
        }
        popupMenu.menu.add(6, ID_REMOVE, Menu.NONE, context.getString(R.string.remove_text_popupmenu))

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_MOVE_UP -> {
                    problemMovable?.onProblemMove(problem, -1)
                }
                ID_MOVE_DOWN -> {
                    problemMovable?.onProblemMove(problem, 1)
                }
                ID_REMOVE -> {
                    actionListener?.onProblemDelete(problem)
                }
            }
            return@setOnMenuItemClickListener true
        }

        popupMenu.show()

    }

    companion object {
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
    }

}