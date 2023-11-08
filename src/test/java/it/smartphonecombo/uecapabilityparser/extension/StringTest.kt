package it.smartphonecombo.uecapabilityparser.extension

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class StringTest {

    @Test
    fun commonPrefixEmptyList() {
        val input = listOf<String>()
        val oracle = ""
        val ignoreCase = false
        testCommonPrefix(input, oracle, ignoreCase)
    }

    @Test
    fun commonPrefixListOfEmpty() {
        val input = listOf("", "", "")
        val oracle = ""
        val ignoreCase = false
        testCommonPrefix(input, oracle, ignoreCase)
    }

    @Test
    fun commonPrefixListWithEmpty() {
        val input = listOf("abc", "cdfe", "z", "")
        val oracle = ""
        val ignoreCase = false
        testCommonPrefix(input, oracle, ignoreCase)
    }

    @Test
    fun commonPrefixOneString() {
        val input = listOf("abc")
        val oracle = "abc"
        val ignoreCase = false
        testCommonPrefix(input, oracle, ignoreCase)
    }

    @Test
    fun commonPrefixTwoStringsNoCommon() {
        val input = listOf("abc", "ABC")
        val oracle = ""
        val ignoreCase = false
        testCommonPrefix(input, oracle, ignoreCase)
    }

    @Test
    fun commonPrefixTwoStringsEquals() {
        val input = listOf("abc", "ABC")
        val oracle = "abc"
        val ignoreCase = true
        testCommonPrefix(input, oracle, ignoreCase)
    }

    @Test
    fun commonPrefixTwoStringsWithCommon() {
        val input = listOf("abc", "abcDEF")
        val oracle = "abc"
        val ignoreCase = false
        testCommonPrefix(input, oracle, ignoreCase)
    }

    @Test
    fun commonPrefixThreeStringsNoCommon() {
        val input = listOf("abc", "ABC", "def")
        val oracle = ""
        val ignoreCase = false
        testCommonPrefix(input, oracle, ignoreCase)
    }

    @Test
    fun commonPrefixThreeStringsEquals() {
        val input = listOf("abc", "ABC", "abc")
        val oracle = "abc"
        val ignoreCase = true
        testCommonPrefix(input, oracle, ignoreCase)
    }

    @Test
    fun commonPrefixThreeStringsWithCommon() {
        val input = listOf("abc", "abcDEF", "abCdef")
        val oracle = "ab"
        val ignoreCase = false
        testCommonPrefix(input, oracle, ignoreCase)
    }

    private fun testCommonPrefix(input: List<String>, oracle: String, ignoreCase: Boolean) {
        val commonPrefix = input.commonPrefix(ignoreCase)
        Assertions.assertEquals(oracle, commonPrefix)
    }
}
