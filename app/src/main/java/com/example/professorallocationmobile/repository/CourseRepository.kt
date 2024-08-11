package com.example.professorallocationmobile.repository

import com.example.professorallocationmobile.model.Course
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CourseRepository {
    @GET("/courses")
    suspend fun getAllCourses(): Response<List<Course>>

    @GET("/courses/{course_id}")
    suspend fun getByCourseId(@Path("course_id") courseId: Int): Course

    @POST("/courses")
    suspend fun saveCourse(@Body course: Course)

    @PUT("/courses/{course_id}")
    suspend fun updateCourse(
        @Path("course_id") courseId: Int,
        @Body course: Course
    )

    @DELETE("/courses/{course_id}")
    suspend fun deleteCourse(
        @Path("course_id") courseId: Int
        )
}