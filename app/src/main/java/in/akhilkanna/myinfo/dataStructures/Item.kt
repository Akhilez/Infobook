package `in`.akhilkanna.myinfo.dataStructures

import `in`.akhilkanna.myinfo.db.SqliteHelper
import android.content.Context

class Item(val id: Int, val title: Title, var key: String, var value: String, var hidden: Boolean) {

    fun commit(context: Context) = SqliteHelper(context).updateRow(
            TABLE_NAME,
            hashMapOf(DESC to "\"" + key + "\"", VALUE to "\"" + value + "\"", HIDDEN to (if (hidden) 1 else 0).toString()),
            ID, id.toString()
    )

    fun delete(context: Context) = SqliteHelper(context).deleteRow(TABLE_NAME, ID, id.toString())

    companion object {
        const val TABLE_NAME = "item"
        const val ID = "id"
        const val TITLE_ID = "title_id"
        const val DESC = "desc"
        const val VALUE = "value"
        const val HIDDEN = "hidden"

        fun getItems(context: Context, title: Title): Array<Item> {
            val helper = SqliteHelper(context)
            val queryResult = helper.retrieveFields(TABLE_NAME, "where $TITLE_ID = ${title.id}", ID, DESC, VALUE, HIDDEN)
            return Array(queryResult.size, { i ->
                val row = queryResult[i]
                Item(row[ID] as Int, title, row[DESC] as String, row[VALUE] as String, row[HIDDEN] as Int != 0)
            })
        }

        fun get(context: Context, itemId: Int): Item? {
            val helper = SqliteHelper(context)
            val queryResult = helper.retrieveFields(TABLE_NAME, "where $ID = $itemId", ID, TITLE_ID, DESC, VALUE, HIDDEN)
            if (queryResult.isNotEmpty()) {
                val row = queryResult[0]
                return Item(row[ID] as Int, Title.get(context, row[TITLE_ID] as Int)!!, row[DESC] as String, row[VALUE] as String, row[HIDDEN] as Int != 0)
            }
            return null
        }

        fun create(context: Context, title: Title, key: String, value: String, hidden: Boolean): Item? {
            val helper = SqliteHelper(context)
            val isInserted = helper.insertRow(TABLE_NAME, hashMapOf(TITLE_ID to title.id.toString(), DESC to "\"$key\"", VALUE to "\"$value\"", HIDDEN to if (hidden) "1" else "0"))
            if (isInserted) {
                val retrievedItem = helper.retrieveFields(TABLE_NAME, "where $TITLE_ID = ${title.id} and $DESC = \"$key\"", ID)
                if (retrievedItem.size == 0) return null
                return Item(retrievedItem[0][ID] as Int, title, key, value, hidden)
            } else return null
        }

    }

}