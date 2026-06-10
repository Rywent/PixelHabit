package com.rywent.pixelhabit.notifications.motivation


object MotivationNotificationMessages {

    data class MotivationMessage(
        val title: String,
        val message: String
    )

    // Morning 8 a.m. 10 a.m.
    private val morningMessages = listOf(
        MotivationMessage("Good morning", "A small habit today leads to big results tomorrow."),
        MotivationMessage("New day", "Your habits are waiting. Start with one small step."),
        MotivationMessage("Morning", "Consistency beats intensity. Show up today."),
        MotivationMessage("Rise", "Don't skip today. Your future self will thank you."),
        MotivationMessage("Start", "The hardest part is beginning. You've got this."),
        MotivationMessage("Focus", "One habit. One day. One step closer."),
        MotivationMessage("Go", "Discipline is choosing what you want most."),
        MotivationMessage("Morning", "Build the life you want, one habit at a time."),
        MotivationMessage("Wake up", "Small progress is still progress."),
        MotivationMessage("Today", "You don't have to be perfect. Just be consistent.")
    )

    // Day 12 p.m 14p.m.
    private val afternoonMessages = listOf(
        MotivationMessage("Half day", "How are your habits going? Still time to complete them."),
        MotivationMessage("Check-in", "A quick reminder about your habit goals today."),
        MotivationMessage("Afternoon", "Don't let the midday slump win. Take action now."),
        MotivationMessage("Still time", "It's not too late to build your streak today."),
        MotivationMessage("Keep going", "Small actions add up. Keep moving forward."),
        MotivationMessage("Reminder", "You set a goal. You can still achieve it today."),
        MotivationMessage("Push", "One habit today is better than none."),
        MotivationMessage("Afternoon", "Future you is counting on present you."),
        MotivationMessage("Momentum", "Don't break the chain. Complete your habit."),
        MotivationMessage("Progress", "Every completed habit is a vote for who you want to be.")
    )

    // Evening 6 p.m 8 p.m
    private val eveningMessages = listOf(
        MotivationMessage("Evening", "Last chance to complete your habit today."),
        MotivationMessage("Day's end", "Don't go to bed with unfinished habits."),
        MotivationMessage("Final hours", "Just a few minutes. You can do this."),
        MotivationMessage("Night", "Tomorrow you'll be glad you did it today."),
        MotivationMessage("Finish", "End the day strong. Complete your habit now."),
        MotivationMessage("Evening", "One small task before the day ends."),
        MotivationMessage("Last call", "Your habit is waiting. Don't leave it for tomorrow."),
        MotivationMessage("Night", "Consistency is key. Don't skip today."),
        MotivationMessage("Before bed", "A quick habit to close the day right."),
        MotivationMessage("Evening reminder", "You're almost there. Just do it.")
    )

    fun getMorningMessage(): MotivationMessage = morningMessages.random()
    fun getAfternoonMessage(): MotivationMessage = afternoonMessages.random()
    fun getEveningMessage(): MotivationMessage = eveningMessages.random()

    fun getByTimeOfDay(timeOfDay: String): MotivationMessage {
        return when (timeOfDay.lowercase()) {
            "morning" -> getMorningMessage()
            "afternoon" -> getAfternoonMessage()
            "evening" -> getEveningMessage()
            else -> morningMessages.random()
        }
    }
}