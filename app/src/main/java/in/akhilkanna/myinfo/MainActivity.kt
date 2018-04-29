package `in`.akhilkanna.myinfo

import `in`.akhilkanna.myinfo.dataStructures.Title
import `in`.akhilkanna.myinfo.fragments.PinFragment
import `in`.akhilkanna.myinfo.libs.MyCallback
import android.app.ActivityOptions
import android.content.ComponentCallbacks2
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.*
import com.wunderlist.slidinglayer.SlidingLayer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_items.view.*
import kotlinx.android.synthetic.main.layout_title.view.*
import android.support.v7.view.ActionMode


class MainActivity : AppCompatActivity(), PinFragment.PinListener {

    //private val pin = Pin(this@MainActivity)
    private var titleClicked: Title? = null
    private var titleClickedView: View? = null
    private var unlockAll = false
    private val unlockedTitles = HashSet<Title>()
    private var pinFragment: PinFragment? = null
    private var pinReasonCode = ""

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var itemHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        unlockAll = getUnlockMode()

        setUpLayout()

    }

    private fun getUnlockMode(): Boolean {
        // TODO get this value from settings sharedPref
        return false
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            // Get called every-time when application went to background.
            pinFragment?.lock()
        }
    }

    override fun onBackPressed() {
        when {
            login_sliding_layer.isOpened -> login_sliding_layer.closeLayer(true)
            items_sliding_layer.isOpened -> items_sliding_layer.closeLayer(true)
            else -> super.onBackPressed()
        }
    }

    private fun setUpLayout() {

        pinFragment = pin_fragment_main as PinFragment

        val titles = Title.getAll(this)

        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(titles)

        recyclerView = titles_list as RecyclerView

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val dragHelper = MyCallback(viewAdapter as MyAdapter)
        itemHelper = ItemTouchHelper(dragHelper)
        itemHelper.attachToRecyclerView(recyclerView)


        items_sliding_layer.setOnInteractListener(object : SlidingLayer.OnInteractListener {
            override fun onClose() {}
            override fun onClosed() {
                items_sliding_layer.items_list.adapter = null
            }

            override fun onShowPreview() {}
            override fun onOpen() {}
            override fun onOpened() {}
            override fun onPreviewShowed() {}
        })

        login_sliding_layer.setOnInteractListener(object : SlidingLayer.OnInteractListener {
            override fun onPreviewShowed() {}
            override fun onOpen() {}
            override fun onOpened() {}
            override fun onClosed() {
                pinFragment?.destroy()
            }

            override fun onShowPreview() {}
            override fun onClose() {}
        })

    }

    override fun pinSuccess() {
        when (pinReasonCode) {
            "edit" -> {
                login_sliding_layer.closeLayer(true)
                startEditActivity(titleClicked!!)
            }
            "open" -> {
                login_sliding_layer.closeLayer(true)
                if (!unlockAll)
                    unlockedTitles.add(titleClicked!!)
                lock_icon.visibility = View.VISIBLE

                titleClicked?.let { titleClickedView?.let { view -> openItemsLayer(it, view) } }
            }
        }
    }

    override fun pinFailed() {
        login_sliding_layer.closeLayer(true)
    }

    private fun openItemsLayer(title: Title, transitionView: View) {
        titleClicked = title
        titleClickedView = transitionView
        if (title.isProtected)
            if ((unlockAll && pinFragment!!.isLocked()) || (!unlockAll && !unlockedTitles.contains(title))) {
                pinReasonCode = "open"
                return login_sliding_layer.openLayer(true)
            }
        val intent = Intent(this@MainActivity, ItemsActivity::class.java).apply {
            putExtra("titleId", title.id.toString())
        }
        val options = ActivityOptions
                .makeSceneTransitionAnimation(this@MainActivity, transitionView, "titleName")
        startActivity(intent, options.toBundle())
    }

    private fun startEditActivity(title: Title) {
        val intent = Intent(this@MainActivity, AddingActivity::class.java)
        intent.putExtra("editingTitle", title.id)
        startActivity(intent)
    }

    fun lockAll(view: View) {
        pinFragment?.lock()
        lock_icon.visibility = View.GONE
        if (!unlockAll) unlockedTitles.clear()
    }

    fun fabClicked(view: View) {
        val adderIntent = Intent(MainActivity@ this, AddingActivity::class.java)
        if (items_sliding_layer.isOpened && titleClicked != null) {
            adderIntent.putExtra("title", titleClicked?.id)
        }
        startActivity(adderIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class MyAdapter(private val myDataset: Array<Title>) : RecyclerView.Adapter<MyAdapter.ViewHolder>(), MyCallback.ActionCompletionContract {

        private val selected = HashSet<Int>()
        private val actionMenu = ActionMenu()
        private var actionMode: ActionMode? = null

        override fun onViewMoved(oldPosition: Int, newPosition: Int) {
            val temp = myDataset[oldPosition]
            myDataset[oldPosition] = myDataset[newPosition]
            myDataset[newPosition] = temp
            if (selected.size > 0)
                highlightAt(recyclerView.findViewHolderForAdapterPosition(oldPosition))
            notifyItemMoved(oldPosition, newPosition)
            Toast.makeText(this@MainActivity, "Dragged from $oldPosition to $newPosition", Toast.LENGTH_SHORT).show()
        }

        override fun itemDropped(holder: RecyclerView.ViewHolder?) {
            if (holder != null) {
                highlightAt(holder)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val item = LayoutInflater.from(parent.context).inflate(R.layout.layout_title, parent, false) as RelativeLayout
            return ViewHolder(item)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = holder.itemView
            item.titleText.text = myDataset[position].title
            item.titleText.tag = myDataset[position].id
            item.setOnClickListener {
                if (selected.size == 0) {
                    openItemsLayer(myDataset[position], holder.itemView.titleText)
                } else
                    highlightAt(holder)
            }
            item.setOnLongClickListener {
                highlightAt(holder)
                true
            }
            item.handle.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN && selected.size > 0) {
                    itemHelper.startDrag(holder)
                }
                false
            }
        }

        override fun getItemCount() = myDataset.size

        fun removeSelected() {
            val cloned = HashSet<Int>(selected)
            for (holder in cloned) {
                highlightAt(recyclerView.findViewHolderForAdapterPosition(holder))
            }
            selected.clear()
        }

        private fun highlightAt(holder: RecyclerView.ViewHolder) {
            highlightAt(holder.adapterPosition, holder.itemView)
            when {
                selected.size == 0 -> {
                    actionMode?.finish()
                    actionMode = null
                    holder.itemView.handle.visibility = View.INVISIBLE
                }
                selected.size == 1 -> {
                    if (actionMode == null) {
                        actionMode = startSupportActionMode(actionMenu)
                        holder.itemView.handle.visibility = View.VISIBLE
                    }
                    actionMenu.hideEdit(actionMode, false)
                }
                selected.size > 1 -> actionMenu.hideEdit(actionMode, true)
            }
        }

        private fun highlightAt(position: Int, layout: View) {
            if (selected.contains(position)) {
                layout.setBackgroundColor(Color.YELLOW)
                selected.remove(position)
            } else {
                layout.setBackgroundColor(Color.BLUE)
                selected.add(position)
            }
        }

        inner class ViewHolder(view: RelativeLayout) : RecyclerView.ViewHolder(view)

        inner class ActionMenu : android.support.v7.view.ActionMode.Callback {
            override fun onActionItemClicked(mode: android.support.v7.view.ActionMode?, item: MenuItem?): Boolean {
                return when (item?.itemId) {
                    R.id.menu_title_edit -> {
                        for (i in selected) {
                            if (myDataset[i].isProtected) {
                                titleClicked = myDataset[i]
                                pinReasonCode = "edit"
                                login_sliding_layer.openLayer(true)
                            } else {
                                startEditActivity(myDataset[i])
                            }
                            break
                        }
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

            override fun onCreateActionMode(mode: android.support.v7.view.ActionMode?, menu: Menu?): Boolean {
                mode?.menuInflater?.inflate(R.menu.context_menu_title, menu)
                return true
            }

            override fun onPrepareActionMode(mode: android.support.v7.view.ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: android.support.v7.view.ActionMode?) {
                removeSelected()
                //mode?.finish()
            }

            fun hideEdit(mode: ActionMode?, hide: Boolean) {
                val editMenu = mode?.menu?.findItem(R.id.menu_title_edit)
                editMenu?.isVisible = !hide
            }

        }

    }

}




