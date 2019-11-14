package com.example.justforfun

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_rss_feed.view.*

class RssFeedListAdapter() : RecyclerView.Adapter<RssFeedListAdapter.FeedModelViewHolder>() {
    constructor(mFeedModelList: List<RssFeedModel>):this(){
        mRssFeedModels = mFeedModelList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedModelViewHolder {
            var v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rss_feed, parent, false)
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
        }

        var mRssFeedModels: List<RssFeedModel>? = null

        class FeedModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
