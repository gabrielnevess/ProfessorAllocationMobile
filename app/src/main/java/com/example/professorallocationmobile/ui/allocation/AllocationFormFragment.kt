package com.example.professorallocationmobile.ui.allocation

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.professorallocationmobile.R
import com.example.professorallocationmobile.databinding.FragmentAllocationFormBinding
import com.example.professorallocationmobile.model.Allocation
import com.example.professorallocationmobile.utils.DateFormatUtil
import com.example.professorallocationmobile.utils.Result
import com.example.professorallocationmobile.utils.SnackbarUtils.Companion.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.Calendar
import java.util.Locale
import java.util.Objects

@AndroidEntryPoint
class AllocationFormFragment : Fragment() {

    private var _binding: FragmentAllocationFormBinding? = null
    private val binding get() = _binding!!
    private val allocationViewModel: AllocationViewModel by viewModels()

    private var allocationId: Int? = null
    private var selectedCourseId: Int? = null
    private var selectedProfessorId: Int? = null
    private var selectedDayOfWeek: DayOfWeek? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllocationFormBinding.inflate(inflater, container, false)
        val root: View = binding.root

        allocationId = arguments?.getInt("allocationId")
        (activity as? AppCompatActivity)?.supportActionBar?.title =
            if (allocationId != null) getString(R.string.update_allocation) else getString(R.string.save_allocation)

        setupUI()
        setupObservers()

        return root
    }

    private fun setupUI() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        binding.editTextStartHour.setOnClickListener {
            val cal = Calendar.getInstance()

            TimePickerDialog(
                context, { _, hour, minute ->
                    cal.apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                    }
                    binding.editTextStartHour.setText(timeFormat.format(cal.time))
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
            ).show()
        }

        binding.editTextEndHour.setOnClickListener {
            val cal = Calendar.getInstance()

            TimePickerDialog(
                context, { _, hour, minute ->
                    cal.apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                    }
                    binding.editTextEndHour.setText(timeFormat.format(cal.time))
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
            ).show()
        }

        binding.buttonSaveAllocation.setOnClickListener {
            closeKeyboard()

            val dayOfWeek = selectedDayOfWeek
            val startHour = binding.editTextStartHour.text.toString()
            val endHour = binding.editTextEndHour.text.toString()
            val courseId = selectedCourseId
            val professorId = selectedProfessorId

            if (Objects.isNull(dayOfWeek)) {
                showSnackbar(binding.root, getString(R.string.allocation_day_of_week_required))
            } else if (startHour.isBlank()) {
                showSnackbar(binding.root, getString(R.string.allocation_start_hour_required))
            } else if (endHour.isBlank()) {
                showSnackbar(binding.root, getString(R.string.allocation_end_hour_required))
            } else if (Objects.isNull(professorId)) {
                showSnackbar(binding.root, getString(R.string.allocation_professor_required))
            } else if (Objects.isNull(courseId)) {
                showSnackbar(binding.root, getString(R.string.allocation_course_required))
            } else {
                handleSaveAllocation(
                    Allocation(
                        dayOfWeek = dayOfWeek!!,
                        startHour = Time.valueOf(DateFormatUtil.formatTime(startHour)),
                        endHour = Time.valueOf(DateFormatUtil.formatTime(endHour)),
                        professorId = professorId!!,
                        courseId = courseId!!
                    )
                )
            }
        }

        allocationId?.let {
            allocationViewModel.getByIdAllocation(allocationId!!) { result ->
                when (result) {
                    is Result.Success -> {}
                    is Result.Error -> showSnackbar(
                        binding.root,
                        getString(R.string.error_loading_allocation)
                    )
                }
            }
        }
    }

    private fun handleSaveAllocation(allocation: Allocation) {
        toggleUIElements(false)
        if (allocationId != null) {
            allocationViewModel.updateAllocation(allocationId!!, allocation)
        } else {
            allocationViewModel.saveAllocation(allocation)
        }
    }

    private fun setupObservers() {
        allocationViewModel.allocation.observe(viewLifecycleOwner) { allocation ->
            allocation?.dayOfWeek?.let {
                binding.autoCompleteDayOfWeekTextView.setText(it.toString(), false)
                selectedDayOfWeek = DayOfWeek.valueOf(it.toString())
            }
            binding.editTextStartHour.setText(DateFormatUtil.formatTime(allocation?.startHour.toString()))
            binding.editTextEndHour.setText(DateFormatUtil.formatTime(allocation?.endHour.toString()))
            allocation?.course?.name?.let {
                binding.autoCompleteCourseTextView.setText(it, false)
                selectedCourseId = allocation.course.id
            }
            allocation?.professor?.name?.let {
                binding.autoCompleteProfessorTextView.setText(it, false)
                selectedProfessorId = allocation.professor.id
            }
        }

        allocationViewModel.getAllDaysOfWeek()
        allocationViewModel.daysOfWeek.observe(viewLifecycleOwner) { daysOfWeek ->
            val dayNames = daysOfWeek.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                dayNames
            )
            binding.autoCompleteDayOfWeekTextView.setAdapter(adapter)
            binding.autoCompleteDayOfWeekTextView.setOnItemClickListener { _, _, position, _ ->
                selectedDayOfWeek = daysOfWeek[position]
            }
        }

        allocationViewModel.getAllCourses()
        allocationViewModel.courses.observe(viewLifecycleOwner) { courses ->
            val courseNames = courses.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                courseNames
            )
            binding.autoCompleteCourseTextView.setAdapter(adapter)
            binding.autoCompleteCourseTextView.setOnItemClickListener { _, _, position, _ ->
                selectedCourseId = courses[position].id
            }
        }

        allocationViewModel.getAllProfessors()
        allocationViewModel.professors.observe(viewLifecycleOwner) { professors ->
            val professorNames = professors.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                professorNames
            )
            binding.autoCompleteProfessorTextView.setAdapter(adapter)
            binding.autoCompleteProfessorTextView.setOnItemClickListener { _, _, position, _ ->
                selectedProfessorId = professors[position].id
            }
        }

        allocationViewModel.saveAllocationResult.observe(viewLifecycleOwner) { result ->
            val message =
                if (allocationId != null) getString(R.string.allocation_updated) else getString(R.string.allocation_saved)
            when (result) {
                is Result.Success -> {
                    showSnackbar(binding.root, message)
                    findNavController().navigateUp()
                }

                is Result.Error -> {
                    showSnackbar(binding.root, getString(R.string.error_saving_allocation))
                    toggleUIElements(true)
                }
            }
        }
    }

    private fun toggleUIElements(enable: Boolean) {
        binding.buttonSaveAllocation.isEnabled = enable
        binding.editTextStartHour.isEnabled = enable
        binding.editTextEndHour.isEnabled = enable
        binding.autoCompleteDayOfWeekTextView.isEnabled = enable
        binding.autoCompleteCourseTextView.isEnabled = enable
        binding.autoCompleteProfessorTextView.isEnabled = enable
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
