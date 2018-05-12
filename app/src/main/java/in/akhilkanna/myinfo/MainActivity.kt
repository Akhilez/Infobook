package `in`.akhilkanna.myinfo

import `in`.akhilkanna.myinfo.dataStructures.Title
import `in`.akhilkanna.myinfo.libs.InfoAdapter
import `in`.akhilkanna.myinfo.libs.LockHelper
import `in`.akhilkanna.myinfo.libs.MyCallback
import android.app.ActivityOptions
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_title.view.*
import kotlinx.android.synthetic.main.pin_sheet.*


class MainActivity : AppCompatActivity(), LockHelper.PinListener {

    private var titleClicked: Title? = null
    private var titleClickedView: View? = null
    private var unlockAll = false
    private val unlockedTitles = HashSet<Title>()
    private lateinit var lockHelper: LockHelper
    private var pinReasonCode = ""
    private lateinit var menu: Menu
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: TitlesAdapter
    private lateinit var itemHelper: ItemTouchHelper
    private lateinit var titles: Array<Title>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        title = ""

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
            lockHelper.lock()
        }
    }

    override fun onResume() {
        super.onResume()
        viewAdapter.updateList(Title.getAll(this@MainActivity))
    }

    override fun onBackPressed() {
        when {
            lockHelper.isExpanded() -> openPins(false)
            else -> super.onBackPressed()
        }
    }

    private fun setUpLayout() {

        titles = Title.getAll(this)

        viewAdapter = TitlesAdapter(titles)

        recyclerView = titles_list as RecyclerView

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = viewAdapter
        }

        val dragHelper = MyCallback(viewAdapter)
        itemHelper = ItemTouchHelper(dragHelper)
        itemHelper.attachToRecyclerView(recyclerView)

        lockHelper = LockHelper(this@MainActivity, pin_sheet)

    }

    override fun pinSuccess() {
        when (pinReasonCode) {
            "edit" -> {
                openPins(false)
                startEditActivity(titleClicked!!)
            }
            "open" -> {
                openPins(false)
                if (!unlockAll)
                    unlockedTitles.add(titleClicked!!)
                menu.findItem(R.id.lock_all_menu).isVisible = true
                titleClicked?.let { titleClickedView?.let { view -> openItemsLayer(it, view) } }
            }
        }
    }

    override fun pinFailed() {
        openPins(false)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(50) // for 500 ms
        }
    }

    private fun openItemsLayer(title: Title, transitionView: View) {
        titleClicked = title
        titleClickedView = transitionView
        if (title.isProtected)
            if ((unlockAll && lockHelper.isLocked()) || (!unlockAll && !unlockedTitles.contains(title))) {
                pinReasonCode = "open"
                openPins(true)
                return
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

    private fun lockAll() {
        lockHelper.lock()
        menu.findItem(R.id.lock_all_menu).isVisible = false
        if (!unlockAll) unlockedTitles.clear()
    }

    fun fabClicked(view: View) {
        val adderIntent = Intent(MainActivity@ this, AddingActivity::class.java)
        startActivity(adderIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        this@MainActivity.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.lock_all_menu -> {
                lockAll()
                true
            }
            R.id.search_menu -> {
                // TODO Implement search
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        // close lock pins when touched anywhere else
        if (!lockHelper.handleOutsideTouch(event))
            return false
        return super.dispatchTouchEvent(event)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        titleClickedView = v
        titleClicked = viewAdapter.getInfoFromView(titleClickedView) as Title
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_longpress, menu)
        Toast.makeText(this@MainActivity, "Created", Toast.LENGTH_SHORT).show()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.move_menu -> {
                titleClickedView?.handle?.visibility = View.VISIBLE
                true
            }
            R.id.edit_menu -> {
                viewAdapter.editAt(titleClickedView)
                true
            }
            R.id.copy_menu -> {
                // TODO do on copy
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun openPins(open: Boolean) {
        if (open) {
            fab.hide()
            lockHelper.openSheet()
        } else {
            lockHelper.closeSheet()
        }
    }

    override fun onCollapsed() {
        super.onCollapsed()
        fab.show()
    }

    inner class TitlesAdapter(titlesList: Array<Title>) : InfoAdapter(titlesList) {

        override fun itemDropped(holder: RecyclerView.ViewHolder?) {
            if (titleClickedView == null) return
            titleClickedView!!.handle.visibility = View.GONE
            notifyDataSetChanged()
            infoList.forEach { it.commit(this@MainActivity) }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = holder.itemView
            val titleObj = infoList[position] as Title
            item.titleText.text = titleObj.title
            item.tag = titleObj.id
            item.setOnClickListener {
                openItemsLayer(titleObj, holder.itemView.titleText)
            }
            item.handle.setOnTouchListener { _: View, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    itemHelper.startDrag(holder)
                }
                false
            }
            this@MainActivity.registerForContextMenu(item)
        }

        fun editAt(titleView: View?){
            val selected = getInfoFromView(titleView) as Title
            this@MainActivity.titleClicked = selected
            if (selected.isProtected) {
                pinReasonCode = "edit"
                openPins(true)
            } else {
                startEditActivity(selected)
            }
        }

    }

}




