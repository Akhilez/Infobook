package `in`.akhilkanna.myinfo

import `in`.akhilkanna.myinfo.dataStructures.Item
import `in`.akhilkanna.myinfo.dataStructures.Title
import `in`.akhilkanna.myinfo.security.Pin
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.wunderlist.slidinglayer.SlidingLayer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_layout.view.*
import kotlinx.android.synthetic.main.title_layout.view.*


class MainActivity : AppCompatActivity() {
    private val pin = Pin(this@MainActivity)
    var titleClicked : Title? = null
    private var pinValidated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            startActivity(Intent(MainActivity@this, AddTitle::class.java))
        }

        setUpSlidingLayers()

        setUpPinButtons()

    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            // Get called every-time when application went to background.
            pinValidated = false
        }
    }

    private fun setUpSlidingLayers() {
        val titles = Title.getAll(this)

        val adapter = TitlesAdapter(titles, this)

        titles_list.adapter = adapter

        titles_list.onItemClickListener = AdapterView.OnItemClickListener{ adapterView: AdapterView<*>, view: View, position: Int, id: Long ->
            val title = titles[position]
            openItemsLayer(title)
        }

        items_sliding_layer.setOnInteractListener(object: SlidingLayer.OnInteractListener{
            override fun onClose() {}
            override fun onClosed() {
                items_sliding_layer.items_list.adapter = null
            }
            override fun onShowPreview() {}
            override fun onOpen() {}
            override fun onOpened() {}
            override fun onPreviewShowed() {}
        })

        login_sliding_layer.setOnInteractListener(object: SlidingLayer.OnInteractListener {
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

    private fun setUpPinButtons(){
        val pins = arrayOf(pin0, pin1, pin2, pin3, pin4, pin5, pin6, pin7, pin8, pin9)
        for ((index, pinView) in pins.withIndex()){
            pinView.setOnClickListener {
                highlightDot(true)
                if (pin.enter(index) && titleClicked != null) {
                    pinValidated = true
                    //main_parent.removeView(login_sliding_layer)
                    buildItemsLayer(titleClicked!!)
                    login_sliding_layer.closeLayer(true)
                    items_sliding_layer.openLayer(true)
                } else if (pin.numEntered() == 4){
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
        val highlightColor = Color.parseColor("#000999")
        val dimColor = Color.parseColor("#666666")
        if (dot4.tag == "1") {
            if (!highlight) {
                dot4.tag = "0"
                dot4.setColorFilter(dimColor, PorterDuff.Mode.MULTIPLY)
            }
        } else if (dot3.tag == "1") {
            if (highlight) {
                dot4.tag = "1"
                dot4.setColorFilter(highlightColor, PorterDuff.Mode.MULTIPLY)
            } else {
                dot3.tag = "0"
                dot3.setColorFilter(dimColor, PorterDuff.Mode.MULTIPLY)
            }
        } else if (dot2.tag == "1") {
            if (highlight) {
                dot3.tag = "1"
                dot3.setColorFilter(highlightColor, PorterDuff.Mode.MULTIPLY)
            } else {
                dot2.tag = "0"
                dot2.setColorFilter(dimColor, PorterDuff.Mode.MULTIPLY)
            }
        } else if (dot1.tag == "1") {
            if (highlight) {
                dot2.tag = "1"
                dot2.setColorFilter(highlightColor, PorterDuff.Mode.MULTIPLY)
            } else {
                dot1.tag = "0"
                dot1.setColorFilter(dimColor, PorterDuff.Mode.MULTIPLY)
            }
        } else if (highlight) {
            dot1.tag = "1"
            dot1.setColorFilter(highlightColor, PorterDuff.Mode.MULTIPLY)
        }
    }

    private fun openItemsLayer(title: Title) {
        titleClicked = title
        if (title.isProtected && !pinValidated){
            login_sliding_layer.openLayer(true)
        } else {
            buildItemsLayer(title)
            items_sliding_layer.openLayer(true)
        }
    }

    private fun buildItemsLayer(title: Title) {
        val items = Item.getItems(this, title)
        val adapter = ItemsAdapter(items, this)

        items_list.adapter = adapter

        items_list.onItemClickListener = AdapterView.OnItemClickListener{ adapterView: AdapterView<*>, view: View, position: Int, id: Long ->
            Snackbar.make(view, items[position].key, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    class TitlesAdapter(titles: Array<Title>, context: Context) : ArrayAdapter<Title>(context, R.layout.title_layout, titles) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val title = getItem(position)

            var view = convertView

            if (convertView == null){
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(R.layout.title_layout, parent, false)
            }
            view?.titleText?.text = title.title
            view?.tag = title.id

            return view
        }


    }

    class ItemsAdapter(items: Array<Item>, context: Context): ArrayAdapter<Item>(context, R.layout.item_layout, items){

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val item = getItem(position)

            var view = convertView

            if (convertView == null){
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(R.layout.item_layout, parent, false)
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




