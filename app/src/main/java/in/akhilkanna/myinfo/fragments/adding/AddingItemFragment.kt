package `in`.akhilkanna.myinfo.fragments.adding

import `in`.akhilkanna.myinfo.R
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by akhil.devarashetti on 1/16/2018.
 *
 */

class AddingItemFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_adding, container, false)
    }
}
