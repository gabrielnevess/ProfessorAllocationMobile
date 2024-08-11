package com.example.professorallocationmobile.ui.department

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
import com.example.professorallocationmobile.databinding.FragmentDepartmentFormBinding
import com.example.professorallocationmobile.model.Department
import com.example.professorallocationmobile.utils.Result
import com.example.professorallocationmobile.utils.SnackbarUtils.Companion.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DepartmentFormFragment : Fragment() {

    private var _binding: FragmentDepartmentFormBinding? = null
    private val binding get() = _binding!!
    private val departmentViewModel: DepartmentViewModel by viewModels()

    private var departmentId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDepartmentFormBinding.inflate(inflater, container, false)
        val root: View = binding.root

        departmentId = arguments?.getInt("departmentId")
        (activity as? AppCompatActivity)?.supportActionBar?.title =
            if (departmentId != null) getString(R.string.update_department) else getString(R.string.save_department)

        setupUI()
        setupObservers()

        return root
    }

    private fun setupUI() {
        binding.buttonSaveDepartment.setOnClickListener {
            closeKeyboard()
            val departmentName = binding.editTextDepartmentName.text.toString()
            if (departmentName.isBlank()) {
                showSnackbar(binding.root, getString(R.string.department_name_required))
            } else {
                handleSaveDepartment(Department(name = departmentName))
            }
        }

        departmentId?.let {
            departmentViewModel.getByIdDepartment(departmentId!!) { result ->
                when (result) {
                    is Result.Success -> {}
                    is Result.Error -> showSnackbar(
                        binding.root,
                        getString(R.string.error_loading_department)
                    )
                }
            }
        }
    }

    private fun handleSaveDepartment(department: Department) {
        toggleUIElements(false)
        if (departmentId != null) {
            departmentViewModel.updateDepartment(departmentId!!, department)
        } else {
            departmentViewModel.saveDepartment(department)
        }
    }

    private fun setupObservers() {
        departmentViewModel.department.observe(viewLifecycleOwner) { department ->
            binding.editTextDepartmentName.setText(department?.name)
        }

        departmentViewModel.saveDepartmentResult.observe(viewLifecycleOwner) { result ->
            val message =
                if (departmentId != null) getString(R.string.department_updated) else getString(R.string.department_saved)
            when (result) {
                is Result.Success -> {
                    showSnackbar(binding.root, message)
                    findNavController().navigateUp()
                }

                is Result.Error -> {
                    showSnackbar(binding.root, getString(R.string.error_saving_department))
                    toggleUIElements(true)
                }
            }
        }
    }

    private fun toggleUIElements(enable: Boolean) {
        binding.buttonSaveDepartment.isEnabled = enable
        binding.editTextDepartmentName.isEnabled = enable
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
