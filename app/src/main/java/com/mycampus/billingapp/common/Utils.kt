package com.mycampus.billingapp.common

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.view.View
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class Utils {
    companion object{
        const val STORAGE_DIR = "/storage/emulated/0/Download/myCampus"
        const val BACKUP_DIR = "$STORAGE_DIR/Backup"
    fun generateRandomValue(char : Int): String {
        val characterSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        val random = Random(System.currentTimeMillis())
        val randomValue = StringBuilder()

        repeat(char) {
            val randomIndex = random.nextInt(characterSet.length)
            randomValue.append(characterSet[randomIndex])
        }

        return randomValue.toString()
    }
    fun convertLongToDate(timestamp: Long, format: String): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(date)
    }
        fun saveBitmapToFile(pngBitmap: Bitmap, pdfFile: File) {

            val pdfDocument = PdfDocument()
            val pdfPageInfo = PdfDocument.PageInfo.Builder(pngBitmap.width, pngBitmap.height, 1).create()
            val pdfPage = pdfDocument.startPage(pdfPageInfo)

            pdfPage.canvas.drawBitmap(pngBitmap, 0f, 0f, null)

            pdfDocument.finishPage(pdfPage)

            try {
                val fileOutputStream = FileOutputStream(pdfFile)
                pdfDocument.writeTo(fileOutputStream)
                pdfDocument.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun saveFile(filename: String, bitmap: Bitmap) {
            val storageDir = File(STORAGE_DIR)
            val file = File(storageDir.absolutePath + "/$filename.pdf")
            if (!storageDir.exists())
                storageDir.mkdirs()
            if (!file.exists())
                file.createNewFile()
            saveBitmapToFile(bitmap, file)
        }
        fun createBitmapFromComposable(view: View, width: Int, height: Int): Bitmap {
            val location = IntArray(2)
            view.getLocationInWindow(location)

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            view.draw(canvas)

            return bitmap
        }

        fun viewPdf(activity: Activity, filename: String) {
            val pdfFile = File(
                "/storage/emulated/0/Download/myCampus/$filename.pdf"
            )

            if (pdfFile.exists()) {
                val uri = FileProvider.getUriForFile(
                    activity,
                    "${activity.packageName}.fileprovider",
                    pdfFile
                )

                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "application/pdf")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    activity.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // Handle the case where no PDF viewer app is installed
                }
            }
        }
        fun getPermissions(): Array<String> {
            return if (Build.VERSION.SDK_INT >= 33) {
                arrayOf(
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            } else {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }
}