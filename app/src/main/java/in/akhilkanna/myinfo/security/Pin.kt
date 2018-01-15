package `in`.akhilkanna.myinfo.security

import android.content.Context



class Pin (private val context: Context) {

    private var pin = IntArray(4)
    private var length = 0

    fun enter(number: Int): Boolean {
        if (length == 4) return false
        pin[length++] = number
        if (length == 4) {
            return validate()
        }
        return false
    }

    fun destroy(){
        length = 0
    }

    fun back() {
        if (length > 0)
            length--
    }

    fun numEntered(): Int {
        return length
    }

    private fun validate(): Boolean {
        return Encryption(pinString()).validate(getRegisteredPin())
    }

    private fun getRegisteredPin(): String {
        return context.getSharedPreferences("Common", Context.MODE_PRIVATE).getString("pin", null)
    }

    private fun pinString(): String {
        return ""+pin[0]+pin[1]+pin[2]+pin[3]
    }

    private fun pinExists(): Boolean {
        return context.getSharedPreferences("Common", Context.MODE_PRIVATE).getString("pin", null) != null

        /*
        if (pin == null){
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Title")
            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            builder.setView(input)
            builder.setPositiveButton("OK", { dialog, which ->
                val editor = sharedPref.edit()
                editor.putString("pin", Encryption(input.text.toString()).hash)
                editor.apply()
            })
            builder.setNegativeButton("Cancel", { dialog, which -> dialog.cancel() })
            builder.show()
        } else return true
        */
    }


}