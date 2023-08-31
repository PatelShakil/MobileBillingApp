package com.mycampus.billingapp.common

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.mycampus.billingapp.data.models.BillItemCollectionExcel
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
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
        const val EXCEL_DIR = "$STORAGE_DIR/Excel"
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
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS
                )
            } else {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            }
        }
        fun requestPermissionsIfNecessary(activity: Activity) {
            val permissions = getPermissions()

            val notGrantedPermissions = permissions.filter {
                ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
            }.toTypedArray()

            if (notGrantedPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(activity, notGrantedPermissions, 1)
            }
        }

        fun generateExcelSheet(dataList: List<BillItemCollectionExcel>) {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Bill Data")

            val headerCellTexts = listOf(
                "ID",
                "Customer Name",
                "Customer Mobile",
                "Customer Email",
                "Customer Address",
                "Bill Number",
                "Payment Mode",
                "Tax(%)",
                "Total Amount(Rs.)",
                "Paid Amount(Rs.)",
                "Balance Amount(Rs.)",
                "Discount(Rs.)",
                "Remarks",
                "Creation Date",
                "Transaction Date",
                "Created By",
                "Sync",
                "Item Name",
                "Item Amount(Rs.)",
                "Item Creation Date",
                "Item Created By"
            )

            val boldHeaderRow = sheet.createRow(0)
            for ((index, headerText) in headerCellTexts.withIndex()) {
                val cell = boldHeaderRow.createCell(index)
                cell.setCellValue(headerText)
                // Set any other cell styling you need here
                val boldFont: Font = workbook.createFont()
                boldFont.bold = true

                val boldCellStyle: CellStyle = workbook.createCellStyle()
                boldCellStyle.setFont(boldFont)
                cell.cellStyle = boldCellStyle as XSSFCellStyle?
            }


            var rowNum = 1
            for (data in dataList) {
                var mainDataRow = sheet.createRow(rowNum)
                mainDataRow.createCell(0).setCellValue(data.id.toDouble())
                mainDataRow.createCell(1).setCellValue(data.customer.name)
                mainDataRow.createCell(2).setCellValue(data.customer.mobile)
                mainDataRow.createCell(3).setCellValue(data.customer.email)
                mainDataRow.createCell(4).setCellValue(data.customer.address)
                mainDataRow.createCell(5).setCellValue(data.bill_no)
                mainDataRow.createCell(6).setCellValue(data.bill_pay_mode)
                mainDataRow.createCell(7).setCellValue(data.tax)
                mainDataRow.createCell(8).setCellValue(data.total_amount)
                mainDataRow.createCell(9).setCellValue(data.paid_amount)
                mainDataRow.createCell(10).setCellValue(data.balance_amount)
                mainDataRow.createCell(11).setCellValue(data.discount)
                mainDataRow.createCell(12).setCellValue(data.remarks)
                mainDataRow.createCell(13).setCellValue((convertLongToDate(data.creation_date,"dd-MM-yyyy")))
                mainDataRow.createCell(14).setCellValue((convertLongToDate(data.bill_date,"dd-MM-yyyy")))
                mainDataRow.createCell(15).setCellValue(data.created_by)
                mainDataRow.createCell(16).setCellValue(if(data.is_sync) "Yes" else "No")

                var isNewRow = false
                for (item in data.itemList) {
                    if(isNewRow) {
                        mainDataRow = sheet.createRow(rowNum)
                    }
                    mainDataRow.createCell(17).setCellValue(item.item_name)
                    mainDataRow.createCell(18).setCellValue(item.item_amount)
                    mainDataRow.createCell(19).setCellValue(convertLongToDate(data.creation_date,"dd-MM-yyyy"))
                    mainDataRow.createCell(20).setCellValue(item.created_by)
                    rowNum++
                    isNewRow = true
                }
            }

                setAutoSizeColumns(sheet,21)
            val filePath = File("$EXCEL_DIR/billcollection.xlsx")
            if (!File(EXCEL_DIR).exists())
                File(EXCEL_DIR).mkdirs()

            if(filePath.exists())
                filePath.delete()

            if(!filePath.exists())
                filePath.createNewFile()

            val fileOutputStream = FileOutputStream(filePath)
            workbook.write(fileOutputStream)
            fileOutputStream.close()

            workbook.close()
        }
        private fun setAutoSizeColumns(sheet: XSSFSheet, columnCount: Int) {
            for (colIndex in 0 until columnCount) {
                val maxColumnWidth = 256 * 20 // Default column width (256 characters)
                val columnWidth = Math.min(maxColumnWidth, sheet.getColumnWidth(colIndex))
                sheet.setColumnWidth(colIndex, columnWidth)
            }
        }
        fun sendWhatsAppMessage(context: Context, message: String, mobile : String) {
            val whatsappPackage = "com.whatsapp"
            val whatsappBusinessPackage = "com.whatsapp.w4b"
            val phone = if(mobile.replace(" ","").startsWith("+"))
                mobile.replace(" ","")
            else
                "+91${mobile.replace(" ","")}"
            val whatsAppTxt = Uri.encode(message) // Encode the message

            // Create the WhatsApp message URI with the formatted message
            val whatsappUri = Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=$whatsAppTxt")

            // Create an Intent to open the WhatsApp chat
            val whatsappIntent = Intent(Intent.ACTION_VIEW, whatsappUri)
            try {
                whatsappIntent.setPackage(whatsappPackage)

            } catch (e: PackageManager.NameNotFoundException) {
                whatsappIntent.setPackage(whatsappBusinessPackage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                context.startActivity(whatsappIntent)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exceptions related to starting the activity
            }
        }
        fun sendWhatsAppMessageWithPDF(context: Context, message: String, phone: String, pdfFilePath: String, packageName: String) {
            val pdfFile = File("$STORAGE_DIR/$pdfFilePath.pdf")
            val pdfUri = FileProvider.getUriForFile(context, "com.mycampus.billingapp.fileprovider", pdfFile)

            if (pdfFile.exists()) {
                val whatsappIntent = Intent(Intent.ACTION_SEND)
                whatsappIntent.type = "application/pdf"
                whatsappIntent.type = "text/plain"
//        whatsappIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, message)
                whatsappIntent.putExtra("jid", "$phone@s.whatsapp.net") // Specify the phone number
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                whatsappIntent.setPackage(packageName)

                try {
                    context.startActivity(whatsappIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle exceptions related to starting the activity
                }
            } else {
                Log.d("PDFFILE",pdfFile.absolutePath)
                // Handle the case where the PDF file does not exist
            }
        }

    }
}

val MainColor = Color(0xFF00638E)
val LightMainColor = Color(0xFFD2E1F1)