package com.example.zbporn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.zbporn.adapters.CategoryAdapter
import com.example.zbporn.databinding.FragmentCategoriesBinding
import com.example.zbporn.utils.GetData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private val urlCategories = "https://zbporn.tv/categories/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            val list = GetData.getCategories(urlCategories)
            binding.rvCategories.layoutManager = GridLayoutManager(context, 2)
            binding.rvCategories.setHasFixedSize(true)
            binding.rvCategories.adapter = CategoryAdapter(list)
            binding.pbCategories.smoothToHide()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}