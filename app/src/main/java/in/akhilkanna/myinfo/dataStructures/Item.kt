package `in`.akhilkanna.myinfo.dataStructures

import `in`.akhilkanna.myinfo.db.SqliteHelper
import android.content.Context

class Item (val id: Int, val title: Title, val key: String, val value: String, val hidden: Boolean) {

    constructor(id: Int, titleId: Int, key: String, value: String, hidden: Boolean) : this(id, Title.get(titleId), key, value, hidden)

    companion object {
        val TABLE_NAME = "item"
        val ID = "id"
        val TITLE_ID = "title_id"
        val KEY = "key"
        val VALUE = "value"
        val HIDDEN = "hidden"

        fun getItems(context: Context, title: Title): Array<Item>{
            val helper = SqliteHelper(context)
            val queryResult = helper.retrieveFields(TABLE_NAME, "where $TITLE_ID = ${title.id}", ID, KEY, VALUE, HIDDEN)
            return Array(queryResult.size, { i ->
                val row = queryResult[i]
                Item(row[ID] as Int, title, row[KEY] as String, row[VALUE] as String, row[HIDDEN] as Int != 0)
            })
        }

        fun create(context: Context, title: Title, key: String, value: String, hidden: Boolean): Item? {
            val helper = SqliteHelper(context)
            val isInserted = helper.insertRow(TABLE_NAME, hashMapOf(TITLE_ID to title.id.toString(), KEY to key, VALUE to value, HIDDEN to if (hidden) "1" else "0"))
            if (isInserted) {
                val retrievedItem = helper.retrieveFields(TABLE_NAME, "where $TITLE_ID = ${title.id} and $KEY = \"$key\"", ID)
                if (retrievedItem.size == 0) return null
                return Item(retrievedItem[0][ID] as Int, title, key, value, hidden)
            } else return null
        }

    }

}