package com.vinu.chucknorris

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() {

    /** Jokes will be logged here */
    private var jokeList: ArrayList<String> = ArrayList()

    /** Listview displays the jokes */
    private var listView: ListView? = null

    /** Adapter for the list of jokes */
    private var adapter: ArrayAdapter<String>? = null

    /** URL to fetch random jokes excluding the explicit ones */
    private val url = "http://api.icndb.com/jokes/random/"
    private val urlAttachment = "?exclude=[explicit]"
    private var finalUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refreshJokeList()

        val button = findViewById<View>(R.id.button) as Button

        button.setOnClickListener {
            refreshJokeList()
        }
    }

    private fun refreshJokeList() {
        finalUrl = url + "6" + urlAttachment

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
            task.execute(finalUrl)
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

            val button = findViewById<View>(R.id.button) as Button

            // If there is a valid list of {@link Joke}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (jokeObjects != null && !jokeObjects.isEmpty()) {
                adapter?.addAll(jokeObjects)
                button.text = getString(R.string.button_refresh)
            }
        }
    }

}