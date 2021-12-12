package com.example.zbporn.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.zbporn.R
import com.example.zbporn.adapters.ViewPagerAdapter
import com.example.zbporn.databinding.ActivitySearchVideoBinding
import com.example.zbporn.fragments.LoadAlbumsFragment
import com.example.zbporn.fragments.LoadVideosFragment

class SearchVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchVideoBinding
    private lateinit var key: String
    private lateinit var url: String
    private lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarSearch)

        key = intent.getStringExtra("key")!!
        url = intent.getStringExtra("url")!!
        type = intent.getStringExtra("type")!!

        supportActionBar?.title = key
        binding.toolbarSearch.setNavigationOnClickListener { onBackPressed() }

        if (type.equals("video")) {
            initVideos()
        } else {
            initAlbums()
        }
    }

    private fun initVideos() {
        var viewPagerAdapter = ViewPagerAdapter(
            supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )

        if (url!!.contains("categories")) {
            viewPagerAdapter.addFragment(
                LoadVideosFragment.newInstance("${url}/latest-updates"), "Newest"
            )
            viewPagerAdapter.addFragment(
                LoadVideosFragment.newInstance("${url}"), "Most Popular"
            )
            viewPagerAdapter.addFragment(
                LoadVideosFragment.newInstance("${url}/longest"), "Longest"
            )
            viewPagerAdapter.addFragment(
                LoadVideosFragment.newInstance("${url}/top-rated"), "Top Rated"
            )
        } else {
            //https://zbporn.tv/search/latest-updates/mom+and+son/
            var query = url.substringAfterLast("/")
            viewPagerAdapter.addFragment(
                LoadVideosFragment.newInstance("https://zbporn.tv/search/latest-updates/${query}"),
                "Newest"
            )
            viewPagerAdapter.addFragment(
                LoadVideosFragment.newInstance("https://zbporn.tv/search/${query}"), "Most Popular"
            )
            viewPagerAdapter.addFragment(
                LoadVideosFragment.newInstance("https://zbporn.tv/search/longest/${query}"),
                "Longest"
            )
            viewPagerAdapter.addFragment(
                LoadVideosFragment.newInstance("https://zbporn.tv/search/top-rated/${query}"),
                "Top Rated"
            )
        }

        binding.viewPagerSearch.adapter = viewPagerAdapter
        val limit = (if (viewPagerAdapter.count > 1) viewPagerAdapter.count - 1 else 1)
        binding.viewPagerSearch.offscreenPageLimit = limit
        binding.tabLayoutSearch.setViewPager(binding.viewPagerSearch)
    }

    private fun initAlbums() {
        var viewPagerAdapter = ViewPagerAdapter(
            supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )

//        viewPagerAdapter.addFragment(
//            //https://zbporn.tv/albums/search/mom+and+son/?sort_by=newest
//            LoadAlbumsFragment.newInstance("${url}/?sort_by=newest"), "Newest"
//        )
//        viewPagerAdapter.addFragment(
//            LoadAlbumsFragment.newInstance("${url}/?sort_by=album_viewed"), "Most Popular"
//        )
        viewPagerAdapter.addFragment(
            LoadAlbumsFragment.newInstance("${url}"), "Albums Search Results"
        )
//        viewPagerAdapter.addFragment(
//            LoadAlbumsFragment.newInstance("${url}/?sort_by=rating"), "Top Rated"
//        )

        binding.viewPagerSearch.adapter = viewPagerAdapter
        val limit = (if (viewPagerAdapter.count > 1) viewPagerAdapter.count - 1 else 1)
        binding.viewPagerSearch.offscreenPageLimit = limit
        binding.tabLayoutSearch.setViewPager(binding.viewPagerSearch)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.face_in, R.anim.slide_out_right)
    }
}