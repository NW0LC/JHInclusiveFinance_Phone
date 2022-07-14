package com.inclusive.finance.jh.utils

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import java.io.File

object ImageUtil {

    /**
     * 删除文件
     * @return
     */
    fun deleteFileUri(activity: FragmentActivity, imgPath: String ) {
        val cursor: Cursor = MediaStore.Images.Media.query(
            activity.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf<String>(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA.toString() + "=?",
            arrayOf(imgPath),
            null
        )

        try {
            if (cursor.moveToFirst()) {
                val id: Long = cursor.getLong(0)
                val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val uri: Uri = ContentUris.withAppendedId(contentUri, id)

                val count: Int = activity.contentResolver.delete(uri, null, null)


            } else {
                val isSuccess = File(imgPath).delete()

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e is RecoverableSecurityException) {
                val DELETE_REQUEST_CODE = 1001

            } else {

            }
        }
    }
}
