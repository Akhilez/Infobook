package `in`.akhilkanna.myinfo.dataStructures

import android.content.Context

abstract class Info(val id: Int) {
    abstract fun commit(context: Context?): Boolean
    abstract fun delete(context: Context?): Boolean
}