package com.ta.wafafood.Model

class Barang {
    var id: String? = null
    var name:String? = null
    var harga:String? = null
    var avatar:String? = null
    var jumlah: Long? = null

    constructor(id: String?, name: String?, harga: String?, jumlah: Long?,  avatar: String?) {
        this.id = id
        this.name = name
        this.harga = harga
        this.jumlah = jumlah
        this.avatar = avatar
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Barang) return false

        if (id != other.id) return false
        if (avatar != other.avatar) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (avatar?.hashCode() ?: 0)

        return result
    }

}