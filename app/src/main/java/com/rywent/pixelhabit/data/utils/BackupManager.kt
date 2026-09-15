package com.rywent.pixelhabit.data.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.room.withTransaction
import com.rywent.pixelhabit.data.local.AppDatabase
import com.rywent.pixelhabit.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupManager(
    private val context: Context,
    private val database: AppDatabase
) {

    data class BackupResult(
        val success: Boolean,
        val message: String,
        val fileName: String? = null,
        val filePath: String? = null
    )

    suspend fun exportAllData(): BackupResult = withContext(Dispatchers.IO) {
        try {
            val backupJson = JSONObject().apply {
                put("version", 3)
                put("exportDate", System.currentTimeMillis())
                put("appVersion", getAppVersion())

                val defaultUserId = "default_user"

                // Users
                put("users", createJsonArray(database.userDao().getAllUsers().first()) { user ->
                    JSONObject().apply {
                        put("id", user.id)
                        put("name", user.name)
                        put("currentStreak", user.currentStreak)
                        put("bestStreak", user.bestStreak)
                        put("createdAt", user.createdAt)
                        put("lastActiveAt", user.lastActiveAt)
                        put("updatedAt", user.updatedAt)
                    }
                })

                // Lifestyles
                put("lifestyles", createJsonArray(
                    database.lifestyleDao().getLifestylesByUserId(defaultUserId).first()
                ) { ls ->
                    JSONObject().apply {
                        put("id", ls.id)
                        put("name", ls.name)
                        put("description", ls.description)
                        put("iconPath", ls.iconPath)
                        put("iconColorArgb", ls.iconColorArgb)
                        put("category", ls.category)
                        put("createdDate", ls.createdDate)
                        put("isActive", ls.isActive)
                        put("userId", ls.userId)
                    }
                })

                // Habits
                put("habits", createJsonArray(
                    database.habitDao().getAllHabits(defaultUserId).first()
                ) { habit ->
                    JSONObject().apply {
                        put("id", habit.id)
                        put("name", habit.name)
                        put("description", habit.description)
                        put("iconPath", habit.iconPath)
                        put("timeOfDayIconPath", habit.timeOfDayIconPath)
                        put("lifestyleIconPath", habit.lifestyleIconPath)
                        put("colorArgb", habit.colorArgb)
                        put("lifestyleColorArgb", habit.lifestyleColorArgb)
                        put("frequency", habit.frequency)
                        put("timeOfDay", habit.timeOfDay)
                        put("specificTime", habit.specificTime)
                        put("lifestyleName", habit.lifestyleName)
                        put("customDays", habit.customDays)
                        put("weeklyProgress", habit.weeklyProgress)
                        put("weeklyDone", habit.weeklyDone)
                        put("weeklyGoal", habit.weeklyGoal)
                        put("currentStreak", habit.currentStreak)
                        put("bestStreak", habit.bestStreak)
                        put("createdAt", habit.createdAt)
                        put("updatedAt", habit.updatedAt)
                        put("userId", habit.userId)
                        put("lifestyleId", habit.lifestyleId)
                    }
                })

                // Habit Completions
                put("habitCompletions", createJsonArray(
                    database.habitCompletionDao().getAllCompletions()
                ) { comp ->
                    JSONObject().apply {
                        put("id", comp.id)
                        put("habitId", comp.habitId)
                        put("date", comp.date)
                        put("completed", comp.completed)
                        put("completedAt", comp.completedAt)
                    }
                })

                // Quests
                put("quests", createJsonArray(
                    database.questDao().getAllQuests(defaultUserId).first()
                ) { quest ->
                    JSONObject().apply {
                        put("id", quest.id)
                        put("name", quest.name)
                        put("description", quest.description)
                        put("iconPath", quest.iconPath)
                        put("iconColorArgb", quest.iconColorArgb)
                        put("totalDays", quest.totalDays)
                        put("currentDay", quest.currentDay)
                        put("daysLeft", quest.daysLeft)
                        put("startDate", quest.startDate)
                        put("endDate", quest.endDate)
                        put("isCompleted", quest.isCompleted)
                        put("completionPercent", quest.completionPercent)
                        put("createdAt", quest.createdAt)
                        put("updatedAt", quest.updatedAt)
                        put("userId", quest.userId)
                    }
                })

                // Focus Sessions
                put("focusSessions", createJsonArray(
                    database.focusSessionDao().getAll(defaultUserId).first()
                ) { session ->
                    JSONObject().apply {
                        put("id", session.id)
                        put("userId", session.userId)
                        put("habitId", session.habitId)
                        put("habitName", session.habitName)
                        put("phase", session.phase)
                        put("plannedSeconds", session.plannedSeconds)
                        put("actualSeconds", session.actualSeconds)
                        put("completed", session.completed)
                        put("startedAt", session.startedAt)
                        put("finishedAt", session.finishedAt)
                        put("date", session.date)
                    }
                })

                // Focus Presets
                put("focusPresets", createJsonArray(
                    database.focusPresetDao().getAll(defaultUserId).first()
                ) { preset ->
                    JSONObject().apply {
                        put("id", preset.id)
                        put("userId", preset.userId)
                        put("name", preset.name)
                        put("focusMin", preset.focusMin)
                        put("shortBreakMin", preset.shortBreakMin)
                        put("longBreakMin", preset.longBreakMin)
                        put("longBreakEvery", preset.longBreakEvery)
                        put("stagesJson", preset.stagesJson)
                        put("isDefault", preset.isDefault)
                        put("createdAt", preset.createdAt)
                    }
                })
            }

            val fileName = "pixelhabit_backup_${getCurrentTimestamp()}.json"
            val file = saveToDownloads(backupJson.toString(2), fileName)

            BackupResult(
                success = true,
                message = "Backup successfully saved.",
                fileName = fileName,
                filePath = file?.absolutePath
            )
        } catch (e: Exception) {
            e.printStackTrace()
            BackupResult(false, "Export error: ${e.localizedMessage}")
        }
    }

    suspend fun importFromJson(fileContent: String): BackupResult = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject(fileContent)

            database.withTransaction {
                database.openHelper.writableDatabase.execSQL("DELETE FROM focus_sessions")
                database.openHelper.writableDatabase.execSQL("DELETE FROM focus_presets")

                database.habitCompletionDao().deleteAll()
                database.questDao().deleteAll()
                database.habitDao().deleteAll()
                database.lifestyleDao().deleteAll()
                database.userDao().deleteAll()

                // Users
                json.optJSONArray("users")?.forEachJSONObject { obj ->
                    database.userDao().insertUser(
                        UserEntity(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            currentStreak = obj.optInt("currentStreak", 0),
                            bestStreak = obj.optInt("bestStreak", 0),
                            createdAt = obj.optLong("createdAt"),
                            lastActiveAt = obj.optLong("lastActiveAt"),
                            updatedAt = obj.optLong("updatedAt")
                        )
                    )
                }

                // Lifestyles
                json.optJSONArray("lifestyles")?.forEachJSONObject { obj ->
                    database.lifestyleDao().insertLifestyle(
                        LifestyleEntity(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            iconPath = obj.getString("iconPath"),
                            iconColorArgb = obj.getLong("iconColorArgb"),
                            category = obj.getString("category"),
                            createdDate = obj.optLong("createdDate"),
                            isActive = obj.optBoolean("isActive", true),
                            userId = obj.getString("userId")
                        )
                    )
                }

                // Habits
                json.optJSONArray("habits")?.forEachJSONObject { obj ->
                    database.habitDao().insertHabit(
                        HabitEntity(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            description = obj.optString("description", ""),
                            iconPath = obj.getString("iconPath"),
                            timeOfDayIconPath = obj.getString("timeOfDayIconPath"),
                            lifestyleIconPath = obj.optString("lifestyleIconPath").ifBlank { null },
                            colorArgb = obj.getLong("colorArgb"),
                            lifestyleColorArgb = obj.getLong("lifestyleColorArgb"),
                            frequency = obj.getString("frequency"),
                            timeOfDay = obj.getString("timeOfDay"),
                            specificTime = obj.optString("specificTime").ifBlank { null },
                            lifestyleName = obj.getString("lifestyleName"),
                            customDays = obj.optString("customDays").ifBlank { null },
                            weeklyProgress = obj.optDouble("weeklyProgress", 0.0).toFloat(),
                            weeklyDone = obj.optInt("weeklyDone"),
                            weeklyGoal = obj.optInt("weeklyGoal"),
                            currentStreak = obj.optInt("currentStreak"),
                            bestStreak = obj.optInt("bestStreak"),
                            createdAt = obj.optLong("createdAt"),
                            updatedAt = obj.optLong("updatedAt"),
                            userId = obj.getString("userId"),
                            lifestyleId = obj.optString("lifestyleId").ifBlank { null }
                        )
                    )
                }

                // Habit Completions
                json.optJSONArray("habitCompletions")?.forEachJSONObject { obj ->
                    database.habitCompletionDao().upsertCompletion(
                        HabitCompletionEntity(
                            id = obj.getString("id"),
                            habitId = obj.getString("habitId"),
                            date = obj.getString("date"),
                            completed = obj.getBoolean("completed"),
                            completedAt = if (obj.has("completedAt") && !obj.isNull("completedAt"))
                                obj.getLong("completedAt") else null
                        )
                    )
                }

                // Quests
                json.optJSONArray("quests")?.forEachJSONObject { obj ->
                    database.questDao().insertQuest(
                        QuestEntity(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            description = obj.optString("description", ""),
                            iconPath = obj.getString("iconPath"),
                            iconColorArgb = obj.getLong("iconColorArgb"),
                            totalDays = obj.getInt("totalDays"),
                            currentDay = obj.getInt("currentDay"),
                            daysLeft = obj.getInt("daysLeft"),
                            startDate = obj.getString("startDate"),
                            endDate = obj.getString("endDate"),
                            isCompleted = obj.getBoolean("isCompleted"),
                            completionPercent = obj.optDouble("completionPercent", 0.0).toFloat(),
                            createdAt = obj.optLong("createdAt"),
                            updatedAt = obj.optLong("updatedAt"),
                            userId = obj.getString("userId")
                        )
                    )
                }

                // Focus Sessions
                json.optJSONArray("focusSessions")?.forEachJSONObject { obj ->
                    database.focusSessionDao().insert(
                        FocusSessionEntity(
                            id = obj.getString("id"),
                            userId = obj.getString("userId"),
                            habitId = if (obj.has("habitId") && !obj.isNull("habitId")) obj.getString("habitId") else null,
                            habitName = if (obj.has("habitName") && !obj.isNull("habitName")) obj.getString("habitName") else null,
                            phase = obj.getString("phase"),
                            plannedSeconds = obj.getInt("plannedSeconds"),
                            actualSeconds = obj.getInt("actualSeconds"),
                            completed = obj.getBoolean("completed"),
                            startedAt = obj.getLong("startedAt"),
                            finishedAt = obj.getLong("finishedAt"),
                            date = obj.getString("date")
                        )
                    )
                }

                // Focus Presets
                json.optJSONArray("focusPresets")?.forEachJSONObject { obj ->
                    database.focusPresetDao().insert(
                        FocusPresetEntity(
                            id = obj.getString("id"),
                            userId = obj.getString("userId"),
                            name = obj.getString("name"),
                            focusMin = obj.getInt("focusMin"),
                            shortBreakMin = obj.getInt("shortBreakMin"),
                            longBreakMin = obj.getInt("longBreakMin"),
                            longBreakEvery = obj.optInt("longBreakEvery", 4),
                            stagesJson = if (obj.has("stagesJson") && !obj.isNull("stagesJson")) obj.getString("stagesJson") else null,
                            isDefault = obj.optBoolean("isDefault", false),
                            createdAt = obj.optLong("createdAt")
                        )
                    )
                }
            }

            BackupResult(true, "Data successfully imported! Please restart the application.")
        } catch (e: Exception) {
            e.printStackTrace()
            BackupResult(false, "Import error: ${e.localizedMessage}")
        }
    }

    private inline fun <T> createJsonArray(items: List<T>, transform: (T) -> JSONObject): JSONArray =
        JSONArray().apply { items.forEach { put(transform(it)) } }

    private inline fun JSONArray.forEachJSONObject(action: (JSONObject) -> Unit) {
        for (i in 0 until length()) {
            action(getJSONObject(i))
        }
    }

    private fun saveToDownloads(content: String, fileName: String): File? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return null

            context.contentResolver.openOutputStream(uri)?.use {
                it.write(content.toByteArray(Charsets.UTF_8))
            }

            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        } else {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(dir, fileName)
            file.writeText(content)
            file
        }
    }

    private fun getCurrentTimestamp(): String =
        SimpleDateFormat("yyyyMMdd_HHMMSS", Locale.getDefault()).format(Date())

    private fun getAppVersion(): String = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
    } catch (e: Exception) {
        "1.0"
    }
}