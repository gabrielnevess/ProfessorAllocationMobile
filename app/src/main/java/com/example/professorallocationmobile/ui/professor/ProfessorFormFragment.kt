package com.example.professorallocationmobile.ui.professor

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
import com.example.professorallocationmobile.databinding.FragmentProfessorFormBinding
import com.example.professorallocationmobile.model.Professor
import com.example.professorallocationmobile.utils.Result
import com.example.professorallocationmobile.utils.SnackbarUtils.Companion.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

@AndroidEntryPoint
class ProfessorFormFragment : Fragment() {

    private var _binding: FragmentProfessorFormBinding? = null
    private val binding get() = _binding!!
    private val professorViewModel: ProfessorViewModel by viewModels()

    private var professorId: Int? = null
    private var selectedDepartmentId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfessorFormBinding.inflate(inflater, container, false)
        val root: View = binding.root

        professorId = arguments?.getInt("professorId")
        (activity as? AppCompatActivity)?.supportActionBar?.title =
            if (professorId != null) getString(R.string.update_professor) else getString(R.string.save_professor)

        setupUI()
        setupObservers()

        return root
    }

    private fun setupUI() {
        binding.buttonSaveProfessor.setOnClickListener {
            closeKeyboard()
            val professorName = binding.editTextProfessorName.text.toString()
            val cpf = binding.editTextProfessorCpf.text.toString()
            val departmentId = selectedDepartmentId

            if (professorName.isBlank()) {
                showSnackbar(binding.root, getString(R.string.professor_name_required))
            } else if (cpf.isBlank()) {
                showSnackbar(binding.root, getString(R.string.professor_cpf_required))
            } else if (Objects.isNull(departmentId)) {
                showSnackbar(binding.root, getString(R.string.professor_department_required))
            } else {
                handleSaveProfessor(
                    Professor(
                        name = professorName,
                        cpf = cpf,
                        departmentId = departmentId!!
                    )
                )
            }
        }

        professorId?.let {
            professorViewModel.getByIdProfessor(professorId!!) { result ->
                when (result) {
                    is Result.Success -> {}
                    is Result.Error -> showSnackbar(
                        binding.root,
                        getString(R.string.error_loading_professor)
                    )
                }
            }
        }
    }

    private fun handleSaveProfessor(professor: Professor) {
        toggleUIElements(false)
        if (professorId != null) {
            professorViewModel.updateProfessor(professorId!!, professor)
        } else {
            professorViewModel.saveProfessor(professor)
        }
    }

    private fun setupObservers() {
        professorViewModel.professor.observe(viewLifecycleOwner) { professor ->
            binding.editTextProfessorName.setText(professor?.name)
            binding.editTextProfessorCpf.setText(professor?.cpf)
            professor?.department?.name?.let {
                binding.autoCompleteDepartmentTextView.setText(it, false)
                selectedDepartmentId = professor.department.id
            }
        }

        professorViewModel.getAllDepartments()
        professorViewModel.departments.observe(viewLifecycleOwner) { departments ->
            val departmentNames = departments.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                departmentNames
            )
            binding.autoCompleteDepartmentTextView.setAdapter(adapter)
            binding.autoCompleteDepartmentTextView.setOnItemClickListener { _, _, position, _ ->
                selectedDepartmentId = departments[position].id
            }
        }

        professorViewModel.saveProfessorResult.observe(viewLifecycleOwner) { result ->
            val message =
                if (professorId != null) getString(R.string.professor_updated) else getString(R.string.professor_saved)
            when (result) {
                is Result.Success -> {
                    showSnackbar(binding.root, message)
                    findNavController().navigateUp()
                }

                is Result.Error -> {
                    showSnackbar(binding.root, getString(R.string.error_saving_professor))
                    toggleUIElements(true)
                }
            }
        }
    }

    private fun toggleUIElements(enable: Boolean) {
        binding.buttonSaveProfessor.isEnabled = enable
        binding.editTextProfessorName.isEnabled = enable
        binding.editTextProfessorCpf.isEnabled = enable
        binding.autoCompleteDepartmentTextView.isEnabled = enable
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
