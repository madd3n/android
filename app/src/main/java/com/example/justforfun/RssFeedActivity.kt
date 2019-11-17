package com.example.justforfun

import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Xml
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView

import kotlinx.android.synthetic.main.activity_rss_feed.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.ArrayList

class RssFeedActivity : AppCompatActivity() {

    private var mFeedModelList: List<RssFeedModel>? = null
    private var mFeedTitle: String? = null
    private var mFeedLink: String? = null
    private var mFeedDescription: String? = null
    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rss_feed)

        recyclerView.layoutManager = LinearLayoutManager(this)
        /*fetchFeedButton.setOnClickListener {

            FetchFeedTask().execute()
        }
        */
/*

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
*/

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.mipmap.ic_launcher_round)
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Handle navigation view item clicks here.
            when (menuItem.itemId) {

                R.id.nav_record -> {
                    FetchFeedTask("https://www.record.pt/rss").execute()
                    swipeRefreshLayout.setOnRefreshListener { FetchFeedTask("https://www.record.pt/rss").execute() }
                }
                R.id.nav_jornaldenegocios -> {
                    FetchFeedTask("https://www.jornaldenegocios.pt/rss").execute()
                    swipeRefreshLayout.setOnRefreshListener { FetchFeedTask("https://www.jornaldenegocios.pt/rss").execute() }
                }
                /*R.id.nav_offer -> {
                    Toast.makeText(this, "Offer", Toast.LENGTH_LONG).show()
                }
                R.id.nav_setting -> {
                    Toast.makeText(this, "Setting", Toast.LENGTH_LONG).show()
                }*/
            }
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }
    }

    //appbar - toolbar button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parseFeed(inputStream: InputStream): List<RssFeedModel> {
        var title: String? = null
        var link: String? = null
        var description: String? = null
        var image: String? = null
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
                var imageUrl =""

                //if(xmlPullParser.next() == XmlPullParser.)
                if(isItem && xmlPullParser.attributeCount >1) {
                    imageUrl = xmlPullParser.getAttributeValue(null, "url")
                }

                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.text
                    xmlPullParser.nextTag()
                }

                when {
                    name.equals("title", ignoreCase = true) -> title = result
                    name.equals("link", ignoreCase = true) -> link = result
                    name.equals("description", ignoreCase = true) -> description = result
                    name.equals("enclosure", ignoreCase = true) -> image = imageUrl
                }

                if (title != null && link != null && description != null && image!= null) {

                    if (isItem) {
                        if(image!= null) {

                            items.add(RssFeedModel(
                                title = title,
                                link = link,
                                description = description,
                                imageLink = image
                            ))
                            /*items.add(item)*/
                        }
                        /*else
                        {
                            items.add(RssFeedModel(
                                title = title,
                                link = link,
                                description = description,
                                imageLink = null
                            ))
                            *//*items.add(item)*//*
                        }*/
                    }

                    title = null
                    link = null
                    description = null
                    image = null
                    isItem = false
                }
                /*else if(title != null && link != null && description != null && image == null) {
                    if (isItem) {
                        val item = RssFeedModel(
                            title = title,
                            link = link,
                            description = description,
                            imageLink = image
                        )
                        items.add(item)
                    }

                    title = null
                    link = null
                    description = null
                    image = null
                    isItem = false
                }*/
            }

            return items
        } finally {
            inputStream.close()
        }
    }

    private inner class FetchFeedTask(var url:String) : AsyncTask<Void, Void, Boolean>() {

        //private var urlLink: String? = null

        override fun onPreExecute() {
            swipeRefreshLayout.isRefreshing = true
            mFeedTitle = null
            mFeedLink = null
            mFeedDescription = null

            //urlLink = rssFeedEditText.text.toString()
        }

        override fun doInBackground(vararg voids: Void): Boolean? {


            if (TextUtils.isEmpty(url))
                return false

            try {
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://$url"

                val url = URL(url)
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
            swipeRefreshLayout.isRefreshing = false

            if (success) {
                recyclerView.adapter = RssFeedListAdapter(this@RssFeedActivity.mFeedModelList!!)

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