package `in`.akhilkanna.myinfo.db

import `in`.akhilkanna.myinfo.dataStructures.Item
import `in`.akhilkanna.myinfo.dataStructures.Title
import android.content.Context
import android.database.Cursor.*
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.sql.SQLException

class SqliteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "info.db"

        class Media{
            companion object {
                val TABLE_NAME = "media"
                val ITEM_ID = "item_id"
                val MEDIA = "media"
            }
        }
    }


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table ${Title.TABLE_NAME} (${Title.ID} integer primary key autoincrement, ${Title.TITLE} varchar unique, ${Title.PROTECTED} int default 0)")
        db.execSQL("create table ${Item.TABLE_NAME} ( ${Item.ID} integer primary key autoincrement, ${Item.TITLE_ID} int references ${Title.TABLE_NAME}.${Title.ID}, ${Item.KEY} varchar, ${Item.VALUE} varchar, ${Item.HIDDEN} int default 0, primary key(${Item.ID}, ${Item.KEY}) )")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists ${Title.TABLE_NAME}")
        db.execSQL("drop table if exists ${Item.TABLE_NAME}")
        onCreate(db)
    }


    // ----- MY METHODS ----------


    fun retrieveFields (tableName: String, clause: String?, vararg fields: String): ArrayList<HashMap<String, Any?>> {
        val resultTable = ArrayList<HashMap<String, Any?>>()
        val db = readableDatabase
        val query = "select " + fields.joinToString() + " from " + tableName + " " + (clause ?: "")
        val result = db.rawQuery(query, null)
        result.moveToFirst()
        while (!result.isAfterLast) {
            val row = HashMap<String, Any?>()
            for (field in fields) {
                var value: Any? = null
                val columnIndex = result.getColumnIndex(field)
                when (result.getType(columnIndex)) {
                    FIELD_TYPE_INTEGER -> value = result.getInt(columnIndex)
                    FIELD_TYPE_FLOAT -> value = result.getFloat(columnIndex)
                    FIELD_TYPE_STRING -> value = result.getString(columnIndex)
                    FIELD_TYPE_BLOB -> value = result.getBlob(columnIndex)
                }
                row.put(field, value)
            }
            resultTable.add(row)
        }
        result.close()
        db.close()
        return resultTable
    }

    fun insertRow (tableName: String, fieldValuePairs: HashMap<String, String>): Boolean {
        val db = writableDatabase
        return try {
            db.execSQL("insert into $tableName (${fieldValuePairs.keys.joinToString()}) values (${fieldValuePairs.values.joinToString()})")
            true
        } catch (e: SQLException) {
            false
        } finally {
            db.close()
        }
    }

    //fun updateRow (tableName: String, )
    // TODO update title set title = "ABC", protected = 1 where id = 2;

}