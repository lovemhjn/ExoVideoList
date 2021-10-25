package com.app.exovideolist.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.exovideolist.R
import com.app.exovideolist.data.models.MediaObject
import com.app.exovideolist.databinding.ActivityMainBinding
import com.app.exovideolist.utils.PlayerViewAdapter.Companion.pauseCurrentPlayingVideo
import com.app.exovideolist.utils.PlayerViewAdapter.Companion.playIndexThenPausePreviousPlayer
import com.app.exovideolist.utils.PlayerViewAdapter.Companion.releaseAllPlayers
import com.app.exovideolist.utils.PlayerViewAdapter.Companion.resumeCurrentPlayingVideo
import com.app.exovideolist.utils.RecyclerViewScrollListener
import com.app.exovideolist.viewmodels.VideoListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var scrollListener: RecyclerViewScrollListener?=null
    private val viewModel: VideoListViewModel by viewModels()
    var videoListAdapter:VideoListAdapter?=null
    val layoutManager by lazy { LinearLayoutManager(this) }
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeData()
        setScrollListener()
    }

    private fun observeData() {
        viewModel.videoListLiveData.observe(this, Observer {
            videoListAdapter = VideoListAdapter(it)
            binding.recyclerView.adapter = videoListAdapter

        })
    }

    private fun setScrollListener() {

        scrollListener = object : RecyclerViewScrollListener() {
            override fun onItemIsFirstVisibleItem(index: Int) {
                Log.d("visible item index", index.toString())
                // play just visible item
                if (index != -1)
                    playIndexThenPausePreviousPlayer(index)
            }

        }
        scrollListener?.let { binding.recyclerView.addOnScrollListener(it)  }


    }

    override fun onPause() {
        super.onPause()
        pauseCurrentPlayingVideo()
    }

    override fun onRestart() {
        super.onRestart()
        resumeCurrentPlayingVideo()

    }

    override fun onDestroy() {
        super.onDestroy()
        releaseAllPlayers()
    }
}