package com.mycampus.billingapp.common.uicomponents

import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.mycampus.billingapp.R
import com.mycampus.billingapp.common.MainColor
import com.mycampus.billingapp.common.Utils
import com.mycampus.billingapp.common.Utils.Companion.sendWhatsAppMessage
import com.mycampus.billingapp.data.models.UserDetails
import com.mycampus.billingapp.data.room.entities.ContactItem
import com.mycampus.billingapp.data.room.entities.CustomerItem
import com.mycampus.billingapp.domain.bluetooth.BluetoothDevice
import com.mycampus.billingapp.ui.customer.AddCustomerTextField
import com.mycampus.billingapp.ui.home.bluetooth.BluetoothDeviceList
import com.mycampus.billingapp.ui.nav.Screen
import java.io.File

@Composable
fun ConfirmationDialog(onDismiss: () -> Unit, onPrint: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        text = {},
        title = {
            Text(
                "Bill Collected Successfully",
                style = MaterialTheme.typography.titleMedium
            )
        },
        confirmButton = {
            Button(onClick = onPrint) {
                Text("Print")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        })
}

@Composable
fun RestoreConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val backupDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "/myCampus/Backup"
    )
    if (!backupDir.exists())
        backupDir.mkdirs()
    var billitemfile = File(backupDir.absolutePath + "/billitems.backup")
    var billitemcolsfile = File(backupDir.absolutePath + "/billitemsCol.backup")
    var customerfile = File(backupDir.absolutePath + "/customers.backup")
    AlertDialog(onDismissRequest = onDismiss,
        text = {
            Column {
                if (billitemfile.exists()) {
                    Text(
                        "last modified on ${
                            Utils.convertLongToDate(
                                billitemcolsfile.lastModified(),
                                "dd-MM-yyyy hh:mm a"
                            )
                        }"
                    )
                }
            }

        },
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painterResource(R.drawable.ic_restore), "",
                        tint = MainColor
                    )
                    Text(
                        "Restore",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                    )
                }
                Spacer(Modifier.height(5.dp))
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .height(.5.dp), color = Color.Gray)
                Spacer(modifier = Modifier.height(5.dp))
                Text(if (billitemfile.exists()) "Backup Available" else "No Backup available, please take backup first",
                    style = MaterialTheme.typography.bodyMedium)
            }

        },
        confirmButton = {

            Button(
                onClick = onConfirm,
                enabled = billitemcolsfile.exists()
            ) {
                Text("Restore")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        })
}

@Composable
fun PromotionDialog(
    shop: UserDetails,
    list: List<ContactItem>,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(modifier = Modifier.fillMaxWidth(.95f)) {
            Column {
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painterResource(R.drawable.ic_promotion), "",
                        tint = MainColor
                    )
                    Text(
                        "Promotion",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                    )
                }
                Spacer(Modifier.height(5.dp))
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(.5.dp), color = Color.Gray
                )
                Spacer(modifier = Modifier.height(5.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(list) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(.9f)) {
                                Text(
                                    it.name,
                                    fontSize = 14.sp
                                )
                                Text(
                                    it.mobileNo,
                                    fontSize = 12.sp
                                )
                            }
                            Box(modifier = Modifier
                                .background(MainColor, RoundedCornerShape(20.dp))
                                .clickable {
                                    //Send Click

                                    if (shop.promotionMessageENG.isNotEmpty() || shop.promotionMessageHND.isNotEmpty()) {
                                        var msg = "Dear ${it.name},\n\n"

                                        if (shop.promotionMessageENG.isEmpty()) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Promotion Message was Empty",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            onDismiss()
                                            navController.navigate(Screen.Setting.route)
                                            return@clickable
                                        }
                                        msg += shop.promotionMessageENG
                                        if (shop.promotionMessageHND.isEmpty()) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Promotion Message was Empty",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            onDismiss()
                                            navController.navigate(Screen.Setting.route)
                                            return@clickable
                                        }
                                        msg += "\n\n"
                                        msg += shop.promotionMessageHND


                                        msg += if (shop.isDynamicLinkEnabled) "\n\n${
                                            if (shop.dynamicLink.startsWith(
                                                    "http"
                                                )
                                            ) shop.dynamicLink + "=" else "http://${shop.dynamicLink}="
                                        }${
                                            it.mobileNo.replace(
                                                " ",
                                                ""
                                            )
                                        }\n" else ""

                                        msg += "\nThanks\n${shop.name}"
                                        sendWhatsAppMessage(context, msg, it.mobileNo)
                                    }else{
                                        Toast
                                            .makeText(
                                                context,
                                                "Promotion Message was Empty",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                        onDismiss()
                                        navController.navigate(Screen.Setting.route)
                                        return@clickable
                                    }
                                }) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        "Send",
                                        color = Color.White
                                    )
//                                    Icon(Icons.Default.Send, "", tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun SettingsPopup(
    userDetail: UserDetails?,
    onDismissRequest: () -> Unit,
    onSaveClicked: (UserDetails) -> Unit
) {
    val userDetails by remember { mutableStateOf(userDetail!!) }
    var isEditable by remember { mutableStateOf(false) }
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEAE9F0))
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Settings, "",
                        tint = MainColor
                    )
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                    )
                }
                Spacer(Modifier.height(5.dp))
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(.5.dp), color = Color.Gray
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column() {
                    if (isEditable) {
                        Column {
                            SettingsTextFieldSample(
                                label = "Name",
                                value = userDetails.name,
                                onTextChanged = {
                                    userDetails.name = it
                                })
                            SettingsTextFieldSample(
                                label = "Email",
                                value = userDetails.email,
                                onTextChanged = {
                                    userDetails.email = it
                                },
                                KeyboardType.Email
                            )
                            SettingsTextFieldSample(
                                label = "Address",
                                value = userDetails.address,
                                onTextChanged = {
                                    userDetails.address = it
                                },
                                lineCount = 3
                            )
                            SettingsTextFieldSample(
                                label = "Mobile",
                                value = userDetails.mobile,
                                onTextChanged = {
                                    userDetails.mobile = it
                                },
                                KeyboardType.Phone
                            )
                            SettingsTextFieldSample(
                                label = "GST No.",
                                value = userDetails.GST,
                                onTextChanged = {
                                    userDetails.GST = it
                                }
                            )

                            SettingsTextFieldSample(
                                label = "Website",
                                value = userDetails.website,
                                onTextChanged = {
                                    userDetails.website = it
                                })

                            CusDropdown(label = "Dynamic Link", selected = DropDownItemData(
                                if (userDetails.isDynamicLinkEnabled) "Enabled" else "Disabled",
                                if (userDetails.isDynamicLinkEnabled) "Enabled" else "Disabled"
                            ), options = listOf(
                                DropDownItemData("Enabled", "Enabled"),
                                DropDownItemData("Disabled", "Disabled")
                            ), onSelected = {
                                userDetails.isDynamicLinkEnabled = it.id == "Enabled"
                            })

                            SettingsTextFieldSample(
                                label = "Promotion Message(English)",
                                value = userDetails.promotionMessageENG,
                                onTextChanged = {
                                    userDetails.promotionMessageENG = it
                                },
                                lineCount = 3
                            )
                            SettingsTextFieldSample(
                                label = "Promotion Message(Hindi)",
                                value = userDetails.promotionMessageHND,
                                onTextChanged = {
                                    userDetails.promotionMessageHND = it
                                },
                                lineCount = 3
                            )
                        }
                    } else {
                        if (userDetails != UserDetails()) {
                            Column {
                                Text("Name: ${userDetails.name}")
                                Text("Email: ${userDetails.email}")
                                Text("Address: ${userDetails.address}")
                                Text("Mobile: ${userDetails.mobile}")
                                Text("GST No: ${userDetails.GST}")
                                Text("Website: ${userDetails.website}")
                                Text("Dynamic Link: ${if (userDetails.isDynamicLinkEnabled) "Enabled" else "Disabled"}")
                                Text("Promotion Message(English): ${userDetails.promotionMessageENG}")
                                Text("Promotion Message(Hindi): ${userDetails.promotionMessageHND}")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (isEditable) {
                        Button(
                            onClick = {
                                isEditable = false
                                onSaveClicked(userDetails)
                                onDismissRequest()
                            }
                        ) {
                            Text("Save")
                        }

                    } else {
                        Button(
                            onClick = {
                                isEditable = true
                            }
                        ) {
                            Text(if (userDetails == UserDetails()) "Add" else "Edit")
                        }
                        Button(
                            onClick = { onDismissRequest() }
                        ) {
                            Text("Close")
                        }
                    }
                }

            }
        }
    }

}

@Composable
fun PrinterPopup(
    pairedDevices: List<BluetoothDevice>,
    scannedDevices: List<BluetoothDevice>,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var isStart by remember { mutableStateOf(false) }
    var isStop by remember { mutableStateOf(true) }
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color(0xFFEAE9F0))
                    .padding(10.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painterResource(R.drawable.ic_printer), "",
                        tint = MainColor
                    )
                    Text(
                        "Printer Connection",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                    )
                }
                Spacer(Modifier.height(5.dp))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(.5.dp), color = Color.Gray
                )
                if (pairedDevices.size > 0 || scannedDevices.size > 0) {
                    BluetoothDeviceList(
                        pairedDevices = pairedDevices,
                        scannedDevices = scannedDevices,
                        onClick = {},
                        modifier = Modifier
                            .padding(vertical = 20.dp)
                            .fillMaxHeight(.7f)
                    )
                }
                if (pairedDevices.isEmpty()) {
                    Text(
                        "No Device Found",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    if (isStop) {
                        Button(onClick = {
                            onStartScan()
                            isStart = true
                            isStop = false
                        }) {
                            Text(text = "Start scan")

                        }
                    }
                    if (isStart) {
                        Button(onClick = {
                            onStopScan()
                            isStart = false
                            isStop = true
                        }) {
                            Text(text = "Stop scan")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterPopup(onDismiss: () -> Unit, onConfirm: (String, Boolean, Boolean) -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        var date by remember { mutableStateOf("") }
        var isCash by remember { mutableStateOf(false) }
        var isOnline by remember { mutableStateOf(false) }
        Card(modifier = Modifier.fillMaxWidth(.95f)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(modifier = Modifier.fillMaxWidth(.95f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(15.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_filter),
                                "",
                                tint = MainColor
                            )
                            Text("Bill Details Filter")
                        }
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth(.95f)
                                .height(1.dp), color = Color.Gray
                        )
                        Spacer(Modifier.height(5.dp))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(.95f)
                        ) {
                            DatePickerDialogCustom(
                                date = "",
                                label = "Date",
                                onDateSelect = { year, month, day ->
                                    date = "$day-$month-$year"
                                })
                            Row(
                                modifier = Modifier.fillMaxWidth(.95f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Payment Mode : ")
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = isCash, onCheckedChange = {
                                        isCash = !isCash
                                    })
                                    Text("Cash")
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = isOnline, onCheckedChange = {
                                        isOnline = !isOnline
                                    })
                                    Text("Online")
                                }
                            }
                            Button(onClick = {
                                onConfirm(date, isOnline, isCash)
                                onDismiss()
                            }, modifier = Modifier.fillMaxWidth(.8f)) {
                                Text(text = "Apply")
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddCustomerPopupScreen(
    isEdit: Boolean = false,
    customer: CustomerItem,
    onDismiss: () -> Unit,
    onConfirm: (CustomerItem) -> Unit
) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        var name by remember { mutableStateOf("") }
        var mobile by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
                .background(Color.Transparent)
                .clickable {
                    onDismiss()
                },
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(modifier = Modifier
                .fillMaxWidth(.95f)
                .clickable { }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(25.dp)
                            .background(Color.White),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier
                                .height(6.dp)
                                .width(50.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MainColor
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "${if (isEdit) "Update" else "Add"} Customer",
                        style = MaterialTheme.typography.titleSmall,
                        color = MainColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(.95f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(5.dp))
                        AddCustomerTextField(label = "Name", onTextChanged = {
                            name = it
                        })
                        Spacer(modifier = Modifier.height(5.dp))
                        AddCustomerTextField(
                            label = "Mobile", onTextChanged = {
                                mobile = it
                            },
                            KeyboardType.Phone
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        AddCustomerTextField(
                            label = "Email", onTextChanged = {
                                email = it
                            },
                            KeyboardType.Email
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        AddCustomerTextField(label = "Address", onTextChanged = {
                            address = it
                        })

                        Spacer(modifier = Modifier.height(5.dp))
                        Button(modifier = Modifier
                            .fillMaxWidth(.9f),
                            enabled = name.isNotEmpty() && mobile.isNotEmpty() && email.isNotEmpty() && address.isNotEmpty(),
                            onClick = {
                                val cus = CustomerItem(0, name, mobile, email, address)
                                onConfirm(cus)
                                onDismiss()
                            }) {
                            Text(if (isEdit) "Update" else "Add")
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}