package com.example.justforfun

import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Xml
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import kotlinx.android.synthetic.main.activity_rss_feed.*

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.ArrayList

class RssFeedActivity : AppCompatActivity() {

    private var mRecyclerView: RecyclerView? = null
    private var mEditText: EditText? = null
    private var mFetchFeedButton: Button? = null
    private var mSwipeLayout: SwipeRefreshLayout? = null
    private var mFeedTitleTextView: TextView? = null
    private var mFeedLinkTextView: TextView? = null
    private var mFeedDescriptionTextView: TextView? = null

    private var mFeedModelList: List<RssFeedModel>? = null
    private var mFeedTitle: String? = null
    private var mFeedLink: String? = null
    private var mFeedDescription: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rss_feed)

        mRecyclerView = recyclerView as RecyclerView
        mEditText = rssFeedEditText as EditText
        mFetchFeedButton = fetchFeedButton as Button
        mSwipeLayout = swipeRefreshLayout as SwipeRefreshLayout
        mFeedTitleTextView =feedTitle as TextView
        mFeedDescriptionTextView = feedDescription as TextView
        mFeedLinkTextView = feedLink as TextView

        mRecyclerView!!.layoutManager = LinearLayoutManager(this)

        mFetchFeedButton!!.setOnClickListener { FetchFeedTask().execute(null as Void?) }
        mSwipeLayout!!.setOnRefreshListener { FetchFeedTask().execute(null as Void?) }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parseFeed(inputStream: InputStream): List<RssFeedModel> {
        var title: String? = null
        var link: String? = null
        var description: String? = null
        var isItem = false
        val items = ArrayList<RssFeedModel>()

        try {
            val xmlPullParser = Xml.newPullParser()
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            xmlPullParser.setInput(inputStream, null)

            xmlPullParser.nextTag()
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                val eventType = xmlPullParser.eventType

                val name = xmlPullParser.name ?: continue

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equals("item", ignoreCase = true)) {
                        isItem = false
                    }
                    continue
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equals("item", ignoreCase = true)) {
                        isItem = true
                        continue
                    }
                }

                Log.d("MainActivity", "Parsing name ==> $name")
                var result = ""
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.text
                    xmlPullParser.nextTag()
                }

                if (name.equals("title", ignoreCase = true)) {
                    title = result
                } else if (name.equals("link", ignoreCase = true)) {
                    link = result
                } else if (name.equals("description", ignoreCase = true)) {
                    description = result
                }

                if (title != null && link != null && description != null) {
                    if (isItem) {
                        val item = RssFeedModel(title, link, description)
                        items.add(item)
                    } else {
                        mFeedTitle = title
                        mFeedLink = link
                        mFeedDescription = description
                    }

                    title = null
                    link = null
                    description = null
                    isItem = false
                }
            }

            return items
        } finally {
            inputStream.close()
        }
    }

    private inner class FetchFeedTask : AsyncTask<Void, Void, Boolean>() {

        private var urlLink: String? = null

        override fun onPreExecute() {
            mSwipeLayout!!.isRefreshing = true
            mFeedTitle = null
            mFeedLink = null
            mFeedDescription = null
            mFeedTitleTextView!!.text = "Feed Title: " + mFeedTitle!!
            mFeedDescriptionTextView!!.text = "Feed Description: " + mFeedDescription!!
            mFeedLinkTextView!!.text = "Feed Link: " + mFeedLink!!
            urlLink = mEditText!!.text.toString()
        }

        override fun doInBackground(vararg voids: Void): Boolean? {
            if (TextUtils.isEmpty(urlLink))
                return false

            try {
                if (!urlLink!!.startsWith("http://") && !urlLink!!.startsWith("https://"))
                    urlLink = "http://" + urlLink!!

                val url = URL(urlLink!!)
                val inputStream = url.openConnection().getInputStream()
                mFeedModelList = parseFeed(inputStream)
                return true
            } catch (e: IOException) {
                Log.e(TAG, "Error", e)
            } catch (e: XmlPullParserException) {
                Log.e(TAG, "Error", e)
            }

            return false
        }

        override fun onPostExecute(success: Boolean) {
            mSwipeLayout!!.isRefreshing = false

            if (success) {
                mFeedTitleTextView!!.text = "Feed Title: " + mFeedTitle!!
                mFeedDescriptionTextView!!.text = "Feed Description: " + mFeedDescription!!
                mFeedLinkTextView!!.text = "Feed Link: " + mFeedLink!!
                // Fill RecyclerView
                mRecyclerView!!.adapter = RssFeedListAdapter(this@RssFeedActivity.mFeedModelList!!)
            } else {
                Toast.makeText(
                    this@RssFeedActivity,
                    "Enter a valid Rss feed url",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {

        private val TAG = "MainActivity"
    }
}