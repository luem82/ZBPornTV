package com.example.zbporn.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.zbporn.adapters.PictureAdapter
import com.example.zbporn.databinding.FragmentDialogAutoSlidePicsBinding
import com.example.zbporn.models.ZPPicture
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AutoSlidePicsDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogAutoSlidePicsBinding? = null
    private val binding get() = _binding!!
    private var selectedPosition: Int? = null
    private lateinit var list: ArrayList<ZPPicture>
    private lateinit var autoSlideAdapter: PictureAdapter
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedPosition = it.getInt(ARG_PARAM2)
            list = it.getSerializable(ARG_PARAM1) as ArrayList<ZPPicture>
        }
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentDialogAutoSlidePicsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        autoSlideAdapter = PictureAdapter("auto", list, null)
        binding.vpAutoSlide.adapter = autoSlideAdapter
        binding.vpAutoSlide.currentItem = selectedPosition!!
        binding.vpAutoSlide.setPageTransformer(object : ViewPager2.PageTransformer {
            override fun transformPage(page: View, position: Float) {
                //rotates the pages around their Z axis by 30 degrees
                //                page.rotation = position * -30
                //These lines perform a scaling effect from and to 50%:
                val normalizedposition = Math.abs(Math.abs(position) - 1)
                page.scaleX = normalizedposition / 2 + 0.5f
                page.scaleY = normalizedposition / 2 + 0.5f
            }

        })

        timer = Timer()
        timer.scheduleAtFixedRate(SlideTimer(), 5000, 5500)

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (dialog != null && timer != null) {
            timer.cancel()
        }
    }

    inner class SlideTimer() : TimerTask() {

        override fun run() {
            activity!!.runOnUiThread(Runnable {
                if (binding.vpAutoSlide.getCurrentItem() < list!!.size - 1) {
                    binding.vpAutoSlide.setCurrentItem(binding.vpAutoSlide.getCurrentItem() + 1)
                } else {
                    binding.vpAutoSlide.setCurrentItem(0)
//                    showDialogEndSlide()
//                    timer.cancel()
                }
            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(list: ArrayList<ZPPicture>, position: Int) =
            AutoSlidePicsDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, list)
                    putInt(ARG_PARAM2, position)
                }
            }
    }
}