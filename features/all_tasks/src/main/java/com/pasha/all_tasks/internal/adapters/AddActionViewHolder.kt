package com.pasha.all_tasks.internal.adapters

import androidx.recyclerview.widget.RecyclerView
import com.pasha.all_tasks.databinding.ItemAddActionBinding

class AddActionViewHolder(
    binding: ItemAddActionBinding,
    private val onActionAdd: () -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener {
            onActionAdd()
        }
    }
}