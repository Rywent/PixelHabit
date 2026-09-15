package com.rywent.pixelhabit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rywent.pixelhabit.data.local.dao.FocusPresetDao
import com.rywent.pixelhabit.data.local.dao.FocusSessionDao
import com.rywent.pixelhabit.data.local.dao.HabitCompletionDao
import com.rywent.pixelhabit.data.local.dao.HabitDao
import com.rywent.pixelhabit.data.local.dao.LifestyleDao
import com.rywent.pixelhabit.data.local.dao.QuestDao
import com.rywent.pixelhabit.data.local.dao.UserDao
import com.rywent.pixelhabit.data.local.entity.FocusPresetEntity
import com.rywent.pixelhabit.data.local.entity.FocusSessionEntity
import com.rywent.pixelhabit.data.local.entity.HabitCompletionEntity
import com.rywent.pixelhabit.data.local.entity.HabitEntity
import com.rywent.pixelhabit.data.local.entity.LifestyleEntity
import com.rywent.pixelhabit.data.local.entity.QuestEntity
import com.rywent.pixelhabit.data.local.entity.UserEntity


@Database(
    entities = [
        UserEntity::class,
        HabitEntity::class,
        LifestyleEntity::class,
        HabitCompletionEntity::class,
        QuestEntity::class,
        FocusSessionEntity::class,
        FocusPresetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun habitDao(): HabitDao
    abstract fun lifestyleDao(): LifestyleDao
    abstract fun habitCompletionDao(): HabitCompletionDao

    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun focusPresetDao(): FocusPresetDao

    abstract fun questDao(): QuestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun  getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pixelhabit_db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}