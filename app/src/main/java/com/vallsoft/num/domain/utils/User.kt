package com.vallsoft.num.domain.utils

data class User(
        var phone: String,
        var operator: String? = "",
        var region: String? = "",
        var avatar: String? = "",
        var category: String? = "",
        var country: String? = "",
        var name: String? = "",
        var namegroup: String? = "",
        var address: String?= "") {
    constructor() : this("", "", "",
            "", "", "", "", "", "")

    fun isEmpty():Boolean {
        return operator.isNullOrEmpty() && region.isNullOrEmpty() && avatar.isNullOrEmpty() &&
                category.isNullOrEmpty() && country.isNullOrEmpty() && name.isNullOrEmpty() &&
                namegroup.isNullOrEmpty() && address.isNullOrEmpty()
    }
}