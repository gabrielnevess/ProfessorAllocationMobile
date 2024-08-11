package com.example.professorallocationmobile.repository

import com.example.professorallocationmobile.model.Department
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DepartmentRepository {
    @GET("/departments")
    suspend fun getAllDepartments(): Response<List<Department>>

    @GET("/departments/{department_id}")
    suspend fun getByDepartmentId(@Path("department_id") departmentId: Int): Department

    @POST("/departments")
    suspend fun saveDepartment(@Body department: Department)

    @PUT("/departments/{department_id}")
    suspend fun updateDepartment(
        @Path("department_id") departmentId: Int,
        @Body department: Department
    )

    @DELETE("/departments/{department_id}")
    suspend fun deleteDepartment(
        @Path("department_id") departmentId: Int
        )
}