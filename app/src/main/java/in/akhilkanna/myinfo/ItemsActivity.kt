package `in`.akhilkanna.myinfo

import `in`.akhilkanna.myinfo.dataStructures.Item
import `in`.akhilkanna.myinfo.dataStructures.Title
import `in`.akhilkanna.myinfo.libs.MyCallback
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_items.*
import kotlinx.android.synthetic.main.layout_item.view.*

class ItemsActivity : AppCompatActivity() {

    private lateinit var title: Title
    private lateinit var items: Array<Item>
    private lateinit var viewAdapter: ItemsAdapter
    private var itemClickedView: View? = null
    private var itemClicked: Item? = null
    private lateinit var itemHelper: ItemTouchHelper
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        setSupportActionBar(toolbar)

        val extras = intent.extras
        val titleId = extras["titleId"].toString().toInt()
        val retrieved = Title.get(this@ItemsActivity, titleId)
        if (retrieved == null) {
            Toast.makeText(this@ItemsActivity, "Title not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        title = retrieved

        buildItemsList()

        fab.setOnClickListener {
            val adderIntent = Intent(MainActivity@ this, AddingActivity::class.java)
            adderIntent.putExtra("title", title.id)
            startActivity(adderIntent)
        }
    }

    private fun buildItemsList() {
        setTitle(title.title)
        titleText.text = title.title
        items = Item.getItems(this@ItemsActivity, title)

        viewAdapter = ItemsAdapter(items)

        recyclerView = items_list as RecyclerView

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ItemsActivity)
            adapter = viewAdapter
        }

        val dragHelper = MyCallback(viewAdapter, MyCallback.CallbackType.ITEM)
        itemHelper = ItemTouchHelper(dragHelper)
        itemHelper.attachToRecyclerView(recyclerView)

    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        itemClickedView = v
        itemClicked = viewAdapter.getTitleFromView(itemClickedView)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_longpress, menu)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.move_menu -> {
                itemClickedView?.handle?.visibility = View.VISIBLE
                true
            }
            R.id.edit_menu -> {
                viewAdapter.editAt(itemClickedView)
                true
            }
            R.id.copy_menu -> {
                // TODO do on copy
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    inner class ItemsAdapter (private val itemsList: Array<Item>) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>(), MyCallback.ActionCompletionContract {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false) as CardView)

        override fun getItemCount() = itemsList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val itemView = holder.itemView
            val item = itemsList[position]
            itemView.keyText.text = item.key
            itemView.valueText.text = item.value
            itemView.tag = item.id
            if (item.hidden){
                itemView.valueText.visibility = View.GONE
                itemView.hideButton.visibility = View.VISIBLE
                itemView.divider.visibility = View.GONE
            }
            itemView.hideButton?.setOnTouchListener { _, event ->
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    itemView.valueText.visibility = View.VISIBLE
                    itemView.divider.visibility = View.VISIBLE
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    itemView.valueText.visibility = View.GONE
                    itemView.divider.visibility = View.GONE
                }
                true
            }
            itemView.setOnClickListener {
                if (!item.hidden) {
                    //TODO copy the value
                }
            }
            itemView.handle.setOnTouchListener { _: View, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    itemHelper.startDrag(holder)
                }
                false
            }
            itemView.hideButton?.setOnDragListener { myView: View?, dragEvent: DragEvent? ->
                itemView.valueText.visibility = View.GONE
                false
            }
            this@ItemsActivity.registerForContextMenu(itemView)
        }

        override fun onViewMoved(oldPosition: Int, newPosition: Int) {
            val temp = itemsList[oldPosition]
            itemsList[oldPosition] = itemsList[newPosition]
            itemsList[newPosition] = temp

            notifyItemMoved(oldPosition, newPosition)
        }

        override fun itemDropped(holder: RecyclerView.ViewHolder?) {
            if (itemClickedView == null) return
            itemClickedView!!.handle.visibility = View.GONE
            notifyDataSetChanged()
            itemsList.forEach { it.commit(this@ItemsActivity) }
        }

        fun editAt(itemView: View?){
            val selected = getTitleFromView(itemView)
            this@ItemsActivity.itemClicked = selected
            val intent = Intent(this@ItemsActivity, AddingActivity::class.java)
            intent.putExtra("editingItem", selected.id)
            intent.putExtra("title", title.id)
            startActivity(intent)
        }

        fun getTitleFromView(itemView: View?): Item {
            val id = itemView?.tag.toString().toInt()
            return itemsList.filter { id == it.id }[0]
        }

        inner class ViewHolder(view: CardView) : RecyclerView.ViewHolder(view)
    }



}
