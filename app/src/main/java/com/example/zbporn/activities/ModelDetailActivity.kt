package com.example.zbporn.activities

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.zbporn.R
import com.example.zbporn.adapters.AlbumAdapter
import com.example.zbporn.adapters.VideoAdapter
import com.example.zbporn.adapters.ViewPagerAdapter
import com.example.zbporn.databinding.ActivityModelDetailBinding
import com.example.zbporn.databinding.FragmentModelAlbumsBinding
import com.example.zbporn.databinding.FragmentModelInfoBinding
import com.example.zbporn.databinding.FragmentModelVideosBinding
import com.example.zbporn.models.ZBAlbum
import com.example.zbporn.models.ZBModel
import com.example.zbporn.models.ZBVideo
import com.example.zbporn.utils.GetData
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModelDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModelDetailBinding

    companion object {
        private lateinit var model: ZBModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModelDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarModelDetail)
        binding.toolbarModelDetail.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.title = "Model Detail"

        model = intent.getSerializableExtra("model") as ZBModel
        getModelDatas(model)
    }

    private fun getModelDatas(model: ZBModel) {
        var viewPagerAdapter = ViewPagerAdapter(
            supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        viewPagerAdapter.addFragment(
            ModelInfoFragment.newInstance(), "Biography"
        )
        viewPagerAdapter.addFragment(
            ModelVideosFragment.newInstance(), "Videos"
        )
        viewPagerAdapter.addFragment(
            ModelAlbumsFragment.newInstance(), "Albums"
        )


        binding.viewPagerModelDetail.adapter = viewPagerAdapter
        val limit = (if (viewPagerAdapter.count > 1) viewPagerAdapter.count - 1 else 1)
        binding.viewPagerModelDetail.offscreenPageLimit = limit
        binding.tabLayoutModelDetail.setViewPager(binding.viewPagerModelDetail)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.face_in, R.anim.slide_out_right)
    }

    class ModelInfoFragment : Fragment() {

        private var _binding: FragmentModelInfoBinding? = null
        private val binding get() = _binding!!

        companion object {
            @JvmStatic
            fun newInstance() = ModelInfoFragment()
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentModelInfoBinding.inflate(inflater, container, false)
            val root: View = binding.root
            return root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.modellName.text = model.name
            Glide.with(binding.modelDetailThumb).load(model.thumb).into(binding.modelDetailThumb)
            GlideToVectorYou.init().with(binding.root.context)
                .load(Uri.parse(model.flag), binding.modelFlag)
            binding.modelVideos.text = "${model.videoCount} Video(s)"
            binding.modelAlbums.text = "${model.photoCount} Album(s)"

            CoroutineScope(Dispatchers.Main).launch {
                val info = GetData.getModelInfo(model.href)
                binding.modelLikes.text = info[0]
                binding.modelDislikes.text = info[1]
                binding.modelAbout.text = info[2]
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

    class ModelVideosFragment : Fragment() {

        private var _binding: FragmentModelVideosBinding? = null
        private val binding get() = _binding!!

        companion object {
            @JvmStatic
            fun newInstance() = ModelVideosFragment()
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentModelVideosBinding.inflate(inflater, container, false)
            val root: View = binding.root
            return root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            var hrefs = mutableListOf<String>()

            if (model.videoCount.trim().toInt() == 0) {
                binding.emptyVideo.visibility = View.VISIBLE
                binding.pbVideos.smoothToHide()
            } else {
                //https://zbporn.tv/performers/sophie-dee/?mode=async&action=get_block&block_id=list_videos_model_videos&from=1
                if (model.videoCount.toInt() <= 24) {
                    hrefs.add(
                        "https://zbporn.tv/performers/${
                            model.name.lowercase().replace(" ", "-")
                        }/?mode=async&action=get_block&block_id=list_videos_model_videos&from=1"
                    )
                } else {
                    hrefs.add(
                        "https://zbporn.tv/performers/${
                            model.name.lowercase().replace(" ", "-")
                        }/?mode=async&action=get_block&block_id=list_videos_model_videos&from=1"
                    )
                    hrefs.add(
                        "https://zbporn.tv/performers/${
                            model.name.lowercase().replace(" ", "-")
                        }/?mode=async&action=get_block&block_id=list_videos_model_videos&from=2"
                    )
                }
                getModelVideos(hrefs)
            }
        }

        private fun getModelVideos(hrefs: MutableList<String>) {
            CoroutineScope(Dispatchers.Main).launch {
                var list = mutableListOf<ZBVideo>()
                hrefs.forEach {
                    list.addAll(GetData.getVideos(it, false))
                }
                binding.rvVideos.layoutManager = GridLayoutManager(context, 2)
                binding.rvVideos.setHasFixedSize(true)
                binding.rvVideos.adapter = VideoAdapter(binding.rvVideos, list)
                binding.pbVideos.smoothToHide()
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

    class ModelAlbumsFragment : Fragment() {

        private var _binding: FragmentModelAlbumsBinding? = null
        private val binding get() = _binding!!

        companion object {
            @JvmStatic
            fun newInstance() = ModelAlbumsFragment()
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentModelAlbumsBinding.inflate(inflater, container, false)
            val root: View = binding.root
            return root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            var hrefs = mutableListOf<String>()

            if (model.photoCount.trim().toInt() == 0) {
                binding.emptyAlbum.visibility = View.VISIBLE
                binding.pbAlbums.smoothToHide()
            } else {
                //https://zbporn.tv/performers/sophie-dee/?mode=async&action=get_block&block_id=list_albums_model_albums&from2=2
                if (model.videoCount.toInt() <= 25) {
                    hrefs.add(
                        "https://zbporn.tv/performers/${
                            model.name.lowercase().replace(" ", "-")
                        }/?mode=async&action=get_block&block_id=list_albums_model_albums&from=1"
                    )
                } else {
                    hrefs.add(
                        "https://zbporn.tv/performers/${
                            model.name.lowercase().replace(" ", "-")
                        }/?mode=async&action=get_block&block_id=list_albums_model_albums&from=1"
                    )
                    hrefs.add(
                        "https://zbporn.tv/performers/${
                            model.name.lowercase().replace(" ", "-")
                        }/?mode=async&action=get_block&block_id=list_albums_model_albums&from=2"
                    )
                }
                getModelAlbums(hrefs)
            }
        }

        private fun getModelAlbums(hrefs: MutableList<String>) {
            CoroutineScope(Dispatchers.Main).launch {
                var list = mutableListOf<ZBAlbum>()
                hrefs.forEach {
                    list.addAll(GetData.getAlbums(it, false))
                }
                binding.rvAlbums.layoutManager = GridLayoutManager(context, 2)
                binding.rvAlbums.setHasFixedSize(true)
                binding.rvAlbums.adapter = AlbumAdapter(binding.rvAlbums, list)
                binding.pbAlbums.smoothToHide()
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
}