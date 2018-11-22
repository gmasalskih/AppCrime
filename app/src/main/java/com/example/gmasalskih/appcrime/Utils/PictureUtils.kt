package com.example.gmasalskih.appcrime.Utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point

class PictureUtils {
    companion object {
        fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
            var options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(path, options)

            val srcWidth = options.outWidth
            val srcHeight = options.outHeight

            var myInSampleSize = 1L

            if (srcHeight > destHeight || srcWidth > destWidth) {
                val heightScale = srcHeight / destHeight
                val widthScale = srcWidth / destWidth
                myInSampleSize =
                        Math.round(if (heightScale > widthScale) heightScale.toDouble() else widthScale.toDouble())
            }
            options = BitmapFactory.Options().apply {
                inSampleSize = myInSampleSize.toInt()
            }
            return BitmapFactory.decodeFile(path, options)
        }

        fun getScaledBitmap(path: String, activity: Activity): Bitmap {
            val size = Point()
            activity.windowManager.defaultDisplay.getSize(size)
            return getScaledBitmap(path, size.x, size.y)
        }
    }
}