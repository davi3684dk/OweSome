package com.owesome.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import androidx.core.graphics.scale


class ImageUtil {
    companion object Util {
        // Source - https://stackoverflow.com/a
        // Posted by jagadishlakkurcom jagadishlakk
        // Retrieved 2025-11-15, License - CC BY-SA 4.0
        fun decodeBase64ToImageBitmap(base64: String): ImageBitmap? {
            return try {
                val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                bitmap.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }

        fun imageBitmapToBase64(imageBitmap: ImageBitmap, maxSize: Int = 512): String? {
            try {
                val bitmap = imageBitmap.asAndroidBitmap()

                val width = bitmap.width
                val height = bitmap.height
                val largestSide = maxOf(width, height)

                val scale = if (largestSide > maxSize) {
                    maxSize.toFloat() / largestSide.toFloat()
                } else {
                    1f
                }

                val scaledBitmap =
                    if (scale < 1f) {
                        bitmap.scale((width * scale).toInt(), (height * scale).toInt())
                    } else bitmap

                val byteArrayOutputStream = ByteArrayOutputStream()
                scaledBitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    80,
                    byteArrayOutputStream
                )

                return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
            } catch (e: Exception) {
                return null
            }
        }

        fun uriToImageBitmap(uri: Uri, context: Context): ImageBitmap? {
            // Source - https://stackoverflow.com/a
            // Posted by Hascher
            // Retrieved 2025-11-15, License - CC BY-SA 4.0
            try {
                val imageUri: Uri = uri;
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)

                return bitmap.asImageBitmap()
            } catch (e: Exception) {
                return null
            }
        }
    }

}
