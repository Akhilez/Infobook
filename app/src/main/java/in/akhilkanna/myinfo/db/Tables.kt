package `in`.akhilkanna.myinfo.db

import `in`.akhilkanna.myinfo.dataStructures.Item
import `in`.akhilkanna.myinfo.dataStructures.Title
import android.arch.persistence.room.*
import android.support.annotation.NonNull


@Entity(tableName = Title.TABLE_NAME)
class TitleEnt(
        @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = Title.ID) val id: Int,
        @field:ColumnInfo(name = Title.TITLE) @field:NonNull val title: String, // Add UNIQUE=true
        @field:ColumnInfo(name = Title.PROTECTED) val isProtected: Boolean = false
)

@Entity(
        tableName = Item.TABLE_NAME,
        foreignKeys = (arrayOf(ForeignKey(entity = TitleEnt::class, parentColumns = arrayOf(Title.ID), childColumns = arrayOf(Item.TITLE_ID), onDelete = ForeignKey.CASCADE))),
        indices = [(Index(value = arrayOf(Item.ID, Item.DESC), unique = true))]
)
class ItemEnt(
        @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = Item.ID) val id: Int,
        @field:ColumnInfo(name = Item.TITLE_ID) @field:NonNull val title: Int,
        @field:ColumnInfo(name = Item.DESC) @field:NonNull val key: String,
        @field:ColumnInfo(name = Item.VALUE) @field:NonNull val value: String,
        @field:ColumnInfo(name = Item.HIDDEN) val hidden: Boolean = false
)