package com.example.zbporn.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zbporn.databinding.ItemLoadingBinding
import com.example.zbporn.databinding.ItemVideoBinding
import com.example.zbporn.fragments.PreviewVideoFragment
import com.example.zbporn.interfaces.IVideoLoadMore
import com.example.zbporn.models.ZBVideo
import com.example.zbporn.utils.Animator

class VideoAdapter(
    var recyclerView: RecyclerView,
    var list: MutableList<ZBVideo>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var oldposition = 0
    var VIEW_TYPE_ITEM = 0
    var VIEW_TYPE_LOADING = 1
    var visibleThresold = 5
    var lastVisibleItem = 0
    var totalItemCount = 0
    var isLoading = false
    var looadMore: IVideoLoadMore? = null

    init {
        val gridLayoutManager = recyclerView.layoutManager as GridLayoutManager
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            //Each item occupies 1 span by default.
            override fun getSpanSize(p0: Int): Int {
                return when (getItemViewType(p0)) {
                    //returns total no of spans, to occupy full sapce for progressbar
                    VIEW_TYPE_LOADING -> gridLayoutManager.spanCount
                    VIEW_TYPE_ITEM -> 1
                    else -> -1
                }
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = gridLayoutManager.itemCount
                lastVisibleItem = gridLayoutManager.findLastCompletelyVisibleItemPosition()
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThresold)) {
                    if (looadMore != null) {
                        looadMore!!.onLoadMore()
                    }
                    isLoading = true
                }
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position].title.equals("null_video")) {
            return VIEW_TYPE_LOADING
        } else {
            return VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        var view: View? = null
        if (viewType == VIEW_TYPE_ITEM) {
            val biding = ItemVideoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            viewHolder = VideoViewHolder(biding)
        } else if (viewType == VIEW_TYPE_LOADING) {
            val biding = ItemLoadingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            viewHolder = LoadViewHolder(biding)
        }
        return viewHolder!!
    }

    fun setLoadMore(loaded: IVideoLoadMore) {
        this.looadMore = loaded
    }

    fun setLoaded() {
        this.isLoading = false
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VideoViewHolder) {
            holder.bindVideo(list[position])
            if (position > oldposition) {
                Animator.animateRecyclerView(holder, true)
            } else {
                Animator.animateRecyclerView(holder, false)
            }
            oldposition = position
        } else if (holder is LoadViewHolder) {
            holder.bindView()
        }
    }

    inner class VideoViewHolder(val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindVideo(video: ZBVideo) {

            binding.videoTitle.text = video.title
            binding.videoDuration.text = video.duration

            Glide.with(binding.videoThumb)
                .load(video.thumb)
//                .load("https/://cdnth.zbporn.tv/images/flags_svg/gb.svg")
                .into(binding.videoThumb)

            binding.root.setOnClickListener {
                val botDialog = PreviewVideoFragment.newInstance(video)
                val manager = (it.context as AppCompatActivity).supportFragmentManager
                botDialog.show(manager, "preview")
            }
        }
    }

    class LoadViewHolder(val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView() {
        }
    }

}