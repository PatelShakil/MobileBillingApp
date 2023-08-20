package com.mycampus.billingapp.data.room

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.room.Database
import androidx.room.RoomDatabase
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.CustomerItem
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader
import java.io.FileWriter

@Database(entities = [BillItemCollection::class, BillItem::class, CustomerItem::class], version = 1, exportSchema = false)
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

    fun parseCsvRowToBillItemsCol(values: Array<String>): BillItemCollection {
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
    fun parseCsvRowToCustomerItem(csvRow: Array<String>): CustomerItem {
        val values = csvRow
        return CustomerItem(
            id = values[0].toLong(),
            name = values[1],
            mobile = values[2],
            email = values[3],
            address = values[4]
        )
    }

    fun parseCsvRowToBillItem(csvRow: Array<String>): BillItem {
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


    suspend fun backupDatabase(billitemCols : List<BillItemCollection>,billitems:List<BillItem>,customers : List<CustomerItem>,context: Context) {

        val backupDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"/myCampus/Backup")
        if(!backupDir.exists())
            backupDir.mkdirs()
        var backupBillItemCSVFile =  File(backupDir.absolutePath + "/billitems.backup")
        var backupBillItemsColCSVFile =  File(backupDir.absolutePath + "/billitemsCol.backup")
        var backupCustomersCSVFile =  File(backupDir.absolutePath + "/customers.backup")

        if(!backupBillItemCSVFile.exists())
            backupBillItemCSVFile.createNewFile()
        if(!backupBillItemsColCSVFile.exists())
            backupBillItemsColCSVFile.createNewFile()
        if(!backupCustomersCSVFile.exists())
            backupCustomersCSVFile.createNewFile()

        try{
            val billItemsColfw = FileWriter(backupBillItemsColCSVFile)
            val billItemfw = FileWriter(backupBillItemCSVFile)
            val customersfw = FileWriter(backupCustomersCSVFile)

            billitemCols.forEach {billCol ->
                billItemsColfw.append(""+generateBillItemsColCsvRow(billCol)+"\n")
                }
            billitems.forEach {billItem->
                billItemfw.append(""+generateBillItemCsvRow(billItem)+"\n")
            }
            customers.forEach {customerItem ->
                customersfw.append(""+generateCustomerCsvRow(customerItem)+"\n")
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
        }catch (e : Exception){
            e.printStackTrace()
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
//
//
//
//        val currentDBPath = context.getDatabasePath(DATABASE_NAME).absolutePath
////        val backupDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        if (!backupDir.exists()) {
//            backupDir.mkdirs()
//        }
//        val backupFile = File(backupDir, "$backupFileName.db")
//
//        val fileInputStream = FileInputStream(currentDBPath)
//        val outputStream = FileOutputStream(backupFile)
//
//        fileInputStream.copyTo(outputStream)
//
//        fileInputStream.close()
//        outputStream.close()
    }
    // Restore the database from a backup file
    suspend fun restoreDatabase(context: Context) {
        val backupDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"/BillingApp/Backup")
        var backupBillItemCSVFile =  File(backupDir.absolutePath + "/billitems.backup")
        var backupBillItemsColCSVFile =  File(backupDir.absolutePath + "/billitemsCol.backup")
        var backupCustomersCSVFile =  File(backupDir.absolutePath + "/customers.backup")

        if(backupBillItemsColCSVFile.exists()){
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
            }catch (e : Exception){
                e.printStackTrace()
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {
        private const val DATABASE_NAME = "billingapp_database"
    }
}
