package com.app.exovideolist.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.exovideolist.data.models.MediaObject
import com.app.exovideolist.data.repository.VideoListRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class VideoListViewModel : ViewModel() {


    val videoListLiveData = MutableLiveData<List<MediaObject>>()


    init {
        viewModelScope.launch {
            VideoListRepository.videoListFlow.collect {
                videoListLiveData.postValue(it)
            }
        }
    }

}