package `in`.akhilkanna.myinfo.dataStructures

import `in`.akhilkanna.myinfo.db.SqliteHelper
import android.content.Context

class Title (val id: Int, var title: String, var isProtected: Boolean) {

    fun commit(context: Context): Boolean {
        return false
        // TODO write this
    }

    override fun toString(): String {
        return title
    }

    companion object {
        val TABLE_NAME = "title"
        val ID = "id"
        val TITLE = "title"
        val PROTECTED = "protected"

        fun get(context: Context, id: Int) : Title? {
            val helper = SqliteHelper(context)
            val queryResult = helper.retrieveFields(TABLE_NAME, "where $ID = $id", ID, TITLE, PROTECTED)
            if (queryResult.isNotEmpty()) {
                val row = queryResult[0]
                return Title(row[ID] as Int, row[TITLE] as String, row[PROTECTED] as Int != 0)
            }
            return null
        }
        fun create (context: Context, title: String, isProtected: Boolean) : Title? {
            val helper = SqliteHelper(context)
            val isInserted = helper.insertRow(TABLE_NAME, hashMapOf(TITLE to "\"$title\"", PROTECTED to if (isProtected) "1" else "0"))
            if (isInserted) {
                val retrievedTitle = helper.retrieveFields(TABLE_NAME, "where $TITLE = \"$title\"", ID, TITLE, PROTECTED)
                if (retrievedTitle.size == 0) return null
                return Title(retrievedTitle[0][ID] as Int, retrievedTitle[0][TITLE] as String, retrievedTitle[0][PROTECTED] as Int != 0)
            } else {
                return null
            }
        }
        fun getAll(context: Context) : Array<Title> {
            val helper = SqliteHelper(context)
            val queryResult = helper.retrieveFields(TABLE_NAME, null, ID, TITLE, PROTECTED)
            return Array(queryResult.size, { i ->
                val row = queryResult[i]
                Title(row[ID] as Int, row[TITLE] as String, row[PROTECTED] as Int != 0)
            })
        }
    }



}