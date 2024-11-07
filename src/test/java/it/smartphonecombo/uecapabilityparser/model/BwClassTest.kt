package it.smartphonecombo.uecapabilityparser.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class BwClassTest {

    @Test
    fun validFromString() {
        val bwc = "A"
        val actual = BwClass.valueOf(bwc).toString()
        val expected = "A"

        assertEquals(expected, actual)
    }

    @Test
    fun invalidFromString() {
        val bwc = "#@*"
        val actual = BwClass.valueOf(bwc)
        val expected = BwClass.INVALID

        assertEquals(expected, actual)
    }

    @Test
    fun noneFromString() {
        val bwc = ""
        val actual = BwClass.valueOf(bwc)
        val expected = BwClass.NONE

        assertEquals(expected, actual)
    }

    @Test
    fun noneFromString2() {
        val bwc = null
        val actual = BwClass.valueOf(bwc)
        val expected = BwClass.NONE

        assertEquals(expected, actual)
    }

    @Test
    fun validFromIndex() {
        val bwc = 4
        val actual = BwClass.valueOf(bwc).toString()
        val expected = "D"

        assertEquals(expected, actual)
    }

    @Test
    fun validFromIndex2() {
        val bwc = 6
        val actual = BwClass.valueOf(bwc).toString()
        val expected = "F"

        assertEquals(expected, actual)
    }

    @Test
    fun invalidFromIndex() {
        val bwc = 100
        val actual = BwClass.valueOf(bwc)
        val expected = BwClass.INVALID

        assertEquals(expected, actual)
    }

    @Test
    fun invalidFromIndex2() {
        val bwc = -100
        val actual = BwClass.valueOf(bwc)
        val expected = BwClass.INVALID

        assertEquals(expected, actual)
    }

    @Test
    fun noneFromIndex() {
        val bwc = 0
        val actual = BwClass.valueOf(bwc)
        val expected = BwClass.NONE

        assertEquals(expected, actual)
    }

    @Test
    fun validFromMtkIndex() {
        val bwc = 0
        val actual = BwClass.valueOfMtkIndex(bwc).toString()
        val expected = "A"

        assertEquals(expected, actual)
    }

    @Test
    fun validFromMtkIndex2() {
        val bwc = 5
        val actual = BwClass.valueOfMtkIndex(bwc).toString()
        val expected = "F"

        assertEquals(expected, actual)
    }

    @Test
    fun invalidFromMtkIndex() {
        val bwc = 7
        val actual = BwClass.valueOfMtkIndex(bwc)
        val expected = BwClass.INVALID

        assertEquals(expected, actual)
    }

    @Test
    fun invalidFromMtkIndex2() {
        val bwc = -7
        val actual = BwClass.valueOfMtkIndex(bwc)
        val expected = BwClass.INVALID

        assertEquals(expected, actual)
    }

    @Test
    fun noneFromMtkIndex() {
        val bwc = 6
        val actual = BwClass.valueOfMtkIndex(bwc)
        val expected = BwClass.NONE

        assertEquals(expected, actual)
    }
}
