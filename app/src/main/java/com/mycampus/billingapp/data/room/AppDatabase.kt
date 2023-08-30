package com.mycampus.billingapp.data.room

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.room.Database
import androidx.room.RoomDatabase
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.ContactItem
import com.mycampus.billingapp.data.room.entities.CustomerItem
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.concurrent.atomic.AtomicInteger

@Database(
    entities = [BillItemCollection::class, BillItem::class, CustomerItem::class, ContactItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun billingDao(): RoomDao
    // Backup the database to a file


    fun generateBillItemsColCsvRow(billCol: BillItemCollection): String {
        return "${billCol.id},${billCol.customerid},${billCol.bill_no},${billCol.bill_pay_mode}," +
                "${billCol.tax},${billCol.total_amount},${billCol.paid_amount},${billCol.balance_amount}," +
                "${billCol.discount},${billCol.remarks},${billCol.creation_date},${billCol.bill_date}," +
                "${billCol.created_by},${billCol.is_sync}"
    }

    fun generateBillItemCsvRow(billItem: BillItem): String {
        return "${billItem.id},${billItem.bill_info_id},${billItem.item_name}," +
                "${billItem.item_amount},${billItem.creation_date},${billItem.created_by}"
    }

    fun generateCustomerCsvRow(customerItem: CustomerItem): String {
        return "${customerItem.id},${customerItem.name},${customerItem.mobile}," +
                "${customerItem.email},${customerItem.address}"
    }

    fun parseCsvRowToBillItemsCol(values: List<String>): BillItemCollection {
        return BillItemCollection(
            id = values[0].toLong(),
            customerid = values[1].toLong(),
            bill_no = values[2],
            bill_pay_mode = values[3],
            tax = values[4].toDouble(),
            total_amount = values[5].toDouble(),
            paid_amount = values[6].toDouble(),
            balance_amount = values[7].toDouble(),
            discount = values[8].toDouble(),
            remarks = values[9],
            creation_date = values[10].toLong(),
            bill_date = values[11].toLong(),
            created_by = values[12],
            is_sync = values[13].toBoolean()
        )
    }

    fun parseCsvRowToCustomerItem(csvRow: List<String>): CustomerItem {
        val values = csvRow
        return CustomerItem(
            id = values[0].toLong(),
            name = values[1],
            mobile = values[2],
            email = values[3],
            address = values[4]
        )
    }

    fun parseCsvRowToBillItem(csvRow: List<String>): BillItem {
        val values = csvRow
        return BillItem(
            id = values[0].toLong(),
            bill_info_id = values[1].toLong(),
            item_name = values[2],
            item_amount = values[3].toDouble(),
            creation_date = values[4].toLong(),
            created_by = values[5]
        )
    }


    suspend fun backupDatabase(
        billitemCols: List<BillItemCollection>,
        billitems: List<BillItem>,
        customers: List<CustomerItem>,
        context: Context
    ) {

        val backupDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "/myCampus/Backup"
        )
        if (!backupDir.exists())
            backupDir.mkdirs()
        var backupBillItemCSVFile = File(backupDir.absolutePath + "/billitems.backup")
        var backupBillItemsColCSVFile = File(backupDir.absolutePath + "/billitemsCol.backup")
        var backupCustomersCSVFile = File(backupDir.absolutePath + "/customers.backup")

        if (backupBillItemCSVFile.exists())
            backupBillItemCSVFile.delete()
        if (backupBillItemsColCSVFile.exists())
            backupBillItemsColCSVFile.delete()
        if (backupCustomersCSVFile.exists())
            backupCustomersCSVFile.delete()

        if (!backupBillItemCSVFile.exists())
            backupBillItemCSVFile.createNewFile()
        if (!backupBillItemsColCSVFile.exists())
            backupBillItemsColCSVFile.createNewFile()
        if (!backupCustomersCSVFile.exists())
            backupCustomersCSVFile.createNewFile()

        try {
            val billItemsColfw = FileWriter(backupBillItemsColCSVFile)
            val billItemfw = FileWriter(backupBillItemCSVFile)
            val customersfw = FileWriter(backupCustomersCSVFile)

            billitemCols.forEach { billCol ->
                billItemsColfw.append("" + generateBillItemsColCsvRow(billCol) + "\n")
            }
            billitems.forEach { billItem ->
                billItemfw.append("" + generateBillItemCsvRow(billItem) + "\n")
            }
            customers.forEach { customerItem ->
                customersfw.append("" + generateCustomerCsvRow(customerItem) + "\n")
            }
            withContext(Dispatchers.IO) {
                billItemsColfw.flush()
                billItemsColfw.close()
                billItemfw.flush()
                billItemfw.close()
                customersfw.flush()
                customersfw.close()
            }
            Toast.makeText(context, "Backup Exported Successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // Restore the database from a backup file
    /*   suspend fun restoreDatabase(context: Context) {
           val backupDir = File(
               Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
               "/myCampus/Backup"
           )
           var backupBillItemCSVFile = File(backupDir.absolutePath + "/billitems.backup")
           var backupBillItemsColCSVFile = File(backupDir.absolutePath + "/billitemsCol.backup")
           var backupCustomersCSVFile = File(backupDir.absolutePath + "/customers.backup")

           if (backupBillItemsColCSVFile.exists()) {
               try {
                   val billitemcolscvr = CSVReader(withContext(Dispatchers.IO) {
                       FileReader(backupBillItemsColCSVFile.absoluteFile)
                   })
                   billitemcolscvr.readAll().forEach {
                       billingDao().insertBillItemCol(parseCsvRowToBillItemsCol(it))
                   }
                   val billitemcvr = CSVReader(withContext(Dispatchers.IO) {
                       FileReader(backupBillItemCSVFile.absoluteFile)
                   })
                   billitemcvr.readAll().forEach {
                       billingDao().insertBillItem(parseCsvRowToBillItem(it))
                   }
                   val customercvr = CSVReader(withContext(Dispatchers.IO) {
                       FileReader(backupCustomersCSVFile.absoluteFile)
                   })
                   customercvr.readAll().forEach {
                       billingDao().insertCustomer(parseCsvRowToCustomerItem(it))
                   }
                   billitemcolscvr.close()
                   billitemcvr.close()
                   customercvr.close()
                   Toast.makeText(context, "Backup Restored Successfully", Toast.LENGTH_SHORT).show()
               } catch (e: Exception) {
                   e.printStackTrace()
                   Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
               }
           }
       }*/
    suspend fun restoreDatabase(context: Context, progressListener: RestoreProgressListener) {
        val backupDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "/myCampus/Backup"
        )
        withContext(Dispatchers.IO){
        val backupBillItemCSVFile = File(backupDir.absolutePath + "/billitems.backup")
        val backupBillItemsColCSVFile = File(backupDir.absolutePath + "/billitemsCol.backup")
        val backupCustomersCSVFile = File(backupDir.absolutePath + "/customers.backup")
        val bif = context.contentResolver.openInputStream(FileProvider.getUriForFile(context,"com.mycampus.billingapp.fileprovider",backupBillItemCSVFile))
        val bicf = context.contentResolver.openInputStream(backupBillItemsColCSVFile.toUri())
        val bcf = context.contentResolver.openInputStream(backupCustomersCSVFile.toUri())
        val bifCache = File(context.cacheDir, "billitems.backup")
        bifCache.outputStream().use { op ->
            bif!!.copyTo(op)
        }
        val bicfCache = File(context.cacheDir, "billitemsCol.backup")
        bicfCache.outputStream().use {
            bicf!!.copyTo(it)
        }
        val bcfCache = File(context.cacheDir, "customers.backup")
        bcfCache.outputStream().use {
            bcf!!.copyTo(it)
        }
        bif!!.close()
        bicf!!.close()
        bcf!!.close()
        val totalRowCount = calculateTotalRowCount(
            bifCache,
            bicfCache,
            bcfCache
        )
        val currentRowCount = AtomicInteger(0)

        if (backupBillItemsColCSVFile.exists()) {
            restoreCsvFile(
                backupBillItemsColCSVFile,
                currentRowCount,
                totalRowCount,
                progressListener
            ) { row ->
                GlobalScope.launch {
                    delay(200)

                    billingDao().insertBillItemCol(parseCsvRowToBillItemsCol(row))
                }
            }
        }

        if (backupBillItemCSVFile.exists()) {
            restoreCsvFile(
                backupBillItemCSVFile,
                currentRowCount,
                totalRowCount,
                progressListener
            ) { row ->
                GlobalScope.launch {
                    billingDao().insertBillItem(parseCsvRowToBillItem(row))
                }
            }
        }

        if (backupCustomersCSVFile.exists()) {
            restoreCsvFile(
                backupCustomersCSVFile,
                currentRowCount,
                totalRowCount,
                progressListener
            ) { row ->
                GlobalScope.launch {
                    billingDao().insertCustomer(parseCsvRowToCustomerItem(row))
                }
            }
        }

           progressListener.onProgressUpdated(100)
        }
    }

    private suspend fun calculateTotalRowCount(vararg files: File): Int {
        var totalRows = 0
        for (file in files) {
            if (file.exists()) {
                val csvReader = CSVReader(withContext(Dispatchers.IO) {
                    FileReader(file)
                })
                totalRows += csvReader.readAll().size
                csvReader.close()
            }
        }
        return totalRows
    }

    private suspend fun restoreCsvFile(
        file: File,
        currentRowCount: AtomicInteger,
        totalRowCount: Int,
        progressListener: RestoreProgressListener,
        insertFunction: (List<String>) -> Unit
    ) {
        val csvReader = CSVReader(withContext(Dispatchers.IO) {
            FileReader(file)
        })
        val rows = csvReader.readAll()

        rows.forEach { row ->
            insertFunction(row.toList())
            val current = currentRowCount.incrementAndGet()
            val progress = (current * 100) / totalRowCount
            withContext(Dispatchers.Main) {
                progressListener.onProgressUpdated(progress)
            }
        }

        csvReader.close()
    }


    companion object {
        private const val DATABASE_NAME = "billingapp_database"
    }
}

interface RestoreProgressListener {
    fun onProgressUpdated(percentage: Int)
}

