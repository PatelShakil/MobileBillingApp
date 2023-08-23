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
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                )
            } else {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                )
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
                mainDataRow.createCell(14).setCellValue(data.created_by)
                mainDataRow.createCell(15).setCellValue(if(data.is_sync) "Yes" else "No")

                var isNewRow = false
                for (item in data.itemList) {
                    if(isNewRow) {
                        mainDataRow = sheet.createRow(rowNum)
                    }
                    mainDataRow.createCell(16).setCellValue(item.item_name)
                    mainDataRow.createCell(17).setCellValue(item.item_amount)
                    mainDataRow.createCell(18).setCellValue(convertLongToDate(data.creation_date,"dd-MM-yyyy"))
                    mainDataRow.createCell(19).setCellValue(item.created_by)
                    rowNum++
                    isNewRow = true
                }
            }

                setAutoSizeColumns(sheet,20)
            val filePath = File("$EXCEL_DIR/billcollection.xlsx")
            if (!File(EXCEL_DIR).exists())
                File(EXCEL_DIR).mkdirs()

            if(!filePath.exists())
                filePath.createNewFile()

            val fileOutputStream = FileOutputStream(filePath)
            workbook.write(fileOutputStream)
            fileOutputStream.close()

            workbook.close()
        }
        fun setAutoSizeColumns(sheet: XSSFSheet, columnCount: Int) {
            for (colIndex in 0 until columnCount) {
                val maxColumnWidth = 256 * 20 // Default column width (256 characters)
                val columnWidth = Math.min(maxColumnWidth, sheet.getColumnWidth(colIndex))
                sheet.setColumnWidth(colIndex, columnWidth)
            }
        }
    }
}

