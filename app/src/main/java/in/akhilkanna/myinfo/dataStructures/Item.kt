package `in`.akhilkanna.myinfo.dataStructures

class Item (val id: Int, val title: Title, val key: String, val value: String, val hidden: Boolean) {

    constructor(id: Int, titleId: Int, key: String, value: String, hidden: Boolean) : this(id, Title.get(titleId), key, value, hidden)

    companion object {
        fun get(id: Int) : Item{
            return Item(id, 1, "key", "value", false)
        }
    }

}