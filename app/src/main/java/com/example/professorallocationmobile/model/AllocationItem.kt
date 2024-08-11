package com.example.professorallocationmobile.model

import java.sql.Time
import java.time.DayOfWeek

data class AllocationItem(
    val id: Int,
    val dayOfWeek: DayOfWeek,
    val startHour: Time,
    val endHour: Time,
    val professor: ProfessorItem,
    val course: Course
)