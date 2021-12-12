package com.example.zbporn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.zbporn.adapters.AlbumAdapter
import com.example.zbporn.databinding.FragmentLoadAlbumsBinding
import com.example.zbporn.models.ZBAlbum
import com.example.zbporn.utils.GetData
import com.example.zbporn.interfaces.IVideoLoadMore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"

class LoadAlbumsFragment : Fragment(), IVideoLoadMore {

    private var urlGet: String? = null
    private var page = 1
    private var limit = 0
    private var isSearch = false
    private lateinit var list: MutableList<ZBAlbum>
    private lateinit var albumAdapter: AlbumAdapter
    private var _binding: FragmentLoadAlbumsBinding? = null
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
        _binding = FragmentLoadAlbumsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridLayoutManager = GridLayoutManager(context, 2)
        binding.rvAlbums.layoutManager = gridLayoutManager
        binding.rvAlbums.setHasFixedSize(true)
        list = mutableListOf<ZBAlbum>()
        albumAdapter = AlbumAdapter(binding.rvAlbums, list)
        binding.rvAlbums.adapter = albumAdapter
        if (urlGet!!.contains("search")) isSearch = true
        CoroutineScope(Dispatchers.Main).launch {
            list.addAll(GetData.getAlbums("${urlGet}/", isSearch))
            albumAdapter.notifyDataSetChanged()
            limit = list[0].limit
            binding.pbAlbums.smoothToHide()
        }
        albumAdapter.setLoadMore(this)
    }

    override fun onLoadMore() {
        page++
        if (page <= limit) {
            list.add(ZBAlbum("null_album", "", "", "", "", 0))
            albumAdapter.notifyItemInserted(list.size - 1)

            CoroutineScope(Dispatchers.Main).launch {
                val temp = GetData.getAlbums("${urlGet}/${page}/", isSearch)
                list.removeAt(list.size - 1)
                albumAdapter.notifyItemRemoved(list.size)
                list.addAll(temp)
                albumAdapter.setLoaded()
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
            LoadAlbumsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, url)
                }
            }
    }

}