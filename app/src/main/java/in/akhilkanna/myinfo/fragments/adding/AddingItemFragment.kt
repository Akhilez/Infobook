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
    private var editItem: Item? = null
    var title: Title? = null
    private var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_adding_item, container, false)

        val arguments = arguments
        if (arguments != null) {
            val titleId = arguments.getInt("titleId", -1)
            if (titleId != -1) {
                title = Title.get(context, titleId)
                rootView?.titleHeading?.text = title?.title
            }

            val editingItemId = arguments.getInt("editingItem", -1)

            if (editingItemId != -1) {
                editItem = Item.get(context, editingItemId)
                addDataToUI(editItem!!)
            }
        }

        rootView?.save_item_fab?.setOnClickListener { saveItem() }

        rootView?.delete_item?.setOnClickListener {
            editItem?.let {
                if (it.delete(context)) {
                    // TODO Success deleting item
                } else {
                    Snackbar.make(rootView!!, "Failed", Snackbar.LENGTH_LONG)
                }
            }
        }

        return rootView
    }

    private fun saveItem() {
        val errorMsg = getErrorMessage()
        if (errorMsg != null) {
            return Snackbar.make(rootView!!, errorMsg, Snackbar.LENGTH_LONG).show()
        }
        if (editItem != null) {
            commitItem(editItem!!)
        } else if (title != null) {
            createItem()
        }
    }

    private fun createItem() {
        val newItem = Item.create(context, title!!, item_key_edit_text.text.toString(), item_value_edit_text.text.toString(), hide_switch.isChecked)
        if (newItem == null) {
            return Snackbar.make(rootView!!, "Failed", Snackbar.LENGTH_LONG).show()
        } else {
            Snackbar.make(rootView!!, "Added Successfully", Snackbar.LENGTH_LONG).show()
            // TODO success, so go to main activity
            activity?.finish()
        }
    }

    private fun commitItem(item: Item) {
        item.key = item_key_edit_text.text.toString()
        item.value = item_value_edit_text.text.toString()
        item.hidden = hide_switch.isChecked
        if (!item.commit(context)) {
            Snackbar.make(rootView!!, "Failed to update", Snackbar.LENGTH_LONG).show()
        } else {
            // TODO Success
        }
    }

    private fun getErrorMessage(): String? {
        if (item_key_edit_text.text.isEmpty())
            return "Please enter the description."
        if (item_value_edit_text.text.isEmpty())
            return "Please enter the value."
        return null
    }

    private fun addDataToUI(item: Item) {
        rootView?.titleHeading?.text = item.title.title
        rootView?.item_key_edit_text?.setText(item.key)
        rootView?.item_value_edit_text?.setText(item.value)
        rootView?.hide_switch?.isChecked = item.hidden
        rootView?.delete_item?.visibility = View.VISIBLE
    }
}
