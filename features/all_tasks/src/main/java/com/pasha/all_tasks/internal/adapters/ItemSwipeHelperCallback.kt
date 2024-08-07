package com.pasha.all_tasks.internal.adapters

import android.graphics.Canvas
import android.graphics.Color
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.pasha.core_ui.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class ItemSwipeHelperCallback(
    private val adapter: TasksRecyclerViewAdapter,
) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val position = viewHolder.absoluteAdapterPosition
        val lastItemIndex = adapter.itemCount - 1

        if (position == lastItemIndex) {
            return 0
        } else {
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, swipeFlags)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun isItemViewSwipeEnabled(): Boolean = true

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val resources = viewHolder.itemView.resources
        val theme = viewHolder.itemView.context.theme
        RecyclerViewSwipeDecorator.Builder(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
            .addSwipeRightBackgroundColor(resources.getColor(R.color.color_green, theme))
            .addSwipeRightActionIcon(com.pasha.all_tasks.R.drawable.ic_check_24)
            .setSwipeRightActionIconTint(Color.WHITE)
            .addSwipeLeftBackgroundColor(resources.getColor(R.color.color_red, theme))
            .addSwipeLeftActionIcon(R.drawable.ic_delete_24)
            .setSwipeLeftActionIconTint(Color.WHITE)
            .create()
            .decorate()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.START -> {
                (viewHolder as TasksViewHolder).startActionDeleteItem()
                adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
            }

            ItemTouchHelper.END -> {
                (viewHolder as TasksViewHolder).startActionChangeProgress()
                (adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition))
            }
        }
    }
}