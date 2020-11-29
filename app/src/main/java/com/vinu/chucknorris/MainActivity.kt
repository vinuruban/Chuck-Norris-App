package com.vinu.chucknorris

import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    /** Tag for log messages  */
    private var LOG_TAG: String = MainActivity::class.java.name

    /** Jokes will be logged here */
    private var jokeList: ArrayList<String> = ArrayList()

    /** Adapter for the list of jokes */
    var adapter: ArrayAdapter<String>? = null

    /** Listview displays the jokes */
    private var listView: ListView? = null

    /** URL to fetch random jokes excluding the explicit ones */
    private val url = "http://api.icndb.com/jokes/random/"
    private val urlAttachment = "?exclude=[explicit]"
    private var finalUrl = ""

    /**
     * Constant value for the joke loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private val JOKE_LOADER_ID = 1

    /** TextView that is displayed when the list is empty  */
    private var emptyStateTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<View>(R.id.button) as Button
        button.setOnClickListener {
            finalUrl = url + "6" + urlAttachment
            Log.e(LOG_TAG, "URL : $url")
            val loadingIndicator = findViewById<View>(R.id.loading_indicator) //todo
            loadingIndicator.visibility = View.VISIBLE

            /** Create a new adapter that takes an EMPTY list of jokes as input */
            adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, jokeList)

            /** Set the adapter on the {@link ListView}
             * so the list can be populated in the user interface
             */
            listView?.adapter = adapter

            //Below is the view that will be viewed if there is no internet connection or no data retrieved
            emptyStateTextView = findViewById<View>(R.id.empty_view) as TextView //todo
            listView?.emptyView = emptyStateTextView

            // Get a reference to the ConnectivityManager to check state of network connectivity
            val connMgr =
                getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            // Get details on the currently active default data network
            val networkInfo = connMgr.activeNetworkInfo

            // If there is a network connection, fetch data
            if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
                // Start the AsyncTask to fetch the data
                val task = JokeAsyncTask()
                task.execute(url)
            } else {
                // Otherwise, display error
                // First, hide loading indicator so error message will be visible
                loadingIndicator.visibility = View.GONE

                // Update empty state with no connection error message
                emptyStateTextView!!.setText(R.string.no_internet_connection)
            }
        }
    }

    private class JokeAsyncTask :
        AsyncTask<String?, Void?, ArrayList<String>?>() {
        override fun doInBackground(vararg urls: String?): ArrayList<String>? {
            // Don't perform the request if there are no URLs, or the first URL is null.
            return if (urls.size < 1 || urls[0] == null) {
                null
            } else QueryUtils.fetchData(urls[0])

            // Perform the HTTP request for the data and process the response.
        }


        override fun onPostExecute(jokeObjects: ArrayList<String>?) {

            var mainActivity = MainActivity()

//            // Hide loading indicator because the data has been loaded todo
//            val loadingIndicator = findViewById<View>(R.id.loading_indicator)
//            loadingIndicator.visibility = View.GONE
//
//            // Set empty state text to display "No jokes found."
//            emptyStateTextView.setText(R.string.no_jokes)

            // Clear the adapter of previous the data
            mainActivity.adapter?.clear()

            // If there is a valid list of {@link Joke}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (jokeObjects != null && !jokeObjects.isEmpty()) {
                mainActivity.adapter?.addAll(jokeObjects) //we add all the data into the adapter.
            }
        }
    }

}