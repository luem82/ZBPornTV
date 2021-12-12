package com.example.zbporn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zbporn.adapters.PlaylistAdapter
import com.example.zbporn.databinding.FragmentLoadPlayliistsBinding
import com.example.zbporn.interfaces.IVideoLoadMore
import com.example.zbporn.models.ZBPlaylist
import com.example.zbporn.utils.GetData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"

class LoadPlaylistsFragment : Fragment(), IVideoLoadMore {

    private var urlGet: String? = null
    private var page = 1
    private var limit = 0
    private lateinit var list: MutableList<ZBPlaylist>
    private lateinit var playlistAdapter: PlaylistAdapter
    private var _binding: FragmentLoadPlayliistsBinding? = null
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
        _binding = FragmentLoadPlayliistsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(context)
        binding.rvPlaylists.layoutManager = linearLayoutManager
        binding.rvPlaylists.setHasFixedSize(true)
        list = mutableListOf<ZBPlaylist>()
        playlistAdapter = PlaylistAdapter(binding.rvPlaylists, list)
        binding.rvPlaylists.adapter = playlistAdapter
        CoroutineScope(Dispatchers.Main).launch {
            list.addAll(GetData.getPlaylists("${urlGet}/${page}/"))
            playlistAdapter.notifyDataSetChanged()
            limit = list[0].limit
            binding.pbPlaylists.smoothToHide()
        }
        playlistAdapter.setLoadMore(this)
    }

    override fun onLoadMore() {
        page++
        if (page <= limit) {
            list.add(
                ZBPlaylist(
                    "null_playlist", "", "", "", "", "", "", 0
                )
            )
            playlistAdapter.notifyItemInserted(list.size - 1)

            var url = ""
            if (urlGet!!.contains("?sort_by")) {
                url = "https://zbporn.tv/playlists/${page}/?sort_by=added_date/"
            } else {
                url = "https://zbporn.tv/playlists/${page}/"
            }
            CoroutineScope(Dispatchers.Main).launch {
                val temp = GetData.getPlaylists(url)
                list.removeAt(list.size - 1)
                playlistAdapter.notifyItemRemoved(list.size)
                list.addAll(temp)
                playlistAdapter.setLoaded()
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
            LoadPlaylistsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, url)
                }
            }
    }

}