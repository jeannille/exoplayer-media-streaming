/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
* limitations under the License.
 */
package com.example.exoplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.exoplayer.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util

/**
 * A fullscreen activity to play audio or video streams.
 */
class PlayerActivity : AppCompatActivity() {

    private var player: SimpleExoPlayer? = null

    private var playWhenReady = true //play/pause using playWhenReady
    private var currentWindow = 0// current window index using currentPosition
    private var playbackPosition = 0L //current playback position using currentPosition

    //obtain reference to the view tree from activity_player xml file
    //lazy(...) - Koitlin delegate for lazy initializing a value for
    //the first time it is used (here in onCReate callback of activity)
    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    //set root of view tree(xml) as the content view of this activity
    //"Also check to see that the videoView property is visible on your
    // viewBinding reference, and that its type is PlayerView."
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
    }

    //method to create SimpleExoPlayer
    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                viewBinding.videoView.player = exoPlayer
                val mediaItem =
                    MediaItem.fromUri(getString(R.string.media_url_mp4)) //create media item, takes URI of media file
                exoPlayer.setMediaItem(mediaItem) //add media item to the player
                //supply state info saved from releasePlayer() to player during initialization
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.prepare()
            }
    }

    //override four methods to implement app lifecycle
    /**
     * initialize the player in onStart or onResume
     */
    //app can be visible but not active in split window mode
    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer() //release resources
        }
    }


    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer() //release resources
        }
    }

    /**
     * Helper method called in onResume, allows you to have a full-screen experience
     */
    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        viewBinding.videoView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    /**
     * Release player's resources & destroys it, called in onPause and onStop.
     *  Allows you to resume playback from where the user left off - All you need to do is supply this state information when you initialize your player.

     */
    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition //play/pause using playWhenReady
            currentWindow = this.currentWindowIndex // current window index using currentPosition
            playWhenReady = this.playWhenReady //current playback position using currentPosition
            release()
        }
        player = null
    }


}