package com.example.zbporn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zbporn.adapters.LetterAdapter
import com.example.zbporn.adapters.ModelAdapter
import com.example.zbporn.databinding.FragmentModelsBinding
import com.example.zbporn.interfaces.ILetterListener
import com.example.zbporn.interfaces.IVideoLoadMore
import com.example.zbporn.models.ZBModel
import com.example.zbporn.utils.Consts
import com.example.zbporn.utils.GetData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModelsFragment : Fragment(), ILetterListener, IVideoLoadMore {

    private var _binding: FragmentModelsBinding? = null
    private val binding get() = _binding!!
    var page = 1
    var limit = 0
    var letter = ""
    var urlGet = "https://zbporn.tv/performers"
    private lateinit var list: MutableList<ZBModel>
    private lateinit var modelAdapter: ModelAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentModelsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLetters()
        initModels()
    }

    private fun initModels() {
        val gridLayoutManager = GridLayoutManager(context, 2)
        binding.rvModels.layoutManager = gridLayoutManager
        binding.rvModels.setHasFixedSize(true)
        list = mutableListOf<ZBModel>()
        modelAdapter = ModelAdapter(binding.rvModels, list)
        binding.rvModels.adapter = modelAdapter
        CoroutineScope(Dispatchers.Main).launch {
            list.addAll(GetData.getModels("${urlGet}/${page}/"))
            modelAdapter.notifyDataSetChanged()
            limit = list[0].limit
            binding.pbModels.smoothToHide()
        }
        modelAdapter.setLoadMore(this)
    }

    override fun onLoadMore() {
        page++
        if (page <= limit) {
            list.add(
                ZBModel(
                    "", "null_model", "", "",
                    "", "", "", 0
                )
            )
            modelAdapter.notifyItemInserted(list.size - 1)


            var url = ""
            if (letter.isNullOrEmpty()) {
                url = "https://zbporn.tv/performers/${page}/"
            } else {
                url = "https://zbporn.tv/performers/${page}/?abc=${letter}"
            }
            CoroutineScope(Dispatchers.Main).launch {
                val temp = GetData.getModels(url)
                list.removeAt(list.size - 1)
                modelAdapter.notifyItemRemoved(list.size)
                list.addAll(temp)
                modelAdapter.setLoaded()
            }
        } else {
            Toast.makeText(context, "End Of List", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initLetters() {
        binding.rvLetters.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false
        )
        binding.rvLetters.setHasFixedSize(true)
        binding.rvLetters.adapter = LetterAdapter(this, Consts.LIST_LETTER)
    }

    override fun onLetterClicked(key: String) {
        letter = key
        list.clear()
        binding.rvModels.removeAllViews()
        modelAdapter.notifyDataSetChanged()
        page = 1
        limit = 0
        binding.pbModels.smoothToShow()
        var url = "${urlGet}/${page}/?abc=${letter}"
        CoroutineScope(Dispatchers.Main).launch {
            list.addAll(GetData.getModels("${url}"))
            modelAdapter.notifyDataSetChanged()
            limit = list[0].limit
            binding.pbModels.smoothToHide()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}