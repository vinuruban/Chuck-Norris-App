package com.vinu.chucknorris

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.ArrayList

class QueryUtilsTest {

    private var input: String? = null
    private var expected: String? = null

    @Before
    @Throws(Exception::class)
    fun setup() {
        input = ""
        expected = ""
    }

    /** When the correct request is made, the correct joke should be returned */
    @Test
    @Throws(Exception::class)
    fun fetchData_correctRequest_correctJokeReturned() {
        input = "http://api.icndb.com/jokes/4?escape=javascript"
        expected = "If you ask Chuck Norris what time it is, he always answers \"Two seconds till\". After you ask \"Two seconds to what?\", he roundhouse kicks you in the face."
        findJoke(input!!, expected!!)
    }

    /** When a request is made for explicit joke, the joke shouldn't be returned */
    @Test
    @Throws(Exception::class)
    fun fetchData_explicitJokeRequest_jokeNotReturned() {
        input = "http://api.icndb.com/jokes/1?escape=javascript"
        expected = "Explicit jokes cannot be seen, please try another joke ID."
        findJoke(input!!, expected!!)
    }

    /** When a request is made with an non-existent joke ID, the joke shouldn't be returned */
    @Test
    @Throws(Exception::class)
    fun fetchData_nonExistentJokeRequest_jokeNotReturned() {
        input = "http://api.icndb.com/jokes/4444?escape=javascript"
        expected = "No jokes found."
        findJoke(input!!, expected!!)
    }

    /** When a request is made with an non-existent joke ID (lower bound), the joke shouldn't be returned */
    @Test
    @Throws(Exception::class)
    fun fetchData_nonExistentJokeRequestLowerBound_jokeNotReturned() {
        input = "http://api.icndb.com/jokes/0?escape=javascript"
        expected = "No jokes found."
        findJoke(input!!, expected!!)
    }

    private fun findJoke(input: String, expected: String) {

        var expectedJokeList = ArrayList<String>()
        expectedJokeList.add(expected)

        var actualJokeList = QueryUtils.fetchData(input)!!

        assertArrayEquals(expectedJokeList.toArray(), actualJokeList.toArray())

    }

}