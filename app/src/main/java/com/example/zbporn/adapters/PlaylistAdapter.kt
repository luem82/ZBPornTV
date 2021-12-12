package com.example.zbporn.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zbporn.R
import com.example.zbporn.activities.PlaylistDetailActivity
import com.example.zbporn.databinding.ItemLoadingBinding
import com.example.zbporn.databinding.ItemPlaylistBinding
import com.example.zbporn.interfaces.IVideoLoadMore
import com.example.zbporn.models.ZBPlaylist
import com.example.zbporn.utils.Animator

class PlaylistAdapter(
    var recyclerView: RecyclerView,
    var list: MutableList<ZBPlaylist>
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
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager.itemCount
                lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
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
        if (list[position].title.equals("null_playlist")) {
            return VIEW_TYPE_LOADING
        } else {
            return VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        var view: View? = null
        if (viewType == VIEW_TYPE_ITEM) {
            val biding = ItemPlaylistBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            viewHolder = PlaylistViewHolder(biding)
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
        if (holder is PlaylistViewHolder) {
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

    inner class PlaylistViewHolder(val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindVideo(playlist: ZBPlaylist) {

            binding.playlistTitle.text = playlist.title
            binding.playlistCount.text = playlist.count

            Glide.with(binding.playlistThumb)
                .load(playlist.thumb)
//                .load("https/://cdnth.zbporn.tv/images/flags_svg/gb.svg")
                .into(binding.playlistThumb)

            Glide.with(binding.playlistPrev1)
                .load(playlist.prevew1)
//                .load("https/://cdnth.zbporn.tv/images/flags_svg/gb.svg")
                .into(binding.playlistPrev1)

            Glide.with(binding.playlistPrev2)
                .load(playlist.preview2)
//                .load("https/://cdnth.zbporn.tv/images/flags_svg/gb.svg")
                .into(binding.playlistPrev2)

            binding.root.setOnClickListener {
                val intent = Intent(it.context, PlaylistDetailActivity::class.java)
                intent.putExtra("playlist", playlist)
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