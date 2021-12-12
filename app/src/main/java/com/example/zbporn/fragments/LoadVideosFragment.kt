package com.example.zbporn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.zbporn.adapters.VideoAdapter
import com.example.zbporn.databinding.FragmentLoadVideosBinding
import com.example.zbporn.models.ZBVideo
import com.example.zbporn.utils.GetData
import com.example.zbporn.interfaces.IVideoLoadMore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"

class LoadVideosFragment : Fragment(), IVideoLoadMore {

    private var urlGet: String? = null
    private var page = 1
    private var limit = 0
    private var isSearch = false
    private lateinit var list: MutableList<ZBVideo>
    private lateinit var videoAdapter: VideoAdapter
    private var _binding: FragmentLoadVideosBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            urlGet = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoadVideosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridLayoutManager = GridLayoutManager(context, 2)
        binding.rvVideos.layoutManager = gridLayoutManager
        binding.rvVideos.setHasFixedSize(true)
        list = mutableListOf<ZBVideo>()
        videoAdapter = VideoAdapter(binding.rvVideos, list)
        binding.rvVideos.adapter = videoAdapter
        if (urlGet!!.contains("search")) isSearch = true
        CoroutineScope(Dispatchers.Main).launch {
            list.addAll(GetData.getVideos("${urlGet}/${page}/", isSearch))
            videoAdapter.notifyDataSetChanged()
            limit = list[0].limit
            binding.pbVideos.smoothToHide()
        }
        videoAdapter.setLoadMore(this)
    }

    override fun onLoadMore() {
        page++
        if (page <= limit) {
            list.add(ZBVideo("", "null_video", "", "", "", "", "", 0))
            videoAdapter.notifyItemInserted(list.size - 1)

            CoroutineScope(Dispatchers.Main).launch {
                val temp = GetData.getVideos("${urlGet}/${page}/", isSearch)
                list.removeAt(list.size - 1)
                videoAdapter.notifyItemRemoved(list.size)
                list.addAll(temp)
                videoAdapter.setLoaded()
            }
        } else {
            Toast.makeText(context, "End Of List", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(url: String) =
            LoadVideosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, url)
                }
            }
    }

}