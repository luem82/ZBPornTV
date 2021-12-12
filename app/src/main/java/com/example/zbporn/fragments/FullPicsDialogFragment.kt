package com.example.zbporn.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.zbporn.R
import com.example.zbporn.adapters.PictureAdapter
import com.example.zbporn.databinding.FragmentDialogFullPicsBinding
import com.example.zbporn.interfaces.IHorizontalPicsClickListener
import com.example.zbporn.models.ZPPicture
import com.example.zbporn.utils.Downloads

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FullPicsDialogFragment : DialogFragment(), IHorizontalPicsClickListener {

    private var _binding: FragmentDialogFullPicsBinding? = null
    private val binding get() = _binding!!
    private var selectedPosition: Int? = null
    private lateinit var list: ArrayList<ZPPicture>
    private lateinit var pictureAdapterHorizontal: PictureAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedPosition = it.getInt(ARG_PARAM2)
            list = it.getSerializable(ARG_PARAM1) as ArrayList<ZPPicture>
        }
        setStyle(STYLE_NORMAL, R.style.Theme_ZBPorn_FullPicture)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentDialogFullPicsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireContext() as AppCompatActivity).setSupportActionBar(binding.toolbarFullPics)
        binding.toolbarFullPics.setNavigationOnClickListener { dismiss() }
        binding.toolbarFullPics.title = list[0].title
        binding.toolbarFullPics.subtitle = "${selectedPosition!! + 1} / ${list.size} pics"

        initViewPagerSlide()
        initHorizontalRecyclerView()
    }

    private fun initViewPagerSlide() {

        var photoAdapter = PictureAdapter("pager", list!!, null)
        binding.vpFullPics.adapter = photoAdapter
        binding.vpFullPics.currentItem = selectedPosition!!

        binding.vpFullPics.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                pictureAdapterHorizontal?.handlerItemClick(position)
                binding.rvFullPics.scrollToPosition(position)
                binding.toolbarFullPics.subtitle = "${position!! + 1} / ${list.size} pics"
            }
        })

        binding.vpFullPics.setPageTransformer(object : ViewPager2.PageTransformer {
            override fun transformPage(page: View, position: Float) {
                //rotates the pages around their Z axis by 30 degrees
                //                page.rotation = position * -30
                //These lines perform a scaling effect from and to 50%:
                val normalizedposition = Math.abs(Math.abs(position) - 1)
                page.scaleX = normalizedposition / 2 + 0.5f
                page.scaleY = normalizedposition / 2 + 0.5f
            }

        })

    }

    private fun initHorizontalRecyclerView() {
        pictureAdapterHorizontal = PictureAdapter("horiz", list!!, this)
        var linearLayoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false
        )
        binding.rvFullPics.layoutManager = linearLayoutManager
        binding.rvFullPics.adapter = pictureAdapterHorizontal
        pictureAdapterHorizontal?.handlerItemClick(selectedPosition!!)
        binding.rvFullPics.smoothScrollToPosition(selectedPosition!!)
        var lastPos = list!!.size - 1

        if (selectedPosition!! >= lastPos) {
            binding.rvFullPics.smoothScrollToPosition(selectedPosition!!)
        } else {
            binding.rvFullPics.smoothScrollToPosition(selectedPosition!! + 1)
        }
    }

    override fun onHorizontalPicClicked(position: Int) {
        binding.vpFullPics.currentItem = position
        binding.toolbarFullPics.subtitle = "${position!! + 1} / ${list.size} pics"
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_full_pics, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_download -> {
                val pic = list[binding.vpFullPics.currentItem]
                val fileUrl = pic.big
                val fileName = "${pic.name}.jpg"
                Downloads.showDialogDownload(
                    requireActivity(), fileUrl, fileName, "Do you want download this photo?"
                )
            }
            R.id.action_slide_show -> {
                val slideAuto = AutoSlidePicsDialogFragment.newInstance(
                    list, binding.vpFullPics.currentItem
                )
                val manager = (requireContext() as AppCompatActivity).supportFragmentManager
                slideAuto.show(manager, "auto_slide")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(list: ArrayList<ZPPicture>, position: Int) =
            FullPicsDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, list)
                    putInt(ARG_PARAM2, position)
                }
            }
    }
}