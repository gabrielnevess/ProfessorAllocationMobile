package com.example.professorallocationmobile.ui.department

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.professorallocationmobile.model.Department
import com.example.professorallocationmobile.repository.DepartmentRepository
import com.example.professorallocationmobile.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DepartmentViewModel @Inject constructor(
    private val departmentRepository: DepartmentRepository
) : ViewModel() {

    private val _departments = MutableLiveData<List<Department>>()
    val departments: LiveData<List<Department>> get() = _departments

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _department = MutableLiveData<Department?>()
    val department: LiveData<Department?> get() = _department

    private val _saveDepartmentResult = MutableLiveData<Result<Unit>>()
    val saveDepartmentResult: LiveData<Result<Unit>> get() = _saveDepartmentResult

    fun getAllDepartments() {
        _isLoading.value = true
        viewModelScope.launch {
            try {

                val response = departmentRepository.getAllDepartments()
                if (response.isSuccessful && response.body() != null) {
                    _departments.value = response.body()
                }

            } catch (e: Exception) {
                _departments.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getByIdDepartment(departmentId: Int, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                _department.value = departmentRepository.getByDepartmentId(departmentId)
                callback(Result.Success(Unit))
            } catch (e: Exception) {
                _department.value = null
                callback(Result.Error(e))
            }
        }
    }

    fun saveDepartment(department: Department) {
        viewModelScope.launch {
            try {
                departmentRepository.saveDepartment(department)
                _saveDepartmentResult.value = Result.Success(Unit)
            } catch (e: Exception) {
                _saveDepartmentResult.value = Result.Error(e)
            }
        }
    }

    fun updateDepartment(departmentId: Int, department: Department) {
        viewModelScope.launch {
            try {
                departmentRepository.updateDepartment(departmentId, department)
                _saveDepartmentResult.value = Result.Success(Unit)
            } catch (e: Exception) {
                _saveDepartmentResult.value = Result.Error(e)
            }
        }
    }

    fun deleteDepartment(departmentId: Int, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                departmentRepository.deleteDepartment(departmentId)
                callback(Result.Success(Unit))
            } catch (e: Exception) {
                callback(Result.Error(e))
            }
        }
    }
}