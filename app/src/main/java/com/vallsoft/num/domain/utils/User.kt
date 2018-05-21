package com.vallsoft.num.domain.utils

data class User(
        var phone: String,
        var operator: String? = null,
        var region: String? = null,
        var avatar: String? = null,
        var category: String? = null,
        var country: String? = null,
        var name: String? = null,
        var namegroup: String? = null,
        var address: String?= null) {
    constructor() : this("", "", "",
            "", "", "", "", "", "")

    fun isEmpty():Boolean {
        return operator.isNullOrEmpty() && region.isNullOrEmpty() && avatar.isNullOrEmpty() &&
                category.isNullOrEmpty() && country.isNullOrEmpty() && name.isNullOrEmpty() &&
                namegroup.isNullOrEmpty() && address.isNullOrEmpty()
    }
}