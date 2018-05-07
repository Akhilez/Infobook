package `in`.akhilkanna.myinfo.libs

import `in`.akhilkanna.myinfo.ItemsActivity
import `in`.akhilkanna.myinfo.MainActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class MyCallback (private var adapter: Any, private val type: MyCallback.CallbackType) : ItemTouchHelper.Callback() {
    private var mOrderChanged = false

    enum class CallbackType { TITLE, ITEM }

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) = makeMovementFlags(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
            //ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    )

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        if (type == CallbackType.TITLE) {
            if (viewHolder != null && target !=null)
                (adapter as MainActivity.TitlesAdapter).onViewMoved(viewHolder.adapterPosition, target.adapterPosition)
        } else if (type == CallbackType.ITEM) {
            if (viewHolder != null && target !=null)
                (adapter as ItemsActivity.ItemsAdapter).onViewMoved(viewHolder.adapterPosition, target.adapterPosition)
        }
        mOrderChanged = true
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {}

    override fun isLongPressDragEnabled() = false

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {
            if (type == CallbackType.TITLE) {
                (adapter as MainActivity.TitlesAdapter).itemDropped(viewHolder)
            } else if (type == CallbackType.ITEM) {
                (adapter as ItemsActivity.ItemsAdapter).itemDropped(viewHolder)
            }
            mOrderChanged = false
        }
    }

    interface ActionCompletionContract {
        fun onViewMoved(oldPosition: Int, newPosition: Int)
        fun itemDropped(holder: RecyclerView.ViewHolder?)
    }
}