package `in`.akhilkanna.myinfo.fragments.adding

import `in`.akhilkanna.myinfo.R
import `in`.akhilkanna.myinfo.dataStructures.Item
import `in`.akhilkanna.myinfo.dataStructures.Title
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_adding_item.*
import kotlinx.android.synthetic.main.fragment_adding_item.view.*

/**
 * Created by akhil.devarashetti on 1/16/2018.
 *
 */

class AddingItemFragment : Fragment() {
    private var editMode = false
    private var editItem : Item? = null
    var title : Title? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_adding_item, container, false)

        val arguments = arguments
        if (arguments != null) {
            val titleId = arguments.getInt("titleId", -1)
            if (titleId != -1) {
                title = Title.get(context, titleId)
                rootView.titleHeading.text = title?.title
            }

            val editingItem = arguments.getInt("editingItem", -1)
            editMode = editingItem != -1

            if (editMode) {
                editItem = Item.get(context, editingItem)
                addDataToUI(editItem!!)
            }
        }

        rootView.save_item_fab.setOnClickListener {
            val errorMsg = getErrorMessage()
            if (errorMsg != null) {
                Snackbar.make(rootView, errorMsg, Snackbar.LENGTH_LONG).show()
            } else if (editMode && editItem != null) {
                editItem?.key = item_key_edit_text.text.toString()
                editItem?.value = item_value_edit_text.text.toString()
                editItem?.hidden = hide_switch.isChecked
                if (!editItem?.commit(context)!!) {
                    Snackbar.make(rootView, "Failed to update", Snackbar.LENGTH_LONG).show()
                } else {
                    // TODO Success
                }
            } else if (title != null){
                val newItem = Item.create(context, title!!, item_key_edit_text.text.toString(), item_value_edit_text.text.toString(), hide_switch.isChecked)
                if (newItem == null) {
                    Snackbar.make(rootView, "Failed", Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(rootView, "Added Successfully", Snackbar.LENGTH_LONG).show()
                    // TODO success, so go to main activity
                }
            }
        }

        return rootView
    }

    private fun getErrorMessage(): String? {
        if (item_key_edit_text.text.isEmpty())
            return "Please enter the description."
        if (item_value_edit_text.text.isEmpty())
            return "Please enter the value."
        return null
    }

    private fun addDataToUI (item: Item) {
        titleHeading.text = item.title.title
        item_key_edit_text.setText(item.key)
        item_value_edit_text.setText(item.value)
        hide_switch.isEnabled = item.hidden
    }
}
