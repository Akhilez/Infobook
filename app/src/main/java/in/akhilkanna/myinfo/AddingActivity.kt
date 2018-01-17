package `in`.akhilkanna.myinfo

import `in`.akhilkanna.myinfo.dataStructures.Item
import `in`.akhilkanna.myinfo.dataStructures.Title
import `in`.akhilkanna.myinfo.fragments.adding.AddingItemFragment
import `in`.akhilkanna.myinfo.fragments.adding.AddingTitleFragment
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_adding.*

class AddingActivity : AppCompatActivity(), AddingTitleFragment.TitleCreationListener {

    private var isTitleFragment = true
    private var fragmentTransaction: FragmentTransaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding)
        setSupportActionBar(toolbar)

        val titleId = intent.getIntExtra("title", -1)
        fragmentTransaction = supportFragmentManager.beginTransaction()

        isTitleFragment = titleId == -1
        val fragment: Fragment?

        if (isTitleFragment){
            fragment = AddingTitleFragment()
            val editingTitle = intent.getIntExtra("editingTitle", -1)
            if (editingTitle != -1) {
                val bundle = Bundle()
                bundle.putInt("editingTitle", editingTitle)
                fragment.setArguments(bundle)
            }
        } else {
            fragment = AddingItemFragment()
            val editingItem = intent.getIntExtra("editingItem", -1)
            val bundle = Bundle()
            if (editingItem != -1)
                bundle.putInt("editingItem", editingItem)
            else
                bundle.putInt("titleId", titleId)
            fragment.arguments = bundle

        }

        fragmentTransaction?.add(R.id.fragment, fragment)
        fragmentTransaction?.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)


        fragmentTransaction?.commit()

    }
    override fun onTitleCreated(title: Title) {
        fragmentTransaction?.replace(R.id.fragment, AddingItemFragment(), title.id.toString())
        fragmentTransaction?.commit()
    }
}
