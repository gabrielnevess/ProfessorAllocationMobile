package com.example.professorallocationmobile.ui.allocation

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
import com.example.professorallocationmobile.databinding.FragmentAllocationBinding
import com.example.professorallocationmobile.model.AllocationItem
import com.example.professorallocationmobile.utils.Result
import com.example.professorallocationmobile.utils.SnackbarUtils.Companion.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllocationFragment : Fragment() {

    private var _binding: FragmentAllocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: GenericRecyclerViewAdapter<AllocationItem>
    private val allocationViewModel: AllocationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllocationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.fab.setOnClickListener {
            it.findNavController().navigate(R.id.action_nav_allocation_to_nav_allocation_form)
        }

        setupRecyclerView()
        setupSwipeRefreshLayout()

        return root
    }

    private fun setupRecyclerView() {
        adapter = GenericRecyclerViewAdapter<AllocationItem>(
            itemLayout = R.layout.item_layout_allocation,
            bind = { view, item, position ->
                val dayOfWeek = view.findViewById<TextView>(R.id.dayOfWeek)
                dayOfWeek.text = item.dayOfWeek.name

                val startAndEndHour = view.findViewById<TextView>(R.id.startAndEndHour)
                startAndEndHour.text = String.format("%s - %s", item.startHour, item.endHour)

                val professorName = view.findViewById<TextView>(R.id.professorName)
                professorName.text = item.professor.name

                val courseName = view.findViewById<TextView>(R.id.courseName)
                courseName.text = item.course.name
            },
            onEditClickListener = { item, position ->
                val bundle = Bundle().apply {
                    putInt("allocationId", item.id)
                }
                findNavController().navigate(
                    R.id.action_nav_allocation_to_nav_allocation_form,
                    bundle
                )
            },
            onDeleteClickListener = { item, position ->
                AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Delete") { _, _ ->
                        allocationViewModel.deleteAllocation(item.id) { result ->
                            when (result) {
                                is Result.Success -> {
                                    adapter.removeItem(position)
                                    showSnackbar(
                                        binding.root,
                                        getString(R.string.allocation_deleted)
                                    )
                                }

                                is Result.Error -> showSnackbar(
                                    binding.root,
                                    getString(R.string.error_delete_allocation)
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

        allocationViewModel.getAllAllocations()

        allocationViewModel.allocations.observe(viewLifecycleOwner) { items ->
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

        allocationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            allocationViewModel.getAllAllocations()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}