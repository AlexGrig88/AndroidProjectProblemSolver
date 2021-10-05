package com.alexgrig.education.problemsolver.utils

import android.content.SharedPreferences
import android.util.Log
import com.alexgrig.education.problemsolver.SET_ORDERED_IDs_VALUE
import com.alexgrig.education.problemsolver.entities.Problem
import com.alexgrig.education.problemsolver.fragments.ProblemListFragment

class KeepingOrder {

    companion object {

        fun restoreOrder(preferences: SharedPreferences, problemList: List<Problem>): List<Problem> {


            var resultList = ArrayList<Problem>()
            preferences.getString(SET_ORDERED_IDs_VALUE, "")?.let {
                Log.i("Result", "orderedString= $it\n")
                Log.i("Result", "probList= $problemList\n============================")

                if (it.isBlank() || problemList.isEmpty()) {
                    resultList = problemList as ArrayList<Problem>
                    return@let
                }

                val orderedList = it.split(", ")
                for (i in 0 until problemList.size) {
                    for (j in 0 until orderedList.size) {
                        if (problemList[j].id.toString() == orderedList[i]) {
                            resultList.add(problemList[j])
                            break;
                        }
                    }
                }
            }

            return resultList
        }
    }
}