package com.example.zbporn.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.zbporn.utils.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var preferrances: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferrances = PreferenceManager(applicationContext)
        val isLock = preferrances.getBoolean("islock")

        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            if (isLock) {
                startActivity(Intent(this@SplashScreenActivity, PasscodeActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}