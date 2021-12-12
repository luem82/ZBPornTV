package com.example.zbporn.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.zbporn.R
import com.example.zbporn.utils.PreferenceManager
import com.hanks.passcodeview.PasscodeView
import kotlinx.android.synthetic.main.activity_passcode.*

class PasscodeActivity : AppCompatActivity() {

    private lateinit var preferrances: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passcode)

        preferrances = PreferenceManager(applicationContext)
        val isLock = preferrances.getBoolean("islock")
        val passcode = preferrances.getString("passcode")

        if (isLock) {
            passcodeView
                .setPasscodeLength(passcode!!.length)
                .setLocalPasscode(passcode).listener = object : PasscodeView.PasscodeViewListener {
                override fun onFail() {
                    Toast.makeText(application, "Wrong!!", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(number: String) {
                    startActivity(Intent(this@PasscodeActivity, MainActivity::class.java))
                    finish()
                }
            }
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}