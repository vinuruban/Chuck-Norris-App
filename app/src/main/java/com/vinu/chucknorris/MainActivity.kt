package com.vinu.chucknorris

import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() {

    /** Jokes will be logged here */
    private var jokeList: ArrayList<String> = ArrayList()

    /** Listview displays the jokes */
    private var listView: ListView? = null

    /** Adapter for the list of jokes */
    private var adapter: ArrayAdapter<String>? = null

    /** Search for a specific joke */
    private var editTextBox: EditText? = null

    /**
     * 1) URL to fetch 6 random jokes excluding the explicit ones
     * 2) URL to search for a specific joke
     * Both URLs escapes specific characters
     * */
    private val url = "http://api.icndb.com/jokes/"
    private val randomUrl = "random/6?exclude=[explicit]"
    private val escapeUrl = "escape=javascript"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Setup attributes */
        val refreshButton = findViewById<View>(R.id.refreshButton) as Button
        val searchButton = findViewById<View>(R.id.searchButton) as Button
        editTextBox = findViewById<View>(R.id.editText) as EditText

        /** Loads 6 random jokes */
        refreshJokeList(0)

        /** When the refresh button is clicked */
        refreshButton.setOnClickListener {

            /** Close keyboard */
            closeKeyboard()

            refreshJokeList(0)
        }

        /** When the search button is clicked */
        searchButton.setOnClickListener {

            /** Close keyboard */
            closeKeyboard()

            /** if the edit text box is empty, display error message */
            if (editTextBox!!.text.isEmpty()) {
                Toast.makeText(this, getString(R.string.enter_ID), Toast.LENGTH_SHORT).show()
            }
            refreshJokeList(1)
        }
    }

    /** To let keyboard disappear when clicked outside  */
    fun closeKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    private fun refreshJokeList(digit: Int) {

        /** Setup list view */
        listView = findViewById(R.id.listView)

        /** Setup the adapter */
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, jokeList)

        /** Set the adapter on the {@link ListView}
         * so the list can be populated in the user interface
         */
        listView?.adapter = adapter

        /** a reference to the ConnectivityManager to check the state of the network connectivity */
        val connMgr =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        /** Get details on the currently active default data network */
        val networkInfo = connMgr.activeNetworkInfo

        /** If there is a network connection, fetch data */
        if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
            // Start the AsyncTask to fetch the data
            val task = JokeAsyncTask()

            /** Url depends on the button that was clicked */
            if (digit == 0) {
                task.execute("$url$randomUrl&$escapeUrl")
            } else {
                var text = editTextBox?.text
                task.execute("$url$text?$escapeUrl")
            }

        } else {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    /** To perform the HTTP request for the data and process the response in the background thread */
    inner class JokeAsyncTask :
        AsyncTask<String?, Void?, ArrayList<String>?>() {

        /** Triggers methods in QueryUtils for JSON parsing */
        override fun doInBackground(vararg urls: String?): ArrayList<String>? {
            /** Don't perform the request if there are no URLs, or the first URL is null. */
            return if (urls.size < 1 || urls[0] == null) {
                null
            } else urls[0]?.let { QueryUtils.fetchData(it) }
        }


        /** Post JSON Parsing */
        override fun onPostExecute(jokeObjects: ArrayList<String>?) {

            /** Clear the adapter of previous the data */
            adapter?.clear()

            val button = findViewById<View>(R.id.refreshButton) as Button

            /** Update the list view with the joke responses */
            if (jokeObjects != null && !jokeObjects.isEmpty()) {
                adapter?.addAll(jokeObjects)
                button.text = getString(R.string.button_refresh)
            }
        }

    }

}