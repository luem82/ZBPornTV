package com.example.zbporn.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.zbporn.R
import com.example.zbporn.adapters.VideoAdapter
import com.example.zbporn.databinding.ActivityChannelDetailBinding
import com.example.zbporn.interfaces.IVideoLoadMore
import com.example.zbporn.models.ZBChannel
import com.example.zbporn.models.ZBVideo
import com.example.zbporn.utils.GetData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChannelDetailActivity : AppCompatActivity(), IVideoLoadMore {

    private lateinit var binding: ActivityChannelDetailBinding
    private lateinit var channel: ZBChannel
    private var page = 1
    private var limit = 0
    private var isSearch = false
    private lateinit var list: MutableList<ZBVideo>
    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarChannelDetail)
        binding.toolbarChannelDetail.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.title = "Channel Detail"

        channel = intent.getSerializableExtra("channel") as ZBChannel
        getChannelDatas(channel)

    }

    private fun getChannelDatas(channel: ZBChannel) {
        val gridLayoutManager = GridLayoutManager(this, 2)
        binding.rvChannelVideos.layoutManager = gridLayoutManager
        binding.rvChannelVideos.setHasFixedSize(true)
        list = mutableListOf<ZBVideo>()
        videoAdapter = VideoAdapter(binding.rvChannelVideos, list)
        binding.rvChannelVideos.adapter = videoAdapter

        CoroutineScope(Dispatchers.Main).launch {
            // get info
            val infos = GetData.getChannelInfos(channel.href)
            Glide.with(applicationContext).load(infos[0]).into(binding.channelBackdrop)
            Glide.with(applicationContext).load(infos[1]).into(binding.channelThumb)
            binding.channelTitle.text = infos[2]
            binding.channeViews.text = infos[3]
            binding.channelLike.text = infos[4]
            binding.channelDislike.text = infos[5]
            binding.channelCount.text = infos[6]
            binding.channelDesciption.text = infos[7]
            binding.channelAllVideos.text = "Videos from channel: ${infos[2]}"

            // get videos
            list.addAll(GetData.getVideos("${channel.href}${page}/", isSearch))
            videoAdapter.notifyDataSetChanged()
            limit = list[0].limit

            binding.pbChannelDetail.smoothToHide()
        }

        videoAdapter.setLoadMore(this)
    }

    override fun onLoadMore() {
        page++
        if (page <= limit) {
            list.add(ZBVideo("", "null_video", "", "", "", "", "", 0))
            videoAdapter.notifyItemInserted(list.size - 1)

            CoroutineScope(Dispatchers.Main).launch {
                val temp = GetData.getVideos("${channel.href}${page}/", isSearch)
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