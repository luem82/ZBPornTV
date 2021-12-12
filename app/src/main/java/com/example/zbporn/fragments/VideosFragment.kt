package com.example.zbporn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.zbporn.adapters.ViewPagerAdapter
import com.example.zbporn.databinding.FragmentVideosBinding
import kotlinx.coroutines.CoroutineScope

class VideosFragment : Fragment() {

    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideosBinding.inflate(inflater, container, false)
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
            //https://zbporn.tv/latest-updates/2/
            LoadVideosFragment.newInstance("https://zbporn.tv/latest-updates"), "Straight"
        )
        viewPagerAdapter.addFragment(
            //https://zbporn.tv/gays/
            LoadVideosFragment.newInstance("https://zbporn.tv/gays/latest-updates"), "Gay"
        )
        viewPagerAdapter.addFragment(
            //https://zbporn.tv/shemale/
            LoadVideosFragment.newInstance("https://zbporn.tv/shemale/latest-updates"), "Shemale"
        )


        binding.viewPagerVideos.adapter = viewPagerAdapter
        val limit = (if (viewPagerAdapter.count > 1) viewPagerAdapter.count - 1 else 1)
        binding.viewPagerVideos.offscreenPageLimit = limit
        binding.tabLayoutVideos.setViewPager(binding.viewPagerVideos)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}