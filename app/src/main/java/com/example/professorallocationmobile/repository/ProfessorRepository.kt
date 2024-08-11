package com.example.professorallocationmobile.repository

import com.example.professorallocationmobile.model.Professor
import com.example.professorallocationmobile.model.ProfessorItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfessorRepository {
    @GET("/professors")
    suspend fun getAllProfessors(): Response<List<ProfessorItem>>

    @GET("/professors/{professor_id}")
    suspend fun getByProfessorId(@Path("professor_id") professorId: Int): ProfessorItem

    @POST("/professors")
    suspend fun saveProfessor(@Body professor: Professor)

    @PUT("/professors/{professor_id}")
    suspend fun updateProfessor(
        @Path("professor_id") professorId: Int,
        @Body professor: Professor
    )

    @DELETE("/professors/{professor_id}")
    suspend fun deleteProfessor(
        @Path("professor_id") professorId: Int
        )
}