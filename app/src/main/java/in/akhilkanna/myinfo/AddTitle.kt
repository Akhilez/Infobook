package `in`.akhilkanna.myinfo

import `in`.akhilkanna.myinfo.dataStructures.Title
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_add_title.*

class AddTitle : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


    }

    fun addTitle(view: View){
        if (!name_text.text.isEmpty()){
            val title = Title(Title.nextId(), name_text.text.toString(), is_protected.isChecked)
            val status = title.commit()
            if (status != null) Snackbar.make(view , status, Snackbar.LENGTH_LONG).show()
        }
        else {
            Snackbar.make(view , "Please Enter the title.", Snackbar.LENGTH_LONG).show()
        }
    }
}
