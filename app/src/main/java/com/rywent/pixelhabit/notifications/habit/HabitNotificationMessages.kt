package com.rywent.pixelhabit.notifications.habit

import kotlin.random.Random

object HabitNotificationMessages {

    private val anytimeTitles = listOf(
        "Time to act!",
        "Not done yet today",
        "Come on, you've got this",
        "Don't miss the moment",
        "Your day - your choice",
        "A small victory awaits",
        "A step toward your best self",
        "Time to take action",
        "Don't put it off",
        "Just a few minutes",
        "Your streak is on the line!",
        "You can do it"
    )
    private val anytimeMessages = listOf(
        "It's the perfect time to build your habit!",
        "One small step - a big result",
        "Don't skip today",
        "You're close to your goal, keep going",
        "It'll only take a couple of minutes",
        "Do it now - future you will thank you",
        "Your future self will say thanks",
        "Discipline = freedom",
        "One more day - one more step forward",
        "Just start - the rest will follow",
        "Today is the perfect day",
        "Don't let your streak break"
    )
    private val withTimeTitles = listOf(
        "Don't miss it!",
        "Reminder",
        "Waiting for you",
        "Your turn",
        "Time to take action",
        "The time has come",
        "Remember?",
        "Don't delay",
        "Now or never",
        "Time's up"
    )

    private val withTimeMessages = listOf(
        "You have it scheduled for %s",
        "You wanted to do this at %s",
        "Reminder: you planned this for %s",
        "On schedule - %s",
        "It's time (%s)",
        "Your habit is waiting at %s",
        "Don't forget about %s",
        "At %s - your hour",
        "Was planned for %s",
        "According to schedule - %s"
    )

    private val motivationalAddons = listOf(
        " 🔥", " 💪", " ⭐", " 🎯", " ✨", " 🌟", " 🏆", " 💯", " 🚀"
    )

    fun getAnytimeNotification(habitName: String): Pair<String, String> {
        val title = anytimeTitles.random()
        val message = anytimeMessages.random()

        val finalTitle = when (Random.nextInt(100)) {
            in 0..30 -> "$habitName — $title"
            in 31..50 -> habitName
            else -> title
        }

        val finalMessage = if (Random.nextInt(100) < 40) {
            "$message${motivationalAddons.random()}"
        } else {
            message
        }

        return finalTitle to finalMessage
    }

    fun getWithTimeNotification(habitName: String, scheduledTime: String): Pair<String, String> {
        val title = withTimeTitles.random()
        val message = withTimeMessages.random()

        val formattedMessage = message.format(scheduledTime)

        val finalTitle = when (Random.nextInt(100)) {
            in 0..25 -> habitName
            in 26..50 -> "$title: $habitName"
            in 51..75 -> habitName
            else -> title
        }

        val finalMessage = if (Random.nextInt(100) < 30) {
            "$formattedMessage${motivationalAddons.random()}"
        } else {
            formattedMessage
        }

        return finalTitle to finalMessage
    }

    fun getRandomNotification(habitName: String, scheduledTime: String? = null): Pair<String, String> {
        return if (!scheduledTime.isNullOrEmpty()) {
            getWithTimeNotification(habitName, scheduledTime)
        } else {
            getAnytimeNotification(habitName)
        }
    }
}