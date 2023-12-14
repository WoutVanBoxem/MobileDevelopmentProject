package com.example.mobiledevelopmentproject

import java.util.Calendar

object TimeSlotUtil {
    val timeSlots = mapOf(
        0 to "07:30 - 09:00",
        1 to "09:00 - 10:30",
        2 to "10:30 - 12:00",
        3 to "12:00 - 13:30",
        4 to "13:30 - 15:00",
        5 to "15:00 - 16:30",
        6 to "16:30 - 18:00",
        7 to "18:00 - 19:30",
        8 to "19:30 - 21:00",
        9 to "21:00 - 22:30",
        10 to "22:30 - 00:00"
    )


    fun getTimeSlotFromId(timeSlotId: Int): String {
        return timeSlots[timeSlotId] ?: "Onbekend tijdslot"
    }

    fun getCurrentTimeSlotId(): Int {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)
        val currentTotalMinutes = currentHour * 60 + currentMinute

        return when {
            currentTotalMinutes in 450 until 540 -> 0 // 07:30 - 09:00
            currentTotalMinutes in 540 until 630 -> 1 // 09:00 - 10:30
            currentTotalMinutes in 630 until 720 -> 2 // 10:30 - 12:00
            currentTotalMinutes in 720 until 810 -> 3 // 12:00 - 13:30
            currentTotalMinutes in 810 until 900 -> 4 // 13:30 - 15:00
            currentTotalMinutes in 900 until 990 -> 5 // 15:00 - 16:30
            currentTotalMinutes in 990 until 1080 -> 6 // 16:30 - 18:00
            currentTotalMinutes in 1080 until 1170 -> 7 // 18:00 - 19:30
            currentTotalMinutes in 1170 until 1260 -> 8 // 19:30 - 21:00
            currentTotalMinutes in 1260 until 1350 -> 9 // 21:00 - 22:30
            currentTotalMinutes >= 1350 || currentTotalMinutes < 450 -> 10 // 22:30 - 07:30
            else -> -1
        }
    }
}
