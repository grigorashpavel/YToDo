package com.pasha.ytodo.presentation.edit.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pasha.ytodo.R
import com.pasha.ytodo.databinding.ItemPriorityDropdownMenuBinding

class DropDownMenuPriorityArrayAdapter(
    private val context: Context,
    resource: Int,
    private val items: Array<String>
) :
    ArrayAdapter<String>(context, resource, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val binding = ItemPriorityDropdownMenuBinding.inflate(inflater, parent, false)

        val text = getItem(position)
        binding.tvItem.text = text

        if (position == items.size - 1) {
            val highPriorityColor = context.getColor(R.color.color_red)
            binding.tvItem.setTextColor(highPriorityColor)
        }

        return binding.root
    }
}