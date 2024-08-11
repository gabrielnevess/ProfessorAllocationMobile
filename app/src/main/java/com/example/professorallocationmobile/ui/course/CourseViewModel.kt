package com.example.professorallocationmobile.ui.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.professorallocationmobile.model.Course
import com.example.professorallocationmobile.repository.CourseRepository
import com.example.professorallocationmobile.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> get() = _courses

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _course = MutableLiveData<Course?>()
    val course: LiveData<Course?> get() = _course

    private val _saveCourseResult = MutableLiveData<Result<Unit>>()
    val saveCourseResult: LiveData<Result<Unit>> get() = _saveCourseResult

    fun getAllCourses() {
        _isLoading.value = true
        viewModelScope.launch {
            try {

                val response = courseRepository.getAllCourses()
                if (response.isSuccessful && response.body() != null) {
                    _courses.value = response.body()
                }

            } catch (e: Exception) {
                _courses.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getByIdCourse(courseId: Int, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                _course.value = courseRepository.getByCourseId(courseId)
                callback(Result.Success(Unit))
            } catch (e: Exception) {
                _course.value = null
                callback(Result.Error(e))
            }
        }
    }

    fun saveCourse(course: Course) {
        viewModelScope.launch {
            try {
                courseRepository.saveCourse(course)
                _saveCourseResult.value = Result.Success(Unit)
            } catch (e: Exception) {
                _saveCourseResult.value = Result.Error(e)
            }
        }
    }

    fun updateCourse(courseId: Int, course: Course) {
        viewModelScope.launch {
            try {
                courseRepository.updateCourse(courseId, course)
                _saveCourseResult.value = Result.Success(Unit)
            } catch (e: Exception) {
                _saveCourseResult.value = Result.Error(e)
            }
        }
    }

    fun deleteCourse(courseId: Int, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                courseRepository.deleteCourse(courseId)
                callback(Result.Success(Unit))
            } catch (e: Exception) {
                callback(Result.Error(e))
            }
        }
    }
}