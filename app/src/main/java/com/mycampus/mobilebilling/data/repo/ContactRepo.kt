package com.mycampus.mobilebilling.data.repo

import android.content.Context
import android.provider.ContactsContract
import com.mycampus.mobilebilling.data.room.RoomDao
import com.mycampus.mobilebilling.data.room.entities.ContactItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ContactRepo @Inject constructor(
    private val dao : RoomDao
){


    suspend fun insertContact(contact : ContactItem):Boolean{
        return try{
            dao.insertContactItem(contact)
            true
        }catch (e : Exception){
            e.printStackTrace()
            false
        }
    }

    suspend fun insertContactList(list : List<ContactItem>):Boolean{
        return try {
            list.forEach {
                dao.insertContactItem(it)
            }
            true
        }catch (e : Exception){
            e.printStackTrace()
            false
        }
    }

    fun loadContacts(context: Context): StateFlow<MutableList<ContactItem>?> {
        val contactsList = MutableStateFlow<MutableList<ContactItem>?>(mutableListOf())
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val idColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)
            val nameColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneNumberColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val id = it.getString(idColumn)
                val name = it.getString(nameColumn)
                val phoneNumber = it.getString(phoneNumberColumn)
                contactsList.value!!.add(ContactItem(id, name, phoneNumber))
            }
        }
        return contactsList.asStateFlow()
    }
    fun getAllContacts() = dao.getAllContacts()

}