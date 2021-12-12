package com.example.zbporn.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.zbporn.R
import com.example.zbporn.activities.FullVideoActivity
import com.example.zbporn.databinding.FragmentPreviewVideoBinding
import com.example.zbporn.models.ZBVideo
import com.example.zbporn.utils.Downloads
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val ARG_PARAM1 = "param1"

class PreviewVideoFragment : BottomSheetDialogFragment() {

    private var video: ZBVideo? = null
    private var _binding: FragmentPreviewVideoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            video = it.getSerializable(ARG_PARAM1) as ZBVideo?
        }
        setStyle(STYLE_NORMAL, R.style.BottomSheetTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPreviewVideoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        binding.botVideoView.setVideoPath("https://static-clst.avgle.com/videos/tmb3/96789/preview.mp4")
        binding.botVideoView.setVideoPath(video?.preview)
        binding.botVideoView.setOnPreparedListener { mediaPlayer ->
            binding.botLoading.smoothToHide()
            binding.botFront.visibility = View.INVISIBLE
            mediaPlayer.isLooping = true
            mediaPlayer.setVolume(0f, 0f)
        }

        binding.botVideoView.start()

        binding.botVideoTitle.text = video?.title
        binding.botVideoDate.text = video?.date
        binding.botVideoRate.text = video?.likes
        binding.botVideoDuration.text = video?.duration

        binding.botClose.setOnClickListener { dismiss() }

        binding.botFullVideo.setOnClickListener {
            val intent = Intent(context, FullVideoActivity::class.java)
            intent.putExtra("video", video)
            startActivity(intent)
            dismiss()
        }

        binding.botDownloadVideo.setOnClickListener {
            val fileUrl = video!!.preview
            val fileName = "${video!!.title}_Preview.mp4"
            Downloads.showDialogDownload(
                requireActivity(), fileUrl, fileName, "Do you want download this video preview?"
            )
            dismiss()
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding.botVideoView.stopPlayback()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(zbVideo: ZBVideo) =
            PreviewVideoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, zbVideo)
                }
            }
    }
}