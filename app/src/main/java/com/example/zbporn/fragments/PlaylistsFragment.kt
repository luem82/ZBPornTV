package com.example.zbporn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.example.zbporn.adapters.ViewPagerAdapter
import com.example.zbporn.databinding.FragmentAlbumsBinding
import com.example.zbporn.databinding.FragmentPlaylistsBinding
import com.ogaclejapan.smarttablayout.SmartTabLayout

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var viewPagerAdapter = ViewPagerAdapter(
            requireActivity().supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )

        viewPagerAdapter.addFragment(
            //https://zbporn.tv/playlists/2/?sort_by=added_date
            LoadPlaylistsFragment.newInstance("https://zbporn.tv/playlists/?sort_by=added_date"),
            "Newest"
        )

        viewPagerAdapter.addFragment(
            LoadPlaylistsFragment.newInstance("https://zbporn.tv/playlists"), "Most Popular"
        )

        binding.viewPagerPlaylists.adapter = viewPagerAdapter
        val limit = (if (viewPagerAdapter.count > 1) viewPagerAdapter.count - 1 else 1)
        binding.viewPagerPlaylists.offscreenPageLimit = limit
        binding.tabLayoutPlaylists.setViewPager(binding.viewPagerPlaylists)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}