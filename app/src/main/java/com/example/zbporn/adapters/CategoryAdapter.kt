package com.example.zbporn.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zbporn.R
import com.example.zbporn.activities.SearchVideoActivity
import com.example.zbporn.databinding.ItemCategoryBinding
import com.example.zbporn.models.ZBCategory
import com.example.zbporn.utils.Animator

class CategoryAdapter(
    val list: MutableList<ZBCategory>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var oldposition = -1

    class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindCategory(category: ZBCategory) {

            binding.cateTitle.text = category.title
            binding.cateCount.text = category.count

            Glide.with(binding.cateThumb)
                .load(category.thumb)
//                .load("https://cdn.nguyenkimmall.com/images/thumbnails/180/180/detailed/634/10045062-smart-tivi-samsung-4k-55-inch-ua55tu8500kxxv-1_uf0b-is.jpg")
                .into(binding.cateThumb)

            binding.root.setOnClickListener {
                val intent = Intent(it.context, SearchVideoActivity::class.java)
                val key = category.title
                val url = category.href.substringBeforeLast("/")
                intent.putExtra("type", "video")
                intent.putExtra("key", key)
                intent.putExtra("url", url)
                it.context.startActivity(intent)
                (it.context as AppCompatActivity).overridePendingTransition(
                    R.anim.slide_in_right, R.anim.face_out
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bindCategory(list[position])
        if (position > oldposition) {
            Animator.animateRecyclerView(holder, true)
        } else {
            Animator.animateRecyclerView(holder, false)
        }
        oldposition = position
    }

    override fun getItemCount(): Int {
        return list.size
    }
}