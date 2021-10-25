package com.app.exovideolist.views

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.exovideolist.R
import com.app.exovideolist.databinding.ActivityPlayerViewBinding
import com.app.exovideolist.utils.PlayerViewAdapter.Companion.loadVideo
import com.app.exovideolist.utils.toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory

class PlayerViewActivity : AppCompatActivity() {
    lateinit var binding:ActivityPlayerViewBinding
    private lateinit var url: String
    private var position:Long =0
    private lateinit var thumbUrl:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        url = intent.getStringExtra("URL")?:""
        position = intent.getLongExtra("POSITION",0)
        thumbUrl = intent.getStringExtra("THUMBNAIL")?:""

        Glide.with(this)
            .load(thumbUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.thumbnail)
        initPlayer()

    }

    fun initPlayer(){
        val player = SimpleExoPlayer.Builder(this).build()

        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_ALL
        // When changing track, retain the latest frame instead of showing a black screen
        binding.itemVideoExoplayer.setKeepContentOnPlayerReset(true)
        // We'll show the controller, change to true if want controllers as pause and start
        binding.itemVideoExoplayer.useController = false
        // Provide url to load the video from here
        val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory("Demo")).createMediaSource(
            Uri.parse(url))

        player.prepare(mediaSource)
        player.seekTo(position)

        binding.itemVideoExoplayer.player = player

        (binding.itemVideoExoplayer.player as SimpleExoPlayer).addListener(object : Player.EventListener{
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                toast("Oops! Error occurred while playing media.")
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                if (playbackState == Player.STATE_BUFFERING){
                    // Buffering..
                    // set progress bar visible here
                    // set thumbnail visible
                    binding.thumbnail.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.VISIBLE
                }

                if (playbackState == Player.STATE_READY){
                    // [PlayerView] has fetched the video duration so this is the block to hide the buffering progress bar
                    binding.progressBar.visibility = View.GONE
                    // set thumbnail gone
                    binding.thumbnail.visibility = View.GONE
                }

                if (playbackState == Player.STATE_READY && player.playWhenReady){
                    // [PlayerView] has started playing/resumed the video
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        binding.itemVideoExoplayer.player?.release()
    }
}