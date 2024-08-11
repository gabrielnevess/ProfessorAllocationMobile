package com.example.professorallocationmobile.config

import com.example.professorallocationmobile.repository.AllocationRepository
import com.example.professorallocationmobile.repository.CourseRepository
import com.example.professorallocationmobile.repository.DepartmentRepository
import com.example.professorallocationmobile.repository.ProfessorRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Time
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitClient {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Time::class.java, SqlTimeDeserializer())
            .registerTypeAdapter(Time::class.java, SqlTimeSerializer())
            .setDateFormat("HH:mm:ss")
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.0.105:8080")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideAllocationApi(retrofit: Retrofit): AllocationRepository {
        return retrofit.create(AllocationRepository::class.java)
    }

    @Provides
    fun provideCourseApi(retrofit: Retrofit): CourseRepository {
        return retrofit.create(CourseRepository::class.java)
    }

    @Provides
    fun provideDepartmentApi(retrofit: Retrofit): DepartmentRepository {
        return retrofit.create(DepartmentRepository::class.java)
    }

    @Provides
    fun provideProfessorApi(retrofit: Retrofit): ProfessorRepository {
        return retrofit.create(ProfessorRepository::class.java)
        }
}