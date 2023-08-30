package com.mycampus.billingapp.ui.nav

import android.bluetooth.BluetoothManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mycampus.billingapp.R
import com.mycampus.billingapp.common.Utils.Companion.sendWhatsAppMessage
import com.mycampus.billingapp.common.uicomponents.ProgressBarCus
import com.mycampus.billingapp.common.uicomponents.RestoreConfirmationDialog
import com.mycampus.billingapp.data.models.BillItemCollectionExcel
import com.mycampus.billingapp.data.models.UserDetails
import com.mycampus.billingapp.data.room.RestoreProgressListener
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.BillItemCollectionWithBillItems
import com.mycampus.billingapp.data.room.entities.ContactItem
import com.mycampus.billingapp.data.room.entities.CustomerItem
import com.mycampus.billingapp.ui.backuprestore.BackupRestoreScreen
import com.mycampus.billingapp.ui.backuprestore.BackupRestoreViewModel
import com.mycampus.billingapp.ui.contact.ContactMainScreen
import com.mycampus.billingapp.ui.customer.CustomerScreen
import com.mycampus.billingapp.ui.detail.BillDetailScreen
import com.mycampus.billingapp.ui.home.HomeScreen
import com.mycampus.billingapp.ui.home.MainColor
import com.mycampus.billingapp.ui.home.PrinterPopup
import com.mycampus.billingapp.ui.home.UserViewModel
import com.mycampus.billingapp.ui.home.bluetooth.BluetoothUiState
import com.mycampus.billingapp.ui.home.bluetooth.BluetoothViewModel
import com.mycampus.billingapp.ui.settings.SettingMainScreen

@Composable
fun AppNavigation(viewModel: BackupRestoreViewModel, onEnableBluetooth: () -> Unit) {
    var title by remember { mutableStateOf("Mobile Billing") }
    val navController = rememberNavController()
    val btViewModel = hiltViewModel<BluetoothViewModel>()
    val userViewModel = hiltViewModel<UserViewModel>()
    val state by btViewModel.state.collectAsState()

    val context = LocalContext.current
    val bluetoothManager by lazy {
        context.applicationContext.getSystemService(BluetoothManager::class.java)
    }
    val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }


    val isBluetoothEnabled: Boolean = bluetoothAdapter?.isEnabled == true

    var billitemsCol: List<BillItemCollection>? = null
    viewModel.billitemsCol.observeForever {
        billitemsCol = it
    }
    var billitems: List<BillItem>? = null
    viewModel.billitems.observeForever {
        billitems = it
    }
    var billitemcollection: List<BillItemCollectionWithBillItems>? = null
    viewModel.billitemcollection.observeForever {
        billitemcollection = it
    }
    var customers: List<CustomerItem>? = null
    viewModel.customers.observeForever {
        customers = it
    }
    val collectionListExcel: MutableList<BillItemCollectionExcel> =
        emptyList<BillItemCollectionExcel>().toMutableList()

    var shop by remember { mutableStateOf(UserDetails()) }

    var isRestoreConfirm by remember { mutableStateOf(false) }
    var isPromotionClick by remember { mutableStateOf(false) }
    var syncContacts by remember { mutableStateOf<List<ContactItem>>(emptyList()) }
    Column {
        HeaderLayout(title, state, userViewModel, onStartScan = {
            if (!isBluetoothEnabled) {
                onEnableBluetooth()
                btViewModel.startScan()
            }
            btViewModel.startScan()
        }, onStopScan = btViewModel::stopScan,
            onSettingClick = {
                navController.navigate(Screen.Setting.route)
            },
            {
                navController.navigate(Screen.Customer.route)
            }, {
                viewModel.backupDatabase(billitemsCol!!, billitems!!, customers!!, context)

            }, {
                isRestoreConfirm = true

            }, {
                billitemsCol!!.forEach { bill ->
                    val customer = customers!!.filter { it.id == bill.customerid }[0]
                    val billitemList =
                        billitemcollection!!.filter { it.itemCollection.bill_no == bill.bill_no }[0].itemList
                    collectionListExcel!!.add(
                        BillItemCollectionExcel(
                            bill.id,
                            customer,
                            bill.bill_no,
                            bill.bill_pay_mode,
                            bill.tax,
                            bill.total_amount,
                            bill.paid_amount,
                            bill.balance_amount,
                            bill.discount,
                            bill.remarks,
                            bill.creation_date,
                            bill.bill_date,
                            bill.created_by,
                            billitemList,
                            bill.is_sync
                        )
                    )
                }
                Log.d("Collection", collectionListExcel.toString())
                viewModel.generateExcel(collectionListExcel)
            },
            onSyncContacts = {
                navController.navigate(Screen.Contact.route)
            },
            onPromotionClick = {
                shop = userViewModel.userDetails!!
                userViewModel.allSyncContacts.observeForever {
                    syncContacts = it
                }
                isPromotionClick = !isPromotionClick
            })
        viewModel.downloadExcelResult.collectAsState().let {
            if (it.value) {
                ProgressBarCus {

                }
            }
        }
        var progress by remember { mutableStateOf(0) }
        LaunchedEffect(key1 = true, block = {

        })

        if (isPromotionClick) {
            PromotionDialog(shop, syncContacts) {
                isPromotionClick = !isPromotionClick
            }
        }
        if (isRestoreConfirm) {
            RestoreConfirmationDialog(
                onDismiss = {
                    isRestoreConfirm = false
                    progress = 0
                }) {
                viewModel.restoreDatabase(context, object : RestoreProgressListener {
                    override fun onProgressUpdated(percentage: Int) {
                        progress = percentage
                        Log.d("Progress", percentage.toString())
                    }
                })
            }
            if (progress in 1..100) {
                ProgressBarCus(onDismiss = { }, progress = progress / 100f)
            }
            if (progress == 100) {
                isRestoreConfirm = false
                progress = 0
            }
        }


        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen(hiltViewModel(), hiltViewModel(), navController)
                title = "Mobile Billing"
            }
            composable(Screen.Details.route) {
                BillDetailScreen(hiltViewModel(), hiltViewModel(), navController)
                title = "Billing Details"
            }
            composable(Screen.Customer.route) {
                CustomerScreen(viewModel = hiltViewModel(), navController = navController)
                title = "Customers"
            }
            composable(Screen.BackupRestore.route) {
                BackupRestoreScreen(viewModel = hiltViewModel())
                title = "Backup & Restore"
            }
            composable(Screen.Contact.route) {
                ContactMainScreen(viewModel = hiltViewModel(), navController)
                title = "Contacts"
            }
            composable(Screen.Setting.route) {
                SettingMainScreen(viewModel = hiltViewModel(), navController = navController)
                title = "Setting"
            }
        }
    }
}

@Composable
fun HeaderLayout(
    title: String,
    state: BluetoothUiState,
    viewModel: UserViewModel,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onSettingClick: () -> Unit,
    onCustomerClick: () -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onDownloadExcel: () -> Unit,
    onSyncContacts: () -> Unit,
    onPromotionClick: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isSettingsExpanded by remember { mutableStateOf(false) }
    var isPrinterExpanded by remember { mutableStateOf(false) }
    val userDetails = viewModel.userDetails


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                }

            },
            actions = {
//                      Icon(painterResource(R.drawable.ic_backup),"",
//                          modifier = Modifier.clickable {
//                              onBackup()
//                          },
//                          tint = Color.White)
//                Spacer(modifier = Modifier.width(10.dp))
//                Icon(painterResource(id = (R.drawable.ic_restore)), "",
//                    modifier = Modifier.clickable {
//                        onRestore()
//                    },
//                    tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Menu Icon",
                        tint = Color.White,
                        modifier = Modifier.clickable { isMenuExpanded = !isMenuExpanded }
                    )
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                isMenuExpanded = false
                                // Handle Settings menu item click
                                onSettingClick()
                            }
                        ) {
                            Icon(Icons.Default.Settings, "", tint = MainColor)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Settings")
                        }
                        DropdownMenuItem(
                            onClick = {
                                isMenuExpanded = false
                                // Handle Printer Connection menu item click
                                isPrinterExpanded = true
                            }
                        ) {
                            Icon(painterResource(id = R.drawable.ic_printer), "", tint = MainColor)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Printer Connection")
                        }
                        DropdownMenuItem(
                            onClick = {
                                isMenuExpanded = false
                                onCustomerClick()
                                // Handle Others menu item click
                            }
                        ) {
                            Image(
                                painterResource(id = R.drawable.customer_service),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(MainColor),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Customers")
                        }
                        DropdownMenuItem(
                            onClick = {
                                isMenuExpanded = false
                                onBackup()
                                // Handle Others menu item click
                            }
                        ) {
                            Image(
                                painterResource(id = R.drawable.ic_backup), contentDescription = "",
                                colorFilter = ColorFilter.tint(MainColor),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Backup")
                        }
                        DropdownMenuItem(
                            onClick = {
                                isMenuExpanded = false
                                onRestore()
                                // Handle Others menu item click
                            }
                        ) {
                            Image(
                                painterResource(id = R.drawable.ic_restore),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(MainColor),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Restore")
                        }
                        DropdownMenuItem(
                            onClick = {
                                isMenuExpanded = false
                                onDownloadExcel()
                                // Handle Others menu item click
                            }
                        ) {
                            Image(
                                painterResource(id = R.drawable.ic_excel),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(MainColor),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Download Excel Sheet")
                        }
                        DropdownMenuItem(
                            onClick = {
                                isMenuExpanded = false
                                onSyncContacts()
                                // Handle Others menu item click
                            }
                        ) {
                            Image(
                                Icons.Default.AccountBox,
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(MainColor),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Sync Contacts")
                        }
                        DropdownMenuItem(
                            onClick = {
                                isMenuExpanded = false
                                onPromotionClick()
                                // Handle Others menu item click
                            }
                        ) {
                            Image(
                                painterResource(id = R.drawable.ic_promotion),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(MainColor),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Promotion")
                        }
                        DropdownMenuItem(
                            onClick = {
                                isMenuExpanded = false
                                // Handle Others menu item click
                            }
                        ) {
                            Icon(Icons.Default.ExitToApp, "", tint = MainColor)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Others")
                        }
                    }
                }

            },
            backgroundColor = MainColor,
            elevation = 0.dp
        )

    }

    if (isPrinterExpanded) {
        PrinterPopup(
            state.pairedDevices,
            state.scannedDevices,
            onStartScan = onStartScan,
            onStopScan = onStopScan,
        ) { isPrinterExpanded = false }
    }
}

@Composable
fun PromotionDialog(shop: UserDetails, list: List<ContactItem>, onDismiss: () -> Unit) {
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
                                    var msg = "Dear ${it.name},\n\n"
                                    msg += shop.promotionMessageENG
                                    msg += "\n\n"
                                    msg += shop.promotionMessageHND
                                    msg += if (shop.isDynamicLinkEnabled) "\n\n${
                                        if (shop.dynamicLink.startsWith(
                                                "http"
                                            )
                                        ) shop.dynamicLink +"=" else "http://${shop.dynamicLink}="
                                    }${
                                        it.mobileNo.replace(
                                            " ",
                                            ""
                                        )
                                    }\n" else ""

                                    msg += "\nThanks\n${shop.name}"
                                    sendWhatsAppMessage(context, msg, it.mobileNo)
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