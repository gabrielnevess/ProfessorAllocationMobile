package com.example.professorallocationmobile.ui.allocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.professorallocationmobile.model.Allocation
import com.example.professorallocationmobile.model.AllocationItem
import com.example.professorallocationmobile.model.Course
import com.example.professorallocationmobile.model.ProfessorItem
import com.example.professorallocationmobile.repository.AllocationRepository
import com.example.professorallocationmobile.repository.CourseRepository
import com.example.professorallocationmobile.repository.ProfessorRepository
import com.example.professorallocationmobile.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class AllocationViewModel @Inject constructor(
    private val allocationRepository: AllocationRepository,
    private val courseRepository: CourseRepository,
    private val professorRepository: ProfessorRepository
) : ViewModel() {

    private val _allocations = MutableLiveData<List<AllocationItem>>()
    val allocations: LiveData<List<AllocationItem>> get() = _allocations

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _allocation = MutableLiveData<AllocationItem?>()
    val allocation: LiveData<AllocationItem?> get() = _allocation

    private val _saveAllocationResult = MutableLiveData<Result<Unit>>()
    val saveAllocationResult: LiveData<Result<Unit>> get() = _saveAllocationResult

    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> get() = _courses

    private val _professors = MutableLiveData<List<ProfessorItem>>()
    val professors: LiveData<List<ProfessorItem>> get() = _professors

    private val _daysOfWeek = MutableLiveData<List<DayOfWeek>>()
    val daysOfWeek: LiveData<List<DayOfWeek>> get() = _daysOfWeek

    fun getAllDaysOfWeek() {
        _daysOfWeek.value = DayOfWeek.entries
    }

    fun getAllAllocations() {
        _isLoading.value = true
        viewModelScope.launch {
            try {

                val response = allocationRepository.getAllAllocations()
                if (response.isSuccessful && response.body() != null) {
                    _allocations.value = response.body()
                }

            } catch (e: Exception) {
                _allocations.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getByIdAllocation(allocationId: Int, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                _allocation.value = allocationRepository.getByAllocationId(allocationId)
                callback(Result.Success(Unit))
            } catch (e: Exception) {
                _allocation.value = null
                callback(Result.Error(e))
            }
        }
    }

    fun saveAllocation(allocation: Allocation) {
        viewModelScope.launch {
            try {
                allocationRepository.saveAllocation(allocation)
                _saveAllocationResult.value = Result.Success(Unit)
            } catch (e: Exception) {
                _saveAllocationResult.value = Result.Error(e)
            }
        }
    }

    fun updateAllocation(allocationId: Int, allocation: Allocation) {
        viewModelScope.launch {
            try {
                allocationRepository.updateAllocation(allocationId, allocation)
                _saveAllocationResult.value = Result.Success(Unit)
            } catch (e: Exception) {
                _saveAllocationResult.value = Result.Error(e)
            }
        }
    }

    fun deleteAllocation(allocationId: Int, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                allocationRepository.deleteAllocation(allocationId)
                callback(Result.Success(Unit))
            } catch (e: Exception) {
                callback(Result.Error(e))
            }
        }
    }

    fun getAllCourses() {
        viewModelScope.launch {
            try {

                val response = courseRepository.getAllCourses()
                if (response.isSuccessful && response.body() != null) {
                    _courses.value = response.body()
                }

            } catch (e: Exception) {
                _courses.value = emptyList()
            }
        }
    }

    fun getAllProfessors() {
        viewModelScope.launch {
            try {

                val response = professorRepository.getAllProfessors()
                if (response.isSuccessful && response.body() != null) {
                    _professors.value = response.body()
                }

            } catch (e: Exception) {
                _professors.value = emptyList()
            }
        }
    }
}