package com.alexgrig.education.problemsolver.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexgrig.education.problemsolver.utils.StateOfProblem
import java.io.Serializable
import java.text.DateFormat
import java.util.*

@Entity
data class Problem(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var state: StateOfProblem = StateOfProblem.Waiting,
    var suspect: String = "",
    var phoneOfSuspect: String = ""
) : Serializable {

    val photoFileName
        get() = "IMG_$id.jpg"

    fun getSimpleDate(): String = DateFormat.getDateInstance(DateFormat.LONG).format(date)



    override fun toString(): String {
        return "Problem(id = ${id})"
    }
}
