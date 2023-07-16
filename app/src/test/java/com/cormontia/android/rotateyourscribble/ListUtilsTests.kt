package com.cormontia.android.rotateyourscribble

import org.junit.Test

class ListUtilsTests {
    @Test
    fun multiple_subsequent_strings_are_removed() {
        val input = listOf("Alice", "Alice", "Alice", "Bob", "Alice", "Bob", "Bob")
        val expected = listOf("Alice", "Bob", "Alice", "Bob")

        val actual = input.removeSubsequentDoubles()

        assert(actual.size == expected.size)
        assert(actual[0] == "Alice")
        assert(actual[1] == "Bob")
        assert(actual[2] == "Alice")
        assert(actual[3] == "Bob")
    }

    @Test
    fun multiple_subsequent_integers_are_removed() {
        val input = listOf(1,2,2,3,3,3,4,4,4,4)
        val expected = listOf(1,2,3,4)

        val actual = input.removeSubsequentDoubles()

        assert(actual.size == expected.size)
        assert(actual[0] == 1)
        assert(actual[1] == 2)
        assert(actual[2] == 3)
        assert(actual[3] == 4)
    }
}

