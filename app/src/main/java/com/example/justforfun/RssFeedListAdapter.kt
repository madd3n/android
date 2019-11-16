package com.example.justforfun

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_rss_feed.view.*
import com.squareup.picasso.Picasso


class RssFeedListAdapter(private var mRssFeedModels: List<RssFeedModel>) : RecyclerView.Adapter<RssFeedListAdapter.FeedModelViewHolder>() {

    lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedModelViewHolder {
            var v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rss_feed, parent, false)

            context = parent.context
            return FeedModelViewHolder(v)
        }

        override fun getItemCount(): Int {
            return mRssFeedModels.size
        }

        override fun onBindViewHolder(holder: FeedModelViewHolder, position: Int) {
            var rssFeedModel = mRssFeedModels[position]

            holder.itemView.titleText.text = rssFeedModel.title
            holder.itemView.descriptionText.text = rssFeedModel.description
            Picasso.get().load(Uri.parse(rssFeedModel.imageLink)).into(holder.itemView.rssFeedImage)

            holder.itemView.rssFeedImage.setOnClickListener {
                Toast.makeText(
                    context,
                    "Link:" + rssFeedModel.link,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        class FeedModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
