package com.app.exovideolist.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.app.exovideolist.R
import com.app.exovideolist.databinding.ActivityMainBinding
import com.app.exovideolist.viewmodels.VideoListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: VideoListViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        observeData()
    }

    private fun observeData() {
        viewModel.videoListLiveData.observe(this, Observer {
            binding.recyclerView.adapter = VideoListAdapter(it)
        })
    }
}