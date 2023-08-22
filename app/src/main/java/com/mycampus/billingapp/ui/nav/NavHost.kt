package com.mycampus.billingapp.ui.nav

import android.bluetooth.BluetoothManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mycampus.billingapp.R
import com.mycampus.billingapp.common.uicomponents.ProgressBarCus
import com.mycampus.billingapp.common.uicomponents.RestoreConfirmationDialog
import com.mycampus.billingapp.data.models.BillItemCollectionExcel
import com.mycampus.billingapp.data.models.UserDetails
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.CustomerItem
import com.mycampus.billingapp.ui.backuprestore.BackupRestoreScreen
import com.mycampus.billingapp.ui.backuprestore.BackupRestoreViewModel
import com.mycampus.billingapp.ui.customer.CustomerScreen
import com.mycampus.billingapp.ui.detail.BillDetailScreen
import com.mycampus.billingapp.ui.home.HomeScreen
import com.mycampus.billingapp.ui.home.MainColor
import com.mycampus.billingapp.ui.home.PrinterPopup
import com.mycampus.billingapp.ui.home.SettingsPopup
import com.mycampus.billingapp.ui.home.UserViewModel
import com.mycampus.billingapp.ui.home.bluetooth.BluetoothUiState
import com.mycampus.billingapp.ui.home.bluetooth.BluetoothViewModel

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
    var customers: List<CustomerItem>? = null
    viewModel.customers.observeForever {
        customers = it
    }
    val collectionListExcel: MutableList<BillItemCollectionExcel> = emptyList<BillItemCollectionExcel>().toMutableList()


    var isRestoreConfirm by remember { mutableStateOf(false) }
    Column {
        HeaderLayout(title, state, userViewModel, onStartScan = {
            if (!isBluetoothEnabled) {
                onEnableBluetooth()
                btViewModel.startScan()
            }
            btViewModel.startScan()
        }, onStopScan = btViewModel::stopScan,
            {
                navController.navigate(Screen.Customer.route)
            }, {
                viewModel.backupDatabase(billitemsCol!!, billitems!!, customers!!, context)

            }, {
                isRestoreConfirm = true

            }, {
                billitemsCol!!.forEach { bill ->
                val customer = customers!!.filter { it.id == bill.customerid }[0]
                val billitemList = billitems!!.filter { it.bill_info_id == bill.id }
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
                        bill.created_by,
                        billitemList,
                        bill.is_sync
                    )
                )
            }
                viewModel.generateExcel(collectionListExcel)
            })
        viewModel.downloadExcelResult.collectAsState().let {
            if(it.value){
                ProgressBarCus {

                }
            }
        }
        if (isRestoreConfirm) {
            RestoreConfirmationDialog(
                onDismiss = {
                    isRestoreConfirm = false
                }) {
                viewModel.restoreDatabase(context)
                isRestoreConfirm = false
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
    onCustomerClick: () -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onDownloadExcel: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isSettingsExpanded by remember { mutableStateOf(false) }
    var isPrinterExpanded by remember { mutableStateOf(false) }
    val userDetails = viewModel.getUserDetails()


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
                                isSettingsExpanded = true
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
                                painterResource(id = R.drawable.ic_restore),
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



    if (isSettingsExpanded) {
        SettingsPopup(
            userDetails ?: UserDetails(),
            onDismissRequest = { isSettingsExpanded = false },
            onSaveClicked = {
                viewModel.saveUserDetails(it)
            }
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