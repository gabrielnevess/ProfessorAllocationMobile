package com.example.professorallocationmobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.professorallocationmobile.R

class GenericRecyclerViewAdapter<T>(
    private val itemLayout: Int,
    private val bind: (View, T, Int) -> Unit,
    private var onEditClickListener: (T, Int) -> Unit,
    private var onDeleteClickListener: (T, Int) -> Unit
) : RecyclerView.Adapter<GenericRecyclerViewAdapter<T>.ViewHolder>() {

    private val items: MutableList<T> = mutableListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.findViewById<View>(R.id.editButton).setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClickListener.invoke(items[position], position)
                }
            }
            itemView.findViewById<View>(R.id.deleteButton).setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClickListener.invoke(items[position], position)
                }
            }
        }

        fun bind(item: T, position: Int) {
            bind(itemView, item, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<T>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}
