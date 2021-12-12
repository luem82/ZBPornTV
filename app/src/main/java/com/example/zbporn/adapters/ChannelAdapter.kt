package com.example.zbporn.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zbporn.R
import com.example.zbporn.activities.ChannelDetailActivity
import com.example.zbporn.databinding.ItemChannelBinding
import com.example.zbporn.databinding.ItemLoadingBinding
import com.example.zbporn.models.ZBChannel
import com.example.zbporn.utils.Animator
import com.example.zbporn.interfaces.IVideoLoadMore

class ChannelAdapter(
    var recyclerView: RecyclerView,
    var list: MutableList<ZBChannel>
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
        if (list[position].title.equals("null_channel")) {
            return VIEW_TYPE_LOADING
        } else {
            return VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        var view: View? = null
        if (viewType == VIEW_TYPE_ITEM) {
            val biding = ItemChannelBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            viewHolder = ChannelViewHolder(biding)
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
        if (holder is ChannelViewHolder) {
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

    inner class ChannelViewHolder(val binding: ItemChannelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindVideo(channel: ZBChannel) {

            binding.channelTitle.text = channel.title
            binding.channelCount.text = channel.count
            binding.channelRate.text=channel.rate

            Glide.with(binding.channelThumb)
                .load(channel.thumb)
//                .load("https://cdn.nguyenkimmall.com/images/thumbnails/180/180/detailed/634/10045062-smart-tivi-samsung-4k-55-inch-ua55tu8500kxxv-1_uf0b-is.jpg")
                .into(binding.channelThumb)

            binding.root.setOnClickListener {
                val intent=Intent(it.context,ChannelDetailActivity::class.java)
                intent.putExtra("channel",channel)
                it.context.startActivity(intent)
                (it.context as AppCompatActivity).overridePendingTransition(
                    R.anim.slide_in_right, R.anim.face_out
                )
            }
        }
    }

    class LoadViewHolder(val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView() {
        }
    }

}