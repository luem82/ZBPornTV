package com.example.zbporn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.zbporn.adapters.ViewPagerAdapter
import com.example.zbporn.databinding.FragmentAlbumsBinding

class AlbumsFragment : Fragment() {

    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)
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
            LoadAlbumsFragment.newInstance("https://zbporn.tv/albums"), "Newest"
        )
        viewPagerAdapter.addFragment(
            LoadAlbumsFragment.newInstance("https://zbporn.tv/albums/most-popular"), "Most Popular"
        )
        viewPagerAdapter.addFragment(
            LoadAlbumsFragment.newInstance("https://zbporn.tv/albums/top-rated"), "Top Rated"
        )


        binding.viewPagerAlbums.adapter = viewPagerAdapter
        val limit = (if (viewPagerAdapter.count > 1) viewPagerAdapter.count - 1 else 1)
        binding.viewPagerAlbums.offscreenPageLimit = limit
        binding.tabLayoutAlbums.setViewPager(binding.viewPagerAlbums)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}