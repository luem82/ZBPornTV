package com.example.zbporn.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.zbporn.R
import com.example.zbporn.adapters.VideoAdapter
import com.example.zbporn.databinding.ActivityPlaylistDetailBinding
import com.example.zbporn.interfaces.IVideoLoadMore
import com.example.zbporn.models.ZBPlaylist
import com.example.zbporn.models.ZBVideo
import com.example.zbporn.utils.GetData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistDetailActivity : AppCompatActivity(), IVideoLoadMore {

    private lateinit var binding: ActivityPlaylistDetailBinding
    private lateinit var playlist: ZBPlaylist
    private var page = 1
    private var limit = 0
    private var isSearch = false
    private lateinit var list: MutableList<ZBVideo>
    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarPlaylistDetail)
        binding.toolbarPlaylistDetail.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.title = "Playlist Detail"

        playlist = intent.getSerializableExtra("playlist") as ZBPlaylist
        getPlaylistDatas(playlist)
    }

    private fun getPlaylistDatas(playlist: ZBPlaylist) {

        // load info
        Glide.with(this).load(playlist.thumb).into(binding.playlistThumbTop)
        Glide.with(this).load(playlist.prevew1).into(binding.playlistThumbLeft)
        Glide.with(this).load(playlist.preview2).into(binding.playlistThumbCenter)
        Glide.with(this).load(playlist.preview3).into(binding.playlistThumbRight)
        binding.playlistTitle.text = playlist.title
        binding.playlistAllVideos.text = "Videos of playlist: ${playlist.title}"
        binding.playlistCount.text = "${playlist.count} Video(s)"

        // get videos
        val gridLayoutManager = GridLayoutManager(this, 2)
        binding.rvPlaylistVideos.layoutManager = gridLayoutManager
        binding.rvPlaylistVideos.setHasFixedSize(true)
        list = mutableListOf<ZBVideo>()
        videoAdapter = VideoAdapter(binding.rvPlaylistVideos, list)
        binding.rvPlaylistVideos.adapter = videoAdapter
        CoroutineScope(Dispatchers.Main).launch {
            list.addAll(GetData.getVideos("${playlist.href}${page}/", isSearch))
            videoAdapter.notifyDataSetChanged()
            limit = list[0].limit
            binding.pbPlaylistDetail.smoothToHide()
        }
        videoAdapter.setLoadMore(this)
    }

    override fun onLoadMore() {
        page++
        if (page <= limit) {
            list.add(ZBVideo("", "null_video", "", "", "", "", "", 0))
            videoAdapter.notifyItemInserted(list.size - 1)

            CoroutineScope(Dispatchers.Main).launch {
                val temp = GetData.getVideos("${playlist.href}${page}/", isSearch)
                list.removeAt(list.size - 1)
                videoAdapter.notifyItemRemoved(list.size)
                list.addAll(temp)
                videoAdapter.setLoaded()
            }
        } else {
            Toast.makeText(this, "End Of List", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.face_in, R.anim.slide_out_right)
    }
}