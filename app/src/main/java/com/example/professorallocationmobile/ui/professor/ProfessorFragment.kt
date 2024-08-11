package com.example.professorallocationmobile.ui.professor

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
import com.example.professorallocationmobile.databinding.FragmentProfessorBinding
import com.example.professorallocationmobile.model.ProfessorItem
import com.example.professorallocationmobile.utils.Result
import com.example.professorallocationmobile.utils.SnackbarUtils.Companion.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfessorFragment : Fragment() {

    private var _binding: FragmentProfessorBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: GenericRecyclerViewAdapter<ProfessorItem>
    private val professorViewModel: ProfessorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfessorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.fab.setOnClickListener {
            it.findNavController().navigate(R.id.action_nav_professor_to_nav_professor_form)
        }

        setupRecyclerView()
        setupSwipeRefreshLayout()

        return root
    }

    private fun setupRecyclerView() {
        adapter = GenericRecyclerViewAdapter<ProfessorItem>(
            itemLayout = R.layout.item_layout_professor,
            bind = { view, item, position ->
                val professorName = view.findViewById<TextView>(R.id.professorName)
                professorName.text = item.name

                val professorCpf = view.findViewById<TextView>(R.id.professorCpf)
                professorCpf.text = item.cpf

                val departmentName = view.findViewById<TextView>(R.id.departmentName)
                departmentName.text = item.department.name
            },
            onEditClickListener = { item, position ->
                val bundle = Bundle().apply {
                    putInt("professorId", item.id)
                }
                findNavController().navigate(
                    R.id.action_nav_professor_to_nav_professor_form,
                    bundle
                )
            },
            onDeleteClickListener = { item, position ->
                AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Delete") { _, _ ->
                        professorViewModel.deleteProfessor(item.id) { result ->
                            when (result) {
                                is Result.Success -> {
                                    adapter.removeItem(position)
                                    showSnackbar(
                                        binding.root,
                                        getString(R.string.professor_deleted)
                                    )
                                }

                                is Result.Error -> showSnackbar(
                                    binding.root,
                                    getString(R.string.error_delete_professor)
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

        professorViewModel.getAllProfessors()

        professorViewModel.professors.observe(viewLifecycleOwner) { items ->
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

        professorViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            professorViewModel.getAllProfessors()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}