package com.example.professorallocationmobile.ui.course

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.professorallocationmobile.R
import com.example.professorallocationmobile.databinding.FragmentCourseFormBinding
import com.example.professorallocationmobile.model.Course
import com.example.professorallocationmobile.utils.Result
import com.example.professorallocationmobile.utils.SnackbarUtils.Companion.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourseFormFragment : Fragment() {

    private var _binding: FragmentCourseFormBinding? = null
    private val binding get() = _binding!!
    private val courseViewModel: CourseViewModel by viewModels()

    private var courseId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseFormBinding.inflate(inflater, container, false)
        val root: View = binding.root

        courseId = arguments?.getInt("courseId")
        (activity as? AppCompatActivity)?.supportActionBar?.title =
            if (courseId != null) getString(R.string.update_course) else getString(R.string.save_course)

        setupUI()
        setupObservers()

        return root
    }

    private fun setupUI() {
        binding.buttonSaveCourse.setOnClickListener {
            closeKeyboard()
            val courseName = binding.editTextCourseName.text.toString()
            if (courseName.isBlank()) {
                showSnackbar(binding.root, getString(R.string.course_name_required))
            } else {
                handleSaveCourse(Course(name = courseName))
            }
        }

        courseId?.let {
            courseViewModel.getByIdCourse(courseId!!) { result ->
                when (result) {
                    is Result.Success -> {}
                    is Result.Error -> showSnackbar(
                        binding.root,
                        getString(R.string.error_loading_course)
                    )
                }
            }
        }
    }

    private fun handleSaveCourse(course: Course) {
        toggleUIElements(false)
        if (courseId != null) {
            courseViewModel.updateCourse(courseId!!, course)
        } else {
            courseViewModel.saveCourse(course)
        }
    }

    private fun setupObservers() {
        courseViewModel.course.observe(viewLifecycleOwner) { course ->
            binding.editTextCourseName.setText(course?.name)
        }

        courseViewModel.saveCourseResult.observe(viewLifecycleOwner) { result ->
            val message =
                if (courseId != null) getString(R.string.course_updated) else getString(R.string.course_saved)
            when (result) {
                is Result.Success -> {
                    showSnackbar(binding.root, message)
                    findNavController().navigateUp()
                }

                is Result.Error -> {
                    showSnackbar(binding.root, getString(R.string.error_saving_course))
                    toggleUIElements(true)
                }
            }
        }
    }

    private fun toggleUIElements(enable: Boolean) {
        binding.buttonSaveCourse.isEnabled = enable
        binding.editTextCourseName.isEnabled = enable
    }

    private fun closeKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
