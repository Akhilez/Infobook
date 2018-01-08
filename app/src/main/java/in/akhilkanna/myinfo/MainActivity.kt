package `in`.akhilkanna.myinfo

import `in`.akhilkanna.myinfo.dataStructures.Item
import `in`.akhilkanna.myinfo.dataStructures.Title
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.content.Context
import android.support.design.widget.Snackbar
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.title_layout.view.*
import kotlinx.android.synthetic.main.item_layout.view.*
import android.view.LayoutInflater


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            startActivity(Intent(MainActivity@this, AddTitle::class.java))
        }

        val titles = Title.getAll()

        val adapter = TitlesAdapter(titles, this)

        titles_list.adapter = adapter

        titles_list.onItemClickListener = AdapterView.OnItemClickListener{ adapterView: AdapterView<*>, view: View, position: Int, id: Long ->
            val title = titles[position]
            openItemsLayer(title)
        }
    }

    fun openItemsLayer(title: Title) {
        if (title.isProtected){
            login_sliding_layer.openLayer(true)
        } else {
            buildItemsLayer(title)
            items_sliding_layer.openLayer(true)
        }
    }

    fun buildItemsLayer(title: Title) {
        val items = Item.getItems(title)
        val adapter = ItemsAdapter(items, this)

        items_list.adapter = adapter

        items_list.onItemClickListener = AdapterView.OnItemClickListener{ adapterView: AdapterView<*>, view: View, position: Int, id: Long ->
            Snackbar.make(view, items[position].key, Snackbar.LENGTH_LONG).show()
        }
    }

    fun title_clicked(view: View){
        login_sliding_layer.openLayer(true)
    }

    fun open_items_layer(view: View){
        login_sliding_layer.closeLayer(true)
        items_sliding_layer.openLayer(true)
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

            if (!item.hidden) {
                view?.hideButton?.visibility = View.GONE
            }

            return view
        }
    }


}




