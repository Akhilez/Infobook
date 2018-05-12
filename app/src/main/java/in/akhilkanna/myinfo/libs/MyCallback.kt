package `in`.akhilkanna.myinfo.libs

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class MyCallback(private var adapter: InfoAdapter) : ItemTouchHelper.Callback() {
    private var mOrderChanged = false

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) = makeMovementFlags(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
    )

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        if (viewHolder != null && target != null)
            adapter.onViewMoved(viewHolder.adapterPosition, target.adapterPosition)
        mOrderChanged = true
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {}

    override fun isLongPressDragEnabled() = false

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {
            adapter.itemDropped(viewHolder)
            mOrderChanged = false
        }
    }

    interface ActionCompletionContract {
        fun onViewMoved(oldPosition: Int, newPosition: Int)
        fun itemDropped(holder: RecyclerView.ViewHolder?)
    }
}