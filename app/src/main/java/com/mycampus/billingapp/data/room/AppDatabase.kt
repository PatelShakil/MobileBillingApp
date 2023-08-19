package com.mycampus.billingapp.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.CustomerItem
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Database(entities = [BillItemCollection::class, BillItem::class, CustomerItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun billingDao(): RoomDao
    // Backup the database to a file
    fun backupDatabase(context: Context, backupFile: File) {
        val currentDBPath = context.getDatabasePath(DATABASE_NAME).absolutePath
        val fileInputStream = FileInputStream(currentDBPath)
        val outputStream = FileOutputStream(backupFile)

        fileInputStream.copyTo(outputStream)

        fileInputStream.close()
        outputStream.close()
    }

    // Restore the database from a backup file
    fun restoreDatabase(context: Context,backupFile: File) {
        try {
            val currentDBPath = context.getDatabasePath(DATABASE_NAME).absolutePath
            val fileOutputStream = FileOutputStream(currentDBPath)
            val inputStream = FileInputStream(backupFile)

            inputStream.copyTo(fileOutputStream)

            inputStream.close()
            fileOutputStream.close()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    companion object {
        private const val DATABASE_NAME = "billingapp_database"
    }
}
