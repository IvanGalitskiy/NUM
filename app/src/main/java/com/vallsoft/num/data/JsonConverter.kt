package com.vallsoft.num.data

import com.vallsoft.num.domain.utils.User
import org.json.JSONException
import org.json.JSONObject

const val SCHEMA_PREFIX = "gr%d_"
const val SCHEMA_AVATAR = "avatar"
const val SCHEMA_NAME = "name"
const val SCHEMA_CATEGORY = "category"
const val SCHEMA_OPERATOR = "Operator"
const val SCHEMA_REGION = "Region"
const val SCHEMA_COUNTRY = "country"
const val SCHEMA_NAMEGROUP = "namegroup"
const val SCHEMA_ADDRESS = "address"

fun parseJson(json:String):User?{
    val user:User=User()
    var haveName=false
    var haveAvatar= false
    var haveCategory= false
    var haveOperator= false
    var haveRegion= false
    var haveCountry= false
    var haveNameGroup= false
    var haveAddress= false

    if (!json.isEmpty()) {
        try {
            val jsonObject = JSONObject(json)
            for (i in 0..10) {
                val prefix = SCHEMA_PREFIX.format(i)
                if (!haveName && jsonObject.has(prefix + SCHEMA_NAME)) {
                    val name = jsonObject.getString(prefix + SCHEMA_NAME)
                    if (name != null) {
                        user.name = name
                        haveName = true
                    }
                }
                if (!haveAvatar && jsonObject.has(prefix + SCHEMA_AVATAR)) {
                    val avatar = jsonObject.getString(prefix + SCHEMA_AVATAR)
                    if (avatar != null) {
                        user.avatar = avatar
                        haveAvatar = true
                    }
                }
                if (!haveCategory && jsonObject.has(prefix + SCHEMA_CATEGORY)) {
                    val category = jsonObject.getString(prefix + SCHEMA_CATEGORY)
                    if (category != null) {
                        user.category = category
                        haveCategory = true
                    }
                }
                if (!haveOperator && jsonObject.has(prefix + SCHEMA_OPERATOR)) {
                    val operator = jsonObject.getString(prefix + SCHEMA_OPERATOR)
                    if (operator != null) {
                        user.operator = operator
                        haveOperator = true
                    }
                }
                if (!haveRegion && jsonObject.has(prefix + SCHEMA_REGION)) {
                    val region = jsonObject.getString(prefix + SCHEMA_REGION)
                    if (region != null) {
                        user.region = region
                        haveRegion = true
                    }
                }
                if (!haveCountry && jsonObject.has(prefix + SCHEMA_COUNTRY)) {
                    val country = jsonObject.getString(prefix + SCHEMA_COUNTRY)
                    if (country != null) {
                        user.country = country
                        haveCountry = true
                    }
                }
                if (!haveNameGroup && jsonObject.has(prefix + SCHEMA_NAMEGROUP)) {
                    val namegroup = jsonObject.getString(prefix + SCHEMA_NAMEGROUP)
                    if (namegroup != null) {
                        user.namegroup = namegroup
                        haveNameGroup = true
                    }
                }
                if (!haveAddress && jsonObject.has(prefix + SCHEMA_ADDRESS)) {
                    val address = jsonObject.getString(prefix + SCHEMA_ADDRESS)
                    if (address != null) {
                        user.address = address
                        haveAddress = true
                    }
                }
            }
        } catch (ex:JSONException){
        }
    }

    return user
}
