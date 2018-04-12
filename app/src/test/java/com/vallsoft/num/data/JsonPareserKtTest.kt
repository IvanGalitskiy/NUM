package com.vallsoft.num.data

import org.junit.Test

import org.junit.Assert.*

class JsonPareserKtTest {

    @Test
    fun parseJson() {
       assertEquals("gr0_", SCHEMA_PREFIX.format(0))
    }
}