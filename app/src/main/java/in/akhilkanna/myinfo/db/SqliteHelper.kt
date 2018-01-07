package `in`.akhilkanna.myinfo.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SqliteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "info.db"

        class Title{
            companion object {
                val TABLE_NAME = "title"
                val ID = "id"
                val TITLE = "title"
                val PROTECTED = "protected"
            }
        }

        class Item{
            companion object {
                val TABLE_NAME = "item"
                val ID = "id"
                val TITLE_ID = "title_id"
                val KEY = "key"
                val VALUE = "value"
                val HIDDEN = "hidden"
            }
        }

        class Media{
            companion object {
                val TABLE_NAME = "media"
                val ITEM_ID = "item_id"
                val MEDIA = "media"
            }
        }
    }


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table ${Title.TABLE_NAME} (${Title.ID} int primary key, ${Title.TITLE} varchar unique, ${Title.PROTECTED} int default 0)")
        db.execSQL("create table ${Item.TABLE_NAME} ( ${Item.ID} int, ${Item.TITLE_ID} int references ${Title.TABLE_NAME}.${Title.ID}, ${Item.KEY} varchar, ${Item.VALUE} varchar, ${Item.HIDDEN} int default 0, primary key(${Item.ID}, ${Item.KEY}) )")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists ${Title.TABLE_NAME}")
        db.execSQL("drop table if exists ${Item.TABLE_NAME}")
        onCreate(db)
    }







}