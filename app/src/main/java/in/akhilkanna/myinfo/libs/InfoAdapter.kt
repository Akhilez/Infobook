package `in`.akhilkanna.myinfo.libs

import `in`.akhilkanna.myinfo.R
import `in`.akhilkanna.myinfo.dataStructures.Info
import `in`.akhilkanna.myinfo.dataStructures.Title
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import java.util.*

abstract class InfoAdapter(infoList: Array<out Info>) : RecyclerView.Adapter<InfoAdapter.ViewHolder>(), MyCallback.ActionCompletionContract {

    var infoList = infoList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when {
            infoList.isEmpty() -> {
                // TODO inflate empty layout
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false) as ViewGroup)
            }
            infoList[0] is Title -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_title, parent, false) as RelativeLayout)
            else -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false) as CardView)
        }
    }

    override fun getItemCount() = infoList.size

    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        Collections.swap(infoList, oldPosition, newPosition)
        notifyItemMoved(oldPosition, newPosition)
    }

    fun updateList(newList: Array<out Info>) {
        infoList = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun getInfoFromView(infoView: View?): Info {
        val id = infoView?.tag.toString().toInt()
        return infoList.filter { id == it.id }[0]
    }

    inner class ViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view)
}