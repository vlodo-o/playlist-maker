package com.practicum.playlistmaker.medialib.ui.fragments.playlists

import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import java.io.File

class PlaylistAdapter (
    private val clickListener: PlaylistClickListener? = null
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> () {

    var playlistList = ArrayList<PlaylistModel>()

    class PlaylistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val playlistCover: ImageView = itemView.findViewById(R.id.playlist_cover_imageview)
        private val playlistName: TextView = itemView.findViewById(R.id.playlist_name_textview)
        private val tracksCount: TextView = itemView.findViewById(R.id.tracks_count_textview)
        private val filePath =
            File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_maker")

        fun bind(model: PlaylistModel) {
            playlistName.text = model.name
            tracksCount.text =  getTrackCount(model.tracksCount)
            val cornerRadius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius)
            val file = File(filePath, model.imagePath.toUri().lastPathSegment)
            Glide.with(itemView)
                .load(file)
                .placeholder(R.drawable.track_placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornerRadius))
                .into(playlistCover)
        }

        fun getTrackCount(count: Int): String {
            val lastDigit = count % 10
            val lastTwoDigits = count % 100

            val ending = when {
                lastDigit == 1 -> "трек"
                lastDigit in 2..4 -> "трека"
                lastTwoDigits in 11..19 -> "треков"
                else -> "треков"
            }

            return "$count $ending"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = if (parent.context is PlayerActivity) {
            LayoutInflater.from(parent.context).inflate(R.layout.playlist_horizontal_list_item, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.playlist_list_item, parent, false)
        }
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlistList[position])
        holder.itemView.setOnClickListener { clickListener?.onPlaylistClick(playlistList[position]) }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: PlaylistModel)
    }

    fun setPlaylists(playlists: List<PlaylistModel>?) {
        playlistList.clear()
        if (playlists != null) {
            playlistList.addAll(playlists)
        }
        notifyDataSetChanged()
    }
}