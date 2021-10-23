package com.app.exovideolist.views

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.exovideolist.R
import com.app.exovideolist.data.models.MediaObject
import com.app.exovideolist.databinding.ItemVideoListBinding
import com.app.exovideolist.utils.PlayerStateCallback
import com.app.exovideolist.utils.PlayerViewAdapter.Companion.releaseRecycledPlayers
import com.google.android.exoplayer2.Player
import java.util.*

/**
 * A custom adapter
 */
class VideoListAdapter(
    private var modelList: List<MediaObject>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    PlayerStateCallback {

    private var mItemClickListener: OnItemClickListener? =
        null


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): VideoPlayerViewHolder {
        val binding: ItemVideoListBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context)
            , R.layout.item_video_list, viewGroup, false)
        return VideoPlayerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int) {

        //Here you can fill your row view
        if (holder is VideoPlayerViewHolder) {
            val model = getItem(position)
            val genericViewHolder = holder

            // send data to view holder
            genericViewHolder.onBind(model)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        val position = holder.bindingAdapterPosition
        releaseRecycledPlayers(position)
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    private fun getItem(position: Int): MediaObject {
        return modelList[position]
    }

    fun SetOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mItemClickListener = mItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(
            view: View?,
            position: Int,
            model: MediaObject?
        )
    }

    inner class VideoPlayerViewHolder(private val binding: ItemVideoListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(model: MediaObject) {
            // handle on item click
            binding.root.setOnClickListener {
                mItemClickListener?.onItemClick(
                    it,
                    bindingAdapterPosition,
                    model
                )
            }

            binding.apply {
                dataModel = model
                callback = this@VideoListAdapter
                index = bindingAdapterPosition
                executePendingBindings()
            }



        }
    }

    override fun onVideoDurationRetrieved(duration: Long, player: Player) {}

    override fun onVideoBuffering(player: Player) {}

    override fun onStartedPlaying(player: Player) {
        Log.d("playvideo", "staaaart" + player.contentDuration)

    }


    override fun onFinishedPlaying(player: Player) {}
}