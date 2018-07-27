package com.vallsoft.num.data

import com.vallsoft.num.domain.utils.User

class UserCache private constructor() {

    companion object {
        val instance by lazy {
            UserCache()
        }
    }

    var user: User? = null

}