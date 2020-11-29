package com.vinu.chucknorris

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    /** Jokes will be logged here */
    private var jokeList: ArrayList<String> = ArrayList()

    /** Listview displays the jokes */
    private var listView: ListView? = null

    /** Adapter for the list of jokes */
    private var adapter: ArrayAdapter<String>? = null

    private var editTextBox: EditText? = null

    /** URL to fetch 6 random jokes excluding the explicit ones */
    private val url = "http://api.icndb.com/jokes/"
    private val urlAttachment = "random/6?exclude=[explicit]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val refreshButton = findViewById<View>(R.id.refreshButton) as Button
        val searchButton = findViewById<View>(R.id.searchButton) as Button
        editTextBox = findViewById<View>(R.id.editText) as EditText

        refreshJokeList(0)

        refreshButton.setOnClickListener {
            refreshJokeList(0)
        }

        searchButton.setOnClickListener {
            if (editTextBox!!.text.isEmpty()) {
                Toast.makeText(this, getString(R.string.enter_ID), Toast.LENGTH_SHORT).show();
            }
            refreshJokeList(1)
        }
    }

    private fun refreshJokeList(digit: Int) {

        listView = findViewById(R.id.listView)

        /** Create a new adapter that takes an EMPTY list of jokes as input */
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, jokeList)

        /** Set the adapter on the {@link ListView}
         * so the list can be populated in the user interface
         */
        listView?.adapter = adapter

        // Get a reference to the ConnectivityManager to check state of network connectivity
        val connMgr =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        // Get details on the currently active default data network
        val networkInfo = connMgr.activeNetworkInfo

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
            // Start the AsyncTask to fetch the data
            val task = JokeAsyncTask()

            if (digit == 0) {
                task.execute(url + urlAttachment)
            } else {
                task.execute(url + editTextBox?.text)
            }

        } else {
//                // Otherwise, display error
//                // First, hide loading indicator so error message will be visible
//                loadingIndicator.visibility = View.GONE
//
//                // Update empty state with no connection error message
//                emptyStateTextView!!.setText(R.string.no_internet_connection)
        }
    }

    inner class JokeAsyncTask :
        AsyncTask<String?, Void?, ArrayList<String>?>() {

        var mainActivity = MainActivity().callingActivity

        override fun doInBackground(vararg urls: String?): ArrayList<String>? {
            // Don't perform the request if there are no URLs, or the first URL is null.
            return if (urls.size < 1 || urls[0] == null) {
                null
            } else urls[0]?.let { QueryUtils.fetchData(it) }

            // Perform the HTTP request for the data and process the response.
        }


        override fun onPostExecute(jokeObjects: ArrayList<String>?) {

            // Clear the adapter of previous the data
            adapter?.clear()

            val button = findViewById<View>(R.id.refreshButton) as Button

            // If there is a valid list of {@link Joke}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (jokeObjects != null && !jokeObjects.isEmpty()) {
                adapter?.addAll(jokeObjects)
                button.text = getString(R.string.button_refresh)
            }
        }
    }

}