package `in`.akhilkanna.myinfo.dataStructures

class Item (val id: Int, val title: Title, val key: String, val value: String, val hidden: Boolean) {

    constructor(id: Int, titleId: Int, key: String, value: String, hidden: Boolean) : this(id, Title.get(titleId), key, value, hidden)

    companion object {
        fun get(id: Int) : Item{
            return Item(id, 1, "key", "value", false)
        }

        fun getItems(title: Title): Array<Item>{
            return arrayOf(
                    Item(1, title, "Account No.", "20212345353", false),
                    Item(2, title, "Debit Card No.", "9234 2345 4546 3453", false),
                    Item(2, title, "Debit Card Expiry date", "04/32", false),
                    Item(2, title, "Debit Card Name", "A Bcde", false),
                    Item(2, title, "Debit Card ATM Pin", "4352", true),
                    Item(2, title, "Debit Card CVC", "234", true)
            )
        }
    }

}