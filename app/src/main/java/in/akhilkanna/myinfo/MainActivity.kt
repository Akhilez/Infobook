package `in`.akhilkanna.myinfo

import `in`.akhilkanna.myinfo.dataStructures.Title
import `in`.akhilkanna.myinfo.fragments.ItemsFragment
import `in`.akhilkanna.myinfo.security.Pin
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.wunderlist.slidinglayer.SlidingLayer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_items.view.*
import kotlinx.android.synthetic.main.layout_title.view.*




class MainActivity : AppCompatActivity() {

    private val pin = Pin(this@MainActivity)
    private var titleClicked: Title? = null
    private var unlockAll = true
    private val unlockedTitles = HashSet<Title>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpSlidingLayers()

        setUpPinButtons()

        unlockAll = getUnlockMode()

    }

    private fun getUnlockMode(): Boolean {
        // TODO get this value from settings sharedPref
        return true
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            // Get called every-time when application went to background.
            pin.lock()
        }
    }

    override fun onBackPressed() {
        when {
            login_sliding_layer.isOpened -> login_sliding_layer.closeLayer(true)
            items_sliding_layer.isOpened -> items_sliding_layer.closeLayer(true)
            else -> super.onBackPressed()
        }
    }

    private fun setUpSlidingLayers() {
        val titles = Title.getAll(this)

        val adapter = TitlesAdapter(titles, this)
        titles_list.adapter = adapter

        titles_list.onItemClickListener = AdapterView.OnItemClickListener { adapterView: AdapterView<*>, view: View, position: Int, id: Long ->
            val title = titles[position]
            openItemsLayer(title)
        }

        titles_list.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        titles_list.setMultiChoiceModeListener(object: AbsListView.MultiChoiceModeListener {

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return when (item?.itemId) {
                    R.id.menu_title_edit -> {
                        val intent = Intent(this@MainActivity, AddingActivity::class.java)
                        for (title in adapter.selectedTitles) {
                            intent.putExtra("editingTitle", title.id)
                            break
                        }
                        startActivity(intent)
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
                val numSelected = titles_list.checkedItemCount
                mode?.menu?.findItem(R.id.menu_title_edit)?.isVisible = numSelected <= 1
                mode?.title = numSelected.toString() + " Selected"
                adapter.toggleSelection(position)
            }

            override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                appbar.visibility = View.GONE
                p0?.menuInflater?.inflate(R.menu.context_menu_title, p1)
                return true
            }

            override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(p0: ActionMode?) {
                appbar.visibility = View.VISIBLE
                adapter.clearSelection()
            }

        })

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
                pin.destroy()
                highlightDot(false)
                highlightDot(false)
                highlightDot(false)
                highlightDot(false)
            }

            override fun onShowPreview() {}
            override fun onClose() {}
        })

    }

    private fun setUpPinButtons() {

        val pins = arrayOf(pin0, pin1, pin2, pin3, pin4, pin5, pin6, pin7, pin8, pin9)

        for ((index, pinView) in pins.withIndex()) {

            pinView.setOnClickListener {
                highlightDot(true)

                if (pin.enter(index) && titleClicked != null) {
                    if (!unlockAll) unlockedTitles.add(titleClicked!!)

                    buildItemsLayer(titleClicked!!)
                    lock_icon.visibility = View.VISIBLE

                    login_sliding_layer.closeLayer(true)
                    items_sliding_layer.openLayer(true)

                } else if (pin.numEntered() == 4) {
                    login_sliding_layer.closeLayer(true)
                }
            }
        }
        pinBack.setOnClickListener {
            pin.back()
            highlightDot(false)
        }
    }

    private fun highlightDot(highlight: Boolean) {
        val highlightColor = R.drawable.ic_lens_black_24dp
        val dimColor = R.drawable.ic_panorama_fish_eye_black_24dp
        if (dot4.tag == "1") {
            if (!highlight) {
                dot4.tag = "0"
                dot4.setImageResource(dimColor)
            }
        } else if (dot3.tag == "1") {
            if (highlight) {
                dot4.tag = "1"
                dot4.setImageResource(highlightColor)
            } else {
                dot3.tag = "0"
                dot3.setImageResource(dimColor)
            }
        } else if (dot2.tag == "1") {
            if (highlight) {
                dot3.tag = "1"
                dot3.setImageResource(highlightColor)
            } else {
                dot2.tag = "0"
                dot2.setImageResource(dimColor)
            }
        } else if (dot1.tag == "1") {
            if (highlight) {
                dot2.tag = "1"
                dot2.setImageResource(highlightColor)
            } else {
                dot1.tag = "0"
                dot1.setImageResource(dimColor)
            }
        } else if (highlight) {
            dot1.tag = "1"
            dot1.setImageResource(highlightColor)
        }
    }

    private fun openItemsLayer(title: Title) {
        titleClicked = title
        if (title.isProtected)
            if ((unlockAll && pin.isLocked()) || (!unlockAll && !unlockedTitles.contains(title)))
                return login_sliding_layer.openLayer(true)
        buildItemsLayer(title)
        items_sliding_layer.openLayer(true)
    }

    private fun buildItemsLayer(title: Title) {
        val fragment: ItemsFragment = supportFragmentManager.findFragmentById(R.id.items_fragment) as ItemsFragment
        fragment.buildItemsList(title)
    }

    fun lockAll(view: View) {
        pin.lock()
        lock_icon.visibility = View.GONE
        if (!unlockAll) unlockedTitles.clear()
    }

    fun fabClicked(view: View){
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


    class TitlesAdapter(private val titles: Array<Title>, context: Context) : ArrayAdapter<Title>(context, R.layout.layout_title, titles) {

        var selectedTitles = HashSet<Title>()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val title = getItem(position)

            var view = convertView

            if (convertView == null) {
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(R.layout.layout_title, parent, false)
            }
            view?.titleText?.text = title.title
            view?.tag = title.id

            return view
        }

        fun toggleSelection(position: Int) {
            val title = titles[position]
            if (selectedTitles.contains(title))
                selectedTitles.remove(title)
            else selectedTitles.add(title)
        }

        fun clearSelection(){
            selectedTitles.clear()
        }

    }

}




