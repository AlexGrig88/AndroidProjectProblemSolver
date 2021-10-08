package com.alexgrig.education.problemsolver.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.alexgrig.education.problemsolver.ORDER_PREFERENCES
import com.alexgrig.education.problemsolver.R
import com.alexgrig.education.problemsolver.SET_ORDERED_IDs_VALUE
import com.alexgrig.education.problemsolver.entities.Problem
import com.alexgrig.education.problemsolver.viewmodels.ProblemDetailViewModel

class ConfirmationDialogFragment: DialogFragment() {

    lateinit var problem: Problem
    private val problemDetailViewModel by viewModels<ProblemDetailViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        problem = requireArguments().getSerializable(KEY_ARG_PROBLEM) as Problem

        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val preferences = requireActivity().getSharedPreferences(ORDER_PREFERENCES, Context.MODE_PRIVATE)
                    //получаем наши preferences удаляем id переданного problem и перезаписываем обновлённые preferences
                    val list = preferences.getString(SET_ORDERED_IDs_VALUE, "")!!.split(", ").toMutableList()
                    list.remove(problem?.id?.toString())
                    preferences.edit()
                        .putString(SET_ORDERED_IDs_VALUE, list.joinToString())
                        .apply()

                    problemDetailViewModel.deleteProblem(problem)
                }
                DialogInterface.BUTTON_NEGATIVE -> false
            }
        }

        return AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setIcon(R.drawable.ic_delete_24)
            .setTitle(R.string.confirmation)
            .setMessage(R.string.text_query_on_delete_problem)
            .setPositiveButton(R.string.yes, listener)
            .setNegativeButton(R.string.no, listener)
            .create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        Toast.makeText(requireContext(), "Dialog canceled", Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic val TAG: String = ConfirmationDialogFragment::class.java.simpleName
        @JvmStatic val REQUEST_KEY_CONFIRM_DIALOG = "$TAG:defaultRequestKey"
        @JvmStatic val KEY_RESPONSE_ON_DELETE = "RESPONSE"
        @JvmStatic val KEY_ARG_PROBLEM = "RESPONSE"

        fun newInstance(problem: Problem): ConfirmationDialogFragment {
            val args = Bundle().apply {
                putSerializable(KEY_ARG_PROBLEM, problem)
            }
            return ConfirmationDialogFragment().apply {
                arguments = args
            }
        }

    }
}