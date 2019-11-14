package com.example.justforfun

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_rss_feed.*
import kotlinx.android.synthetic.main.item_rss_feed.view.*

class RssFeedListAdapter() : RecyclerView.Adapter<RssFeedListAdapter.FeedModelViewHolder>() {
    constructor(mFeedModelList: List<RssFeedModel>):this(){
        mRssFeedModels = mFeedModelList
    }

    var context : Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedModelViewHolder {
            var v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rss_feed, parent, false)

            context = parent.context
            return FeedModelViewHolder(v)
        }

        override fun getItemCount(): Int {
            return mRssFeedModels?.size ?: 0
        }

        override fun onBindViewHolder(holder: FeedModelViewHolder, position: Int) {
            var rssFeedModel = mRssFeedModels?.get(position)

            holder.itemView.titleText.text = rssFeedModel?.title
            holder.itemView.descriptionText.text = rssFeedModel?.description
            holder.itemView.linkText.text = rssFeedModel?.link

            holder.itemView.linkText.setOnClickListener {
                Toast.makeText(
                    context,
                    "Link:" + holder.itemView.linkText.text,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        var mRssFeedModels: List<RssFeedModel>? = null

        class FeedModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
