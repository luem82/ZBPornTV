package com.example.zbporn.utils

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.WindowManager
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.example.zbporn.R
import com.example.zbporn.databinding.CustomDownloadDialogBinding
import com.example.zbporn.databinding.CustomSearchDialogBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener

object Downloads {

    fun showDialogDownload(activity: Activity, fileUrl: String, fileName: String, message: String) {
        val custom = LayoutInflater.from(activity).inflate(R.layout.custom_download_dialog, null)
        var dialog = AppCompatDialog(activity)
        dialog.setContentView(custom)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = android.R.style.Animation_Dialog
        dialog.show()

        val bindingCD = CustomDownloadDialogBinding.bind(custom)
        bindingCD.dialogMessage.text = message
        bindingCD.dialogClose.setOnClickListener { dialog.dismiss() }
        bindingCD.dialogCancel.setOnClickListener { dialog.dismiss() }
        bindingCD.dialogOk.setOnClickListener {
            checkPermissionAndDownload(activity, fileUrl, fileName)
            dialog.dismiss()
        }

    }

    private fun downloadFile(fileUrl: String, fileName: String, activity: Activity) {

        val downloadRequest = DownloadManager.Request(Uri.parse(fileUrl))
            .setTitle(fileName)
            .setDescription("Download")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager =
            activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(downloadRequest)
        Toast.makeText(activity, "Download is starting.", Toast.LENGTH_SHORT).show()
    }

    private fun checkPermissionAndDownload(activity: Activity, fileUrl: String, fileName: String) {
        Dexter.withActivity(activity)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    downloadFile(fileUrl, fileName, activity)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(activity, "Permission Denied.", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: com.karumi.dexter.listener.PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).check()
    }

}