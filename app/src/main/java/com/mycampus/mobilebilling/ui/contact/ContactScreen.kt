package com.mycampus.mobilebilling.ui.contact

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mycampus.mobilebilling.common.uicomponents.ErrorMessage
import com.mycampus.mobilebilling.common.uicomponents.ProgressBarCus
import com.mycampus.mobilebilling.common.uicomponents.SearchBar
import com.mycampus.mobilebilling.data.room.entities.ContactItem
import com.mycampus.mobilebilling.ui.home.UserViewModel

@Composable
fun ContactMainScreen(viewModel : UserViewModel,navController: NavController) {
    val selectedContacts = remember { mutableStateListOf<ContactItem>() } // State for selected contacts
    val context = LocalContext.current
    var isChecked by remember{ mutableStateOf(false) }
    var contacts by remember { mutableStateOf(listOf<ContactItem>())  }
    var contactsOg by remember { mutableStateOf(listOf<ContactItem>()) }
    LaunchedEffect(key1 = true, block = {
        viewModel.loadContacts(context = context)
        viewModel.contacts!!.observeForever {
            if (it != null) {
                contactsOg = it
                contacts = contactsOg
            }
        }
    })

    Column (modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally){
        SearchBar("Contact",onTextChanged = {value->
            contacts = if (value.isNotEmpty())
                contactsOg.filter { it.name.lowercase().contains(value.lowercase().trim()) || it.mobileNo.contains(value.trim())}
            else
                contactsOg
        })
        if(contactsOg.isNotEmpty()) {
            if (contacts.isNotEmpty()) {
                LazyColumn(modifier = Modifier.weight(.9f)) {
                    items(contacts) { contact ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedContacts.contains(contact),
                                onCheckedChange = { isCheck ->
                                    isChecked = isCheck
                                    if (isChecked) {
                                        selectedContacts.add(contact)
                                    } else {
                                        selectedContacts.remove(contact)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(contact.name)
                                Text(contact.mobileNo)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.saveContactList(selectedContacts)
                        navController.popBackStack()
                        Toast.makeText(
                            context,
                            selectedContacts.size.toString() + " contacts synced",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    enabled = selectedContacts.isNotEmpty() // Enable only when there are selected contacts
                    , modifier = Modifier.fillMaxWidth(.8f)
                ) {
                    Text("Sync Contacts")
                }
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                if (contactsOg.isNotEmpty())
                    ErrorMessage(msg = "No Corresponding record found...")
                else
                    ErrorMessage(msg = "No Data found...")
            }
        }else{
            ProgressBarCus {

            }
        }
    }
}