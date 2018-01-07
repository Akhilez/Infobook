package `in`.akhilkanna.myinfo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_add_title.*

class AddTitle : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


    }

    fun add_title(view: View){
        if (name_text.text.isEmpty()){

        }
    }
}
