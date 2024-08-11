package com.example.professorallocationmobile.ui.professor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.professorallocationmobile.model.Department
import com.example.professorallocationmobile.model.Professor
import com.example.professorallocationmobile.model.ProfessorItem
import com.example.professorallocationmobile.repository.DepartmentRepository
import com.example.professorallocationmobile.repository.ProfessorRepository
import com.example.professorallocationmobile.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfessorViewModel @Inject constructor(
    private val professorRepository: ProfessorRepository,
    private val departmentRepository: DepartmentRepository
) : ViewModel() {

    private val _professors = MutableLiveData<List<ProfessorItem>>()
    val professors: LiveData<List<ProfessorItem>> get() = _professors

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _professor = MutableLiveData<ProfessorItem?>()
    val professor: LiveData<ProfessorItem?> get() = _professor

    private val _saveProfessorResult = MutableLiveData<Result<Unit>>()
    val saveProfessorResult: LiveData<Result<Unit>> get() = _saveProfessorResult

    private val _departments = MutableLiveData<List<Department>>()
    val departments: LiveData<List<Department>> get() = _departments

    fun getAllProfessors() {
        _isLoading.value = true
        viewModelScope.launch {
            try {

                val response = professorRepository.getAllProfessors()
                if (response.isSuccessful && response.body() != null) {
                    _professors.value = response.body()
                }

            } catch (e: Exception) {
                _professors.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getByIdProfessor(professorId: Int, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                _professor.value = professorRepository.getByProfessorId(professorId)
                callback(Result.Success(Unit))
            } catch (e: Exception) {
                _professor.value = null
                callback(Result.Error(e))
            }
        }
    }

    fun saveProfessor(professor: Professor) {
        viewModelScope.launch {
            try {
                professorRepository.saveProfessor(professor)
                _saveProfessorResult.value = Result.Success(Unit)
            } catch (e: Exception) {
                _saveProfessorResult.value = Result.Error(e)
            }
        }
    }

    fun updateProfessor(professorId: Int, professor: Professor) {
        viewModelScope.launch {
            try {
                professorRepository.updateProfessor(professorId, professor)
                _saveProfessorResult.value = Result.Success(Unit)
            } catch (e: Exception) {
                _saveProfessorResult.value = Result.Error(e)
            }
        }
    }

    fun deleteProfessor(professorId: Int, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                professorRepository.deleteProfessor(professorId)
                callback(Result.Success(Unit))
            } catch (e: Exception) {
                callback(Result.Error(e))
            }
        }
    }

    fun getAllDepartments() {
        viewModelScope.launch {
            try {

                val response = departmentRepository.getAllDepartments()
                if (response.isSuccessful && response.body() != null) {
                    _departments.value = response.body()
                }

            } catch (e: Exception) {
                _departments.value = emptyList()
            }
        }
    }
}