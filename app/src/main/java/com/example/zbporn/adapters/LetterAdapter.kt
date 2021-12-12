package com.example.zbporn.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zbporn.R
import com.example.zbporn.databinding.ItemLetterBinding
import com.example.zbporn.interfaces.ILetterListener


class LetterAdapter(
    val iLetterListener: ILetterListener,
    val arrayList: ArrayList<String>
) : RecyclerView.Adapter<LetterAdapter.LetterViewHolder>() {

    var positionSelected = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        return LetterViewHolder(
            ItemLetterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        holder.bindLetter(arrayList[position])
        if (position == positionSelected) {
            holder.binding.tvLetter.setBackgroundResource(R.drawable.background_letter_2)
        } else {
            holder.binding.tvLetter.setBackgroundResource(R.drawable.background_letter_1)
        }
    }

    inner class LetterViewHolder(
        val binding: ItemLetterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindLetter(s: String) {
            binding.tvLetter.text = s
            binding.tvLetter.setOnClickListener {
                handlerItemClick(adapterPosition, s)
            }
        }
    }

    fun handlerItemClick(position: Int, s: String) {
        positionSelected = position
        iLetterListener.onLetterClicked(s)
        notifyDataSetChanged()
    }

}