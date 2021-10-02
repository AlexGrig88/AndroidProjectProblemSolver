package com.alexgrig.education.problemsolver.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alexgrig.education.problemsolver.entities.Problem

@Database(entities = arrayOf(Problem::class), version = 2, exportSchema = false)
@TypeConverters(ProblemTypeConverters::class)
abstract class ProblemDatabase : RoomDatabase() {

    abstract fun problemDao(): ProblemDao
}
