package `in`.akhilkanna.myinfo.fragments

import `in`.akhilkanna.myinfo.R
import `in`.akhilkanna.myinfo.security.Pin
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_pin.*
import kotlinx.android.synthetic.main.fragment_pin.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PinFragment.PinListener] interface
 * to handle interaction events.
 * Use the [PinFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PinFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: PinListener? = null

    private var rootView: View? = null
    private var pin: Pin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_pin, container, false)

        pin = Pin(context)

        setUpPinButtons()

        return rootView
    }

    private fun setUpPinButtons() {

        val pins = arrayOf(rootView?.pin0, rootView?.pin1, rootView?.pin2, rootView?.pin3, rootView?.pin4, rootView?.pin5, rootView?.pin6, rootView?.pin7, rootView?.pin8, rootView?.pin9)

        for ((index, pinView) in pins.withIndex()) {

            pinView?.setOnClickListener {
                highlightDot(true)

                if (pin!!.enter(index)) {
                    listener?.pinSuccess()
                    destroy()
                } else if (pin!!.numEntered() == 4) {
                    listener?.pinFailed()
                    destroy()
                }
            }
        }
        rootView?.pinBack?.setOnClickListener {
            pin!!.back()
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

    fun lock() {
        pin?.lock()
    }

    fun destroy() {
        pin?.let {
            it.destroy()
            highlightDot(false)
            highlightDot(false)
            highlightDot(false)
            highlightDot(false)
        }
    }

    fun isLocked(): Boolean {
        return if (pin != null) pin!!.isLocked() else false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PinListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement PinListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface PinListener {
        // TODO: Update argument type and name
        fun pinSuccess()

        fun pinFailed()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PinFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                PinFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
