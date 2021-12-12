package com.example.zbporn.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.zbporn.R
import com.example.zbporn.models.ZBVideo
import com.example.zbporn.utils.Downloads
import com.example.zbporn.utils.GetData
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import kotlinx.android.synthetic.main.activity_full_video.*
import kotlinx.android.synthetic.main.custom_controller_view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FullVideoActivity : AppCompatActivity() {

    private lateinit var player: SimpleExoPlayer
    private lateinit var video: ZBVideo
    private lateinit var urlSource: String
    private var isFullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        window.attributes.windowAnimations = R.style.FullPictureAnimtion
        setContentView(R.layout.activity_full_video)


        // immesive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, root).let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        video = intent.getSerializableExtra("video") as ZBVideo
        CoroutineScope(Dispatchers.Main).launch {
            urlSource = GetData.getZBVideoSource(video.href)
            createPlayer(urlSource)
        }
//        createPlayer("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
        setListeners()

    }

    private fun createPlayer(uri: String) {
        video_title_full_video.text = video.title
        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player
        val mediaItem =
            MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_BUFFERING) {
                    pb_loading_video.visibility = View.VISIBLE
                } else if (playbackState == Player.STATE_READY) {
                    pb_loading_video.visibility = View.GONE
                }
            }
        })
        player.prepare()
        player.play()
        playInFullscreen(enable = isFullscreen)
    }

    private fun setListeners() {

        back_full_video.setOnClickListener { finish() }

        full_screen_mode.setOnClickListener {
            if (isFullscreen) {
                isFullscreen = false
                playInFullscreen(enable = false)
            } else {
                isFullscreen = true
                playInFullscreen(enable = true)
            }
        }

        download_full_video.setOnClickListener {
            val fileUrl = urlSource
            val fileName = "${video!!.title}_Full.mp4"
            Downloads.showDialogDownload(
                this, fileUrl, fileName, "Do you want download this video full?"
            )
        }
    }

    private fun playInFullscreen(enable: Boolean) {
        if (enable) {
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            full_screen_mode.setImageResource(R.drawable.ic_baseline_fullscreen_exit_24)
        } else {
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            full_screen_mode.setImageResource(R.drawable.ic_baseline_fullscreen_24)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}