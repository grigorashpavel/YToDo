package com.pasha.ytodo.presentation.tasks.adapters

import androidx.recyclerview.widget.RecyclerView
import com.pasha.ytodo.databinding.ItemAddActionBinding

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