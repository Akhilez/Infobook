package `in`.akhilkanna.myinfo.dataStructures

class Title (val id: Int, val title: String, val isProtected: Boolean) {

    companion object {
        fun get(id: Int) : Title{
            return Title(id, "test", false)
        }
        fun nextId () : Int {
            return 0
        }
    }



}