package `in`.akhilkanna.myinfo.fragments

import `in`.akhilkanna.myinfo.R
import `in`.akhilkanna.myinfo.dataStructures.Item
import `in`.akhilkanna.myinfo.dataStructures.Title
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.*
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.fragment_items.*
import kotlinx.android.synthetic.main.layout_item.view.*

class ItemsFragment : Fragment(){
    var title: Title? = null
    var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_items, container, false)

        return rootView
    }

    fun buildItemsList(title_: Title) {
        title = title_
        titleHeading.text = title_.title

        val items = Item.getItems(context, title_)
        items_list.adapter = ItemsAdapter(items, context)

        items_list.onItemClickListener = AdapterView.OnItemClickListener{ adapterView: AdapterView<*>, view: View, position: Int, id: Long ->
            Snackbar.make(view, items[position].key, Snackbar.LENGTH_LONG).show()
        }

        items_list.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        items_list.setMultiChoiceModeListener(object: AbsListView.MultiChoiceModeListener {

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return when (item?.itemId) {
                    R.id.menu_title_edit -> {
                        mode?.finish()
                        true
                    }
                    R.id.menu_title_share -> {
                        mode?.finish()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            override fun onItemCheckedStateChanged(mode: ActionMode?, position: Int, id: Long, checked: Boolean) {

            }

            override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                p0?.menuInflater?.inflate(R.menu.context_menu_title, p1)
                return true
            }

            override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(p0: ActionMode?) {

            }

        })
    }


    class ItemsAdapter(items: Array<Item>, context: Context): ArrayAdapter<Item>(context, R.layout.layout_item, items){

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val item = getItem(position)

            var view = convertView

            if (convertView == null){
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(R.layout.layout_item, parent, false)
            }

            view?.keyText?.text = item.key
            view?.valueText?.text = item.value

            if (item.hidden) {
                val hideButton = view?.hideButton
                hideButton?.visibility = View.VISIBLE
                view?.valueText?.visibility = View.INVISIBLE

                hideButton?.setOnTouchListener { myView, event ->
                    if (event?.action == MotionEvent.ACTION_DOWN) {
                        view?.valueText?.visibility = View.VISIBLE
                    }else if (event?.action == MotionEvent.ACTION_UP){
                        view?.valueText?.visibility = View.INVISIBLE
                    }
                    true
                }

                hideButton?.setOnDragListener { myView: View?, dragEvent: DragEvent? ->
                    view?.valueText?.visibility = View.INVISIBLE
                    false
                }


            }

            return view
        }
    }
}