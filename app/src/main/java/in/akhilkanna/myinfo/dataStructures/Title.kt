package `in`.akhilkanna.myinfo.dataStructures

class Title (val id: Int, val title: String, val isProtected: Boolean) {

    fun commit(): String?{
        return "Failed"
    }

    override fun toString(): String {
        return title
    }


    companion object {
        fun get(id: Int) : Title{
            return Title(id, "test", false)
        }
        fun nextId () : Int {
            return 0
        }
        fun getAll() : Array<Title> {
            return arrayOf(
                Title(1, "test1", false),
                Title(2, "test2", true)
            )
        }
    }



}