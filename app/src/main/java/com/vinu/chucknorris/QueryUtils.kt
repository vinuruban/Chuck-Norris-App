package com.vinu.chucknorris

import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import java.util.*

object QueryUtils {

    /** Tag for the log messages  */
    private val LOG_TAG = QueryUtils::class.java.simpleName

    /** To extract data depending on request */
    private var searchActive = false

    /**
     * Return a list of [ArrayList] objects.
     */
    fun fetchData(requestUrl: String): ArrayList<String>? {

        /** Set searchActive to true if the Url doesn't contain "random" */
        searchActive = !requestUrl.contains("random")

        /** Create URL object */
        val url = createUrl(requestUrl)

        /** Perform HTTP request to the URL and receive a JSON response back */
        var jsonResponse: String? = null
        try {
            jsonResponse = makeHttpRequest(url)
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e)
        }

        /** Extract relevant fields from the JSON response */
        return extractFeatureFromJson(jsonResponse)
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private fun createUrl(stringUrl: String): URL? {
        var url: URL? = null
        try {
            url = URL(stringUrl)
        } catch (e: MalformedURLException) {
            Log.e(LOG_TAG, "Error with creating URL ", e)
        }
        return url
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    @Throws(IOException::class)
    private fun makeHttpRequest(url: URL?): String {
        var jsonResponse = ""

        /** If the URL is null, then return early. */
        if (url == null) {
            return jsonResponse
        }
        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.readTimeout = 10000
            urlConnection!!.connectTimeout = 15000
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            /** If the request was successful (response code 200),
             * then read the input stream and parse the response.
             */
            if (urlConnection.responseCode == 200) {
                inputStream = urlConnection.inputStream
                jsonResponse = readFromStream(inputStream)
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.responseCode)
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Problem retrieving the joke JSON results.", e)
        } finally {
            urlConnection?.disconnect()
            inputStream?.close()
        }
        return jsonResponse
    }

    /**
     * Convert the [InputStream] into a String which contains the
     * whole JSON response from the server.
     */
    @Throws(IOException::class)
    private fun readFromStream(inputStream: InputStream?): String {
        val output = StringBuilder()
        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }
        }
        return output.toString()
    }

    /**
     * Return a list of [JokeObject] objects that has been built up from
     * parsing the given JSON response.
     */
    fun extractFeatureFromJson(jokeJSON: String?): ArrayList<String>? {

        /** If the JSON string is empty or null, then return early. */
        if (TextUtils.isEmpty(jokeJSON)) {
            return null
        }

        /** An empty ArrayList to add the jokes in */
        val jokes = ArrayList<String>()

        /** Try to parse the JSON response string. If there's a problem with the way the JSON
         * is formatted, a JSONException exception object will be thrown.
         * Catch the exception so the app doesn't crash, and print the error message to the logs.
         */
        try {
            /** Create a JSONObject from the JSON response string */
            val jsonObj = JSONObject(jokeJSON)

            /** To know if a joke was found when searching */
            val type = jsonObj.getString("type")

            if (type == "NoSuchQuoteException") {
                /** If no jokes are found from the request */
                jokes.add("No jokes found.")
            }
            else if(searchActive) {

                /** If you're searching for a specific joke... */

                val valueObj = jsonObj.getJSONObject("value")
                val jokeString = valueObj.getString("joke")
                val categoriesArray = valueObj.getJSONArray("categories")

                /** if the joke is explicit, do not show the joke */
                if (categoriesArray.length() != 0) {
                    if (categoriesArray.get(0) == "explicit") {
                        jokes.add("Explicit jokes cannot be seen, please try another joke ID.")
                    }
                }
                else {
                    jokes.add(jokeString)
                }

            } else {

                val valueArray = jsonObj.getJSONArray("value")

                /** Loop through all of value Array's elements */
                for (i in 0 until valueArray.length()) {
                    val currentJoke = valueArray.getJSONObject(i)
                    val jokeString = currentJoke.getString("joke")
                    jokes.add(jokeString)
                }
            }
        } catch (e: JSONException) {
            /** If an error is thrown when executing any of the above statements in the "try" block,
             * catch the exception here, so the app doesn't crash. Print a log message
             * with the message from the exception.
             */

            Log.e(LOG_TAG, "Problem parsing the joke JSON results", e)
        }

        /** Return the list of jokes */
        return jokes
    }
}