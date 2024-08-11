package com.example.professorallocationmobile.model

import java.sql.Time
import java.time.DayOfWeek

data class Allocation(
    val id: Int? = null,
    val dayOfWeek: DayOfWeek,
    val startHour: Time,
    val endHour: Time,
    val professorId: Int,
    val courseId: Int
)