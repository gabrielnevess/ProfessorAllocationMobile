package com.example.professorallocationmobile.ui.department

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.professorallocationmobile.R
import com.example.professorallocationmobile.adapter.GenericRecyclerViewAdapter
import com.example.professorallocationmobile.databinding.FragmentDepartmentBinding
import com.example.professorallocationmobile.model.Department
import com.example.professorallocationmobile.utils.Result
import com.example.professorallocationmobile.utils.SnackbarUtils.Companion.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DepartmentFragment : Fragment() {

    private var _binding: FragmentDepartmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: GenericRecyclerViewAdapter<Department>
    private val departmentViewModel: DepartmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDepartmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.fab.setOnClickListener {
            it.findNavController().navigate(R.id.action_nav_department_to_nav_department_form)
        }

        setupRecyclerView()
        setupSwipeRefreshLayout()

        return root
    }

    private fun setupRecyclerView() {
        adapter = GenericRecyclerViewAdapter<Department>(
            itemLayout = R.layout.item_layout_department,
            bind = { view, item, position ->
                val textView = view.findViewById<TextView>(R.id.departmentName)
                textView.text = item.name
            },
            onEditClickListener = { item, position ->
                val bundle = Bundle().apply {
                    putInt("departmentId", item.id!!)
                }
                findNavController().navigate(
                    R.id.action_nav_department_to_nav_department_form,
                    bundle
                )
            },
            onDeleteClickListener = { item, position ->
                AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Delete") { _, _ ->
                        departmentViewModel.deleteDepartment(item.id!!) { result ->
                            when (result) {
                                is Result.Success -> {
                                    adapter.removeItem(position)
                                    showSnackbar(
                                        binding.root,
                                        getString(R.string.department_deleted)
                                    )
                                }

                                is Result.Error -> showSnackbar(
                                    binding.root,
                                    getString(R.string.error_delete_department)
                                )
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        departmentViewModel.getAllDepartments()

        departmentViewModel.departments.observe(viewLifecycleOwner) { items ->
            adapter.setItems(items)
            binding.swipeRefreshLayout.isRefreshing = false

            if (items.isEmpty()) {
                binding.emptyTextView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.emptyTextView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }

        departmentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            departmentViewModel.getAllDepartments()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}