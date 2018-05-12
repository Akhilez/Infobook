package `in`.akhilkanna.myinfo.db

import `in`.akhilkanna.myinfo.dataStructures.Item
import `in`.akhilkanna.myinfo.dataStructures.Title
import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.content.Context
import android.os.AsyncTask


@Dao
interface TitleDao {

    @get:Query("SELECT * from ${Title.TABLE_NAME}")
    val allTitles: LiveData<List<TitleEnt>>

    @Query("SELECT * FROM ${Title.TABLE_NAME} WHERE ${Title.ID} = :titleId LIMIT 1")
    fun getTitle(titleId: Int): LiveData<TitleEnt>

    @Update
    fun update(title: TitleEnt)

    @Insert
    fun insert(title: TitleEnt)

    @Delete
    fun delete(title: TitleEnt)
}

@Dao
interface ItemDao {
    @Query("SELECT * from ${Item.TABLE_NAME} where ${Item.TITLE_ID} = :titleId")
    fun getItems(titleId: Int): LiveData<List<ItemEnt>>

    @Update
    fun update(item: ItemEnt)

    @Insert
    fun insert(item: ItemEnt)

    @Delete
    fun delete(item: ItemEnt)
}

@Database(entities = [TitleEnt::class, ItemEnt::class], version = 1)
abstract class InfoRoomDatabase : RoomDatabase() {
    abstract fun titleDao(): TitleDao
    abstract fun itemDao(): ItemDao

    companion object {
        private var INSTANCE: InfoRoomDatabase? = null

        fun getDatabase(context: Context): InfoRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(InfoRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        // Create database here
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                InfoRoomDatabase::class.java, Title.DATABASE_NAME)
                                .build()
                    }
                }
            }
            return INSTANCE
        }
    }

}

class InfoRepository(application: Application) {
    private var titleDao: TitleDao?
    private var allTitles: LiveData<List<TitleEnt>>?

    init {
        val db = InfoRoomDatabase.getDatabase(application)
        titleDao = db?.titleDao()
        allTitles = titleDao?.allTitles
    }

    fun getAllTitles(): LiveData<List<TitleEnt>>? {
        return allTitles
    }

    fun insert(title: TitleEnt) {
        titleDao?.let { InsertAsyncTask(it).execute(title) }
    }

    private class InsertAsyncTask internal constructor(private val mAsyncTaskDao: TitleDao) : AsyncTask<TitleEnt, Void, Void>() {

        override fun doInBackground(vararg params: TitleEnt): Void? {
            mAsyncTaskDao.insert(params[0])
            return null
        }
    }

}