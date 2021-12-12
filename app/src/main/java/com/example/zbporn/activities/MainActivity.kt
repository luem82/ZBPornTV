package com.example.zbporn.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.zbporn.R
import com.example.zbporn.databinding.ActivityMainBinding
import com.example.zbporn.databinding.CustomSearchDialogBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        binding.navView.menu.findItem(R.id.nav_settings)
            .setTitle(Html.fromHtml("<font color='#ffffff'>Settings</font>"))

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_videos, R.id.nav_albums, R.id.nav_categories,
                R.id.nav_channels, R.id.nav_models, R.id.nav_playlists
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            showDialogSearch()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialogSearch() {
        val custom = LayoutInflater.from(this)
            .inflate(R.layout.custom_search_dialog, binding.root, false)
        var dialog = AppCompatDialog(this)
        dialog.setContentView(custom)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = android.R.style.Animation_Dialog
        dialog.show()

        val bindingCD = CustomSearchDialogBinding.bind(custom)
        bindingCD.dialogClose.setOnClickListener { dialog.dismiss() }

        bindingCD.dialogVideo.setOnClickListener {
            if (bindingCD.dialogEditText.text.toString().isNullOrEmpty()) {
                Toast.makeText(applicationContext, "Enter key search", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, SearchVideoActivity::class.java)
                val key = bindingCD.dialogEditText.text.toString().trim()
                val url = "https://zbporn.tv/search/${key.replace(" ", "+")}"
                intent.putExtra("type", "video")
                intent.putExtra("key", key)
                intent.putExtra("url", url)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.face_out)
                dialog.dismiss()
            }
        }

        bindingCD.dialogAlbum.setOnClickListener {
            if (bindingCD.dialogEditText.text.toString().isNullOrEmpty()) {
                Toast.makeText(applicationContext, "Enter key search", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, SearchVideoActivity::class.java)
                val key = bindingCD.dialogEditText.text.toString().trim()
                val url = "https://zbporn.tv/albums/search/${key.replace(" ", "+")}"
                intent.putExtra("type", "album")
                intent.putExtra("key", key)
                intent.putExtra("url", url)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.face_out)
                dialog.dismiss()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}