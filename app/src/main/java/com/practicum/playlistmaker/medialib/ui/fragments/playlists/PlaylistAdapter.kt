package com.practicum.playlistmaker.medialib.ui.fragments.playlists

import android.os.Environment
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
            tracksCount.text = model.tracksCount.toString()
            val cornerRadius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius)
            val file = File(filePath, model.imagePath.toUri().lastPathSegment)
            Glide.with(itemView)
                .load(file)
                .placeholder(R.drawable.track_placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornerRadius))
                .into(playlistCover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_list_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlistList[position])
        holder.itemView.setOnClickListener { clickListener?.onPlaylistClick(playlistList.get(position)) }
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