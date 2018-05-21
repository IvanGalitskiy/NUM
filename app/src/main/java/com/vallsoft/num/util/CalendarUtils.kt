package com.vallsoft.num.util

import java.util.*

fun addSevenDays(date:Date):Long{
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.DATE,7)
    //calendar.add(Calendar.SECOND,20)
    return calendar.time.time
}