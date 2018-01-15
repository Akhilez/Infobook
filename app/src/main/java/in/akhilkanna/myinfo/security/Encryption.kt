package `in`.akhilkanna.myinfo.security

class Encryption (value: String) {
    val hash = getHash(value)

    private fun getHash(value: String): String{
        return value
    }

    fun validate(hashValue: String): Boolean {
        return hash == hashValue
    }
}