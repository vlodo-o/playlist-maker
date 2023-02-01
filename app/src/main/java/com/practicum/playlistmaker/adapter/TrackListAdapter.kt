package com.practicum.playlistmaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.model.Track

class TrackListAdapter(
    private val trackList: List<Track>
) : RecyclerView.Adapter<TrackListAdapter.TrackViewHolder> () {

    class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val trackCoverImageView: ImageView = itemView.findViewById(R.id.track_cover)
        private val trackNameTextView: TextView = itemView.findViewById(R.id.track_name_textview)
        private val trackArtistTextView: TextView = itemView.findViewById(R.id.track_artist_textview)
        private val trackTimeTextView: TextView = itemView.findViewById(R.id.track_time_textview)

        fun bind(model: Track) {
            val cornerRadius = itemView.resources.getDimensionPixelSize(R.dimen.track_cover_radius)
            Glide.with(itemView)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.track_placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornerRadius))
                .into(trackCoverImageView)
            trackNameTextView.text = model.trackName
            trackArtistTextView.text = model.artistName
            trackTimeTextView.text = model.trackTime
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_track_list_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

}