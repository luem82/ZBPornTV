package com.example.zbporn.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.zbporn.R
import com.example.zbporn.adapters.PictureAdapter
import com.example.zbporn.databinding.ActivityAlbumDetailBinding
import com.example.zbporn.models.ZBAlbum
import com.example.zbporn.utils.GetData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlbumDetailActivity : AppCompatActivity() {

    private lateinit var biding: ActivityAlbumDetailBinding
    private lateinit var album: ZBAlbum


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        biding = ActivityAlbumDetailBinding.inflate(layoutInflater)
        setContentView(biding.root)

        album = intent.getSerializableExtra("album") as ZBAlbum
        setSupportActionBar(biding.toolbarAlbumDetail)
        supportActionBar?.title = "Album Detail: ${album.count.replace("p", "P")}"
        biding.albumTitle.text = album.title
        biding.toolbarAlbumDetail.setNavigationOnClickListener { onBackPressed() }

        CoroutineScope(Dispatchers.Main).launch {
            biding.rvPictures.layoutManager = StaggeredGridLayoutManager(
                3, StaggeredGridLayoutManager.VERTICAL
            )
            biding.rvPictures.setHasFixedSize(true)
            biding.rvPictures.adapter = PictureAdapter(
                "default", GetData.getPictures(album.href), null
            )
            biding.pbAlbumDetail.smoothToHide()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.face_in, R.anim.slide_out_right)
    }
}