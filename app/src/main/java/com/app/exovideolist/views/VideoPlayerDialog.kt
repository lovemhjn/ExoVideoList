package com.app.exovideolist.views

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import com.app.exovideolist.databinding.DialogVideoPlayerBinding
import com.google.android.exoplayer2.Player

class VideoPlayerDialog (val mContext: Context, val player: Player): Dialog(mContext) {

    lateinit var binding: DialogVideoPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.itemVideoExoplayer.player = player
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        binding.itemVideoExoplayer.player = null
    }
}