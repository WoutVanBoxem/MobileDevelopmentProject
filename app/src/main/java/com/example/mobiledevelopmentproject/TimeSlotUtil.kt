package com.example.mobiledevelopmentproject

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
}
