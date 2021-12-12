package com.example.zbporn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.zbporn.adapters.ChannelAdapter
import com.example.zbporn.databinding.FragmentChannelsBinding
import com.example.zbporn.models.ZBChannel
import com.example.zbporn.utils.GetData
import com.example.zbporn.interfaces.IVideoLoadMore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChannelsFragment : Fragment(), IVideoLoadMore {

    private var urlGet = "https://zbporn.tv/channels"
    private var page = 1
    private var limit = 0
    private lateinit var list: MutableList<ZBChannel>
    private lateinit var channelAdapter: ChannelAdapter
    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridLayoutManager = GridLayoutManager(context, 2)
        binding.rvChannels.layoutManager = gridLayoutManager
        binding.rvChannels.setHasFixedSize(true)
        list = mutableListOf<ZBChannel>()
        channelAdapter = ChannelAdapter(binding.rvChannels, list)
        binding.rvChannels.adapter = channelAdapter
        CoroutineScope(Dispatchers.Main).launch {
            list.addAll(GetData.getChannels("${urlGet}/${page}/"))
            channelAdapter.notifyDataSetChanged()
            limit = list[0].limit
            binding.pbChannels.smoothToHide()
        }
        channelAdapter.setLoadMore(this)
    }

    override fun onLoadMore() {
        page++
        if (page <= limit) {
            list.add(ZBChannel("null_channel", "", "", "", "", 0))
            channelAdapter.notifyItemInserted(list.size - 1)

            CoroutineScope(Dispatchers.Main).launch {
                val temp = GetData.getChannels("${urlGet}/${page}/")
                list.removeAt(list.size - 1)
                channelAdapter.notifyItemRemoved(list.size)
                list.addAll(temp)
                channelAdapter.setLoaded()
            }
        } else {
            Toast.makeText(context, "End Of List", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}