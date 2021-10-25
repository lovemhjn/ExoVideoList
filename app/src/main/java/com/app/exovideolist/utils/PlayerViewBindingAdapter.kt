package com.app.exovideolist.utils

import android.R.attr
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.app.exovideolist.views.PlayerViewActivity
import com.app.exovideolist.views.VideoPlayerDialog
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import android.R.attr.data
import android.graphics.Bitmap

import android.view.TextureView
import com.google.android.exoplayer2.analytics.AnalyticsListener


// extension function for show toast
fun Context.toast(text: String){
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

class PlayerViewAdapter {

    companion object{
        // for hold all players generated
        private var playersMap: MutableMap<Int, SimpleExoPlayer>  = mutableMapOf()
        // for hold current player
        private var currentPlayingVideo: Pair<Int, SimpleExoPlayer>? = null
        fun releaseAllPlayers(){
            playersMap.map {
                it.value.release()
            }
        }

        // call when item recycled to improve performance
        fun releaseRecycledPlayers(index: Int){
            playersMap[index]?.release()
        }

        // call when scroll to pause any playing player
        fun pauseCurrentPlayingVideo(){
            if (currentPlayingVideo != null){
                currentPlayingVideo?.second?.playWhenReady = false
            }
        }
        fun resumeCurrentPlayingVideo(){
            if (currentPlayingVideo != null){
                currentPlayingVideo?.second?.playWhenReady = true
            }
        }

        fun playIndexThenPausePreviousPlayer(index: Int){
            playersMap[index]?.let {
                if (!it.playWhenReady) {
                    pauseCurrentPlayingVideo()
                    it.playWhenReady = true
                    currentPlayingVideo = Pair(index, it)
                }
            }

        }

        /*
        *  url is a url of stream video
        *  progressbar for show when start buffering stream
        * thumbnail for show before video start
        * */
        @JvmStatic
        @BindingAdapter(value = ["video_url", "on_state_change", "progressbar", "thumbnail", "item_index", "autoPlay","view","thumb_url"], requireAll = false)
        fun PlayerView.loadVideo(url: String, callback: PlayerStateCallback, progressbar: ProgressBar, thumbnail: ImageView, item_index: Int? = null, autoPlay: Boolean = false, view: View,thumb_url:String) {
            if (url == null) return
            val player = SimpleExoPlayer.Builder(context).build()

            player.playWhenReady = autoPlay
            player.repeatMode = Player.REPEAT_MODE_ALL
            // When changing track, retain the latest frame instead of showing a black screen
            setKeepContentOnPlayerReset(true)
            // We'll show the controller, change to true if want controllers as pause and start
            this.useController = false
            // Provide url to load the video from here
            val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory("Demo")).createMediaSource(
                Uri.parse(url))

            player.prepare(mediaSource)

            this.player = player


            view.setOnClickListener{
                context.startActivity(Intent(context, PlayerViewActivity::class.java).apply {
                    putExtra("URL",url)
                    putExtra("POSITION", player.currentPosition)
                    putExtra("THUMBNAIL", thumb_url)
                })
            }

            // add player with its index to map
            if (playersMap.containsKey(item_index))
                playersMap.remove(item_index)
            if (item_index != null)
                playersMap[item_index] = player

            this.player?.volume = 0.0f

           this.player?.addListener(object : Player.Listener{
               override fun onPlayerError(error: PlaybackException) {
                   super.onPlayerError(error)
                   this@loadVideo.context.toast("Oops! Error occurred while playing media.")
               }


           })

            this.player?.addListener(object : Player.EventListener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)

                    if (playbackState == Player.STATE_BUFFERING){
                        callback.onVideoBuffering(player)
                        // Buffering..
                        // set progress bar visible here
                        // set thumbnail visible
                        thumbnail.visibility = View.VISIBLE
                        progressbar.visibility = View.VISIBLE
                    }

                    if (playbackState == Player.STATE_READY){
                        // [PlayerView] has fetched the video duration so this is the block to hide the buffering progress bar
                        progressbar.visibility = View.GONE
                        // set thumbnail gone
                        thumbnail.visibility = View.GONE
                        this@loadVideo.player?.duration?.let { callback.onVideoDurationRetrieved(it, player) }
                    }

                    if (playbackState == Player.STATE_READY && player.playWhenReady){
                        // [PlayerView] has started playing/resumed the video
                        callback.onStartedPlaying(player)
                    }
                }
            })
        }
    }
}