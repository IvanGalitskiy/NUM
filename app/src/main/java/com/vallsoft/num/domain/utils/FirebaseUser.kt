package com.vallsoft.num.domain.utils

data class FirebaseUser(val gr0_address: String?,
                        val gr0_avatar: String?,
                        val gr0_category: String?,
                        val gr0_country: String?,
                        val gr0_name: String?,
                        val gr0_namegroup: String?,
                        val gr0_Operator: String?,
                        val gr0_phone: String?,
                        val gr0_Region: String?) {
    constructor(user: User) : this(user.address, user.avatar, user.category, user.country, user.name,
            user.namegroup, user.operator, user.phone, user.region)
}