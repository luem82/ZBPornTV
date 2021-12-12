package com.example.zbporn.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zbporn.R
import com.example.zbporn.databinding.ItemPicture1Binding
import com.example.zbporn.databinding.ItemPicture2Binding
import com.example.zbporn.databinding.ItemPicture3Binding
import com.example.zbporn.databinding.ItemPicture4Binding
import com.example.zbporn.fragments.FullPicsDialogFragment
import com.example.zbporn.interfaces.IHorizontalPicsClickListener
import com.example.zbporn.models.ZPPicture
import com.example.zbporn.utils.Animator
import com.flaviofaria.kenburnsview.RandomTransitionGenerator


class PictureAdapter(
    val type: String,
    val list: MutableList<ZPPicture>,
    val iHorizontalPicsClickListener: IHorizontalPicsClickListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var oldposition = -1
    var indexPosition = -1

    inner class PictureViewHolder_1(val binding: ItemPicture1Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindPíc(picture: ZPPicture) {

            Glide.with(binding.picThumb1)
                .load(picture.small)
//                .load("https://cdn.nguyenkimmall.com/images/thumbnails/180/180/detailed/634/10045062-smart-tivi-samsung-4k-55-inch-ua55tu8500kxxv-1_uf0b-is.jpg")
                .placeholder(R.drawable.ic_logo)
                .error((R.drawable.ic_logo))
                .into(binding.picThumb1)

            binding.root.setOnClickListener {
                val dialog = FullPicsDialogFragment.newInstance(
                    list as ArrayList<ZPPicture>, adapterPosition
                )
                val manager = (it.context as AppCompatActivity).supportFragmentManager
                dialog.show(manager, "full_pics")
            }
        }
    }

    inner class PictureViewHolder_2(val binding: ItemPicture2Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindPíc(picture: ZPPicture) {

            Glide.with(binding.picThumb2)
                .load(picture.small)
//                .load("https://cdn.nguyenkimmall.com/images/thumbnails/180/180/detailed/634/10045062-smart-tivi-samsung-4k-55-inch-ua55tu8500kxxv-1_uf0b-is.jpg")
                .placeholder(R.drawable.ic_logo)
                .error((R.drawable.ic_logo))
                .into(binding.picThumb2)

            binding.root.setOnClickListener {
                handlerItemClick(adapterPosition)
            }
        }
    }

    inner class PictureViewHolder_3(val binding: ItemPicture3Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindPíc(picture: ZPPicture) {

            Glide.with(binding.picThumb3)
                .load(picture.small)
                .fitCenter()
//                .load("https://cdn.nguyenkimmall.com/images/thumbnails/180/180/detailed/634/10045062-smart-tivi-samsung-4k-55-inch-ua55tu8500kxxv-1_uf0b-is.jpg")
                .placeholder(R.drawable.ic_logo)
                .error((R.drawable.ic_logo))
                .into(binding.picThumb3)
        }
    }

    inner class PictureViewHolder_4(val binding: ItemPicture4Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindPíc(picture: ZPPicture) {

            Glide.with(binding.picThumb4)
                .load(picture.small)
                .fitCenter()
//                .load("https://cdn.nguyenkimmall.com/images/thumbnails/180/180/detailed/634/10045062-smart-tivi-samsung-4k-55-inch-ua55tu8500kxxv-1_uf0b-is.jpg")
                .placeholder(R.drawable.ic_logo)
                .error((R.drawable.ic_logo))
                .into(binding.picThumb4)

            val adi = AccelerateDecelerateInterpolator()
            val generator = RandomTransitionGenerator(1500, adi)
            binding.picThumb4.setTransitionGenerator(generator)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (type.equals("default")) {
            val binding = ItemPicture1Binding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return PictureViewHolder_1(binding)
        } else if (type.equals("horiz")) {
            val binding = ItemPicture2Binding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return PictureViewHolder_2(binding)
        } else if (type.equals("pager")) {
            val binding = ItemPicture3Binding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return PictureViewHolder_3(binding)
        } else {
            val binding = ItemPicture4Binding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return PictureViewHolder_4(binding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is PictureViewHolder_1) {
            holder.bindPíc(list[position])
            if (position > oldposition) {
                Animator.animateRecyclerView(holder, true)
            } else {
                Animator.animateRecyclerView(holder, false)
            }
            oldposition = position
        } else if (holder is PictureViewHolder_2) {
            holder.bindPíc(list[position])
            var with = holder.binding.root.width
            var height = holder.binding.root.height
            if (indexPosition == position) {
                holder.binding.picThumb2Front.visibility = View.INVISIBLE
                holder.binding.picIndicator.visibility = View.VISIBLE
                holder.binding.picThumb2.borderWidth = 4f
            } else {
                holder.binding.picThumb2Front.visibility = View.VISIBLE
                holder.binding.picIndicator.visibility = View.INVISIBLE
                holder.binding.picThumb2.borderWidth = 0f
            }
        } else if (holder is PictureViewHolder_3) {
            holder.bindPíc(list[position])
        } else if (holder is PictureViewHolder_4) {
            holder.bindPíc(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun handlerItemClick(position: Int) {
        indexPosition = position
        iHorizontalPicsClickListener?.onHorizontalPicClicked(position)
        notifyDataSetChanged()
    }

}