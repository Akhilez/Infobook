package `in`.akhilkanna.myinfo.dataStructures

import `in`.akhilkanna.myinfo.db.SqliteHelper
import android.content.Context

class Title(id: Int, var title: String, var isProtected: Boolean) : Info(id) {

    override fun commit(context: Context?): Boolean {
        return SqliteHelper(context!!).updateRow(
                TABLE_NAME,
                hashMapOf(TITLE to "\"" + title+ "\"", PROTECTED to (if (isProtected) 1 else 0).toString()),
                ID, id.toString()
        )
    }

    override fun delete(context: Context?): Boolean {
        return SqliteHelper(context!!).deleteRow(TABLE_NAME, ID, id.toString())
    }

    override fun toString(): String {
        return title
    }

    override fun equals(other: Any?): Boolean {
        return title == (other as Title).title
    }

    override fun hashCode(): Int {
        return title.hashCode()
    }

    companion object {
        const val TABLE_NAME = "title"
        const val ID = "id"
        const val TITLE = "title"
        const val PROTECTED = "protected"
        const val DATABASE_NAME = "info"

        fun get(context: Context?, id: Int): Title? {
            val helper = SqliteHelper(context!!)
            val queryResult = helper.retrieveFields(TABLE_NAME, "where $ID = $id", ID, TITLE, PROTECTED)
            if (queryResult.isNotEmpty()) {
                val row = queryResult[0]
                return Title(row[ID] as Int, row[TITLE] as String, row[PROTECTED] as Int != 0)
            }
            return null
        }

        fun create(context: Context?, title: String, isProtected: Boolean): Title? {
            val helper = SqliteHelper(context!!)
            val isInserted = helper.insertRow(TABLE_NAME, hashMapOf(TITLE to "\"$title\"", PROTECTED to if (isProtected) "1" else "0"))
            if (isInserted) {
                val retrievedTitle = helper.retrieveFields(TABLE_NAME, "where $TITLE = \"$title\"", ID, TITLE, PROTECTED)
                if (retrievedTitle.size == 0) return null
                return Title(retrievedTitle[0][ID] as Int, retrievedTitle[0][TITLE] as String, retrievedTitle[0][PROTECTED] as Int != 0)
            } else {
                return null
            }
        }

        fun getAll(context: Context): Array<Title> {
            val helper = SqliteHelper(context)
            val queryResult = helper.retrieveFields(TABLE_NAME, null, ID, TITLE, PROTECTED)
            return Array(queryResult.size, { i ->
                val row = queryResult[i]
                Title(row[ID] as Int, row[TITLE] as String, row[PROTECTED] as Int != 0)
            })
        }
    }


}