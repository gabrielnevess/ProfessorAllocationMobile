package com.example.professorallocationmobile.repository

import com.example.professorallocationmobile.model.Allocation
import com.example.professorallocationmobile.model.AllocationItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AllocationRepository {
    @GET("/allocations")
    suspend fun getAllAllocations(): Response<List<AllocationItem>>

    @GET("/allocations/{allocation_id}")
    suspend fun getByAllocationId(@Path("allocation_id") allocationId: Int): AllocationItem

    @POST("/allocations")
    suspend fun saveAllocation(@Body allocation: Allocation)

    @PUT("/allocations/{allocation_id}")
    suspend fun updateAllocation(
        @Path("allocation_id") allocationId: Int,
        @Body allocation: Allocation
    )

    @DELETE("/allocations/{allocation_id}")
    suspend fun deleteAllocation(
        @Path("allocation_id") allocationId: Int
        )
}