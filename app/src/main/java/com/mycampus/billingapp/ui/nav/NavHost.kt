package com.mycampus.billingapp.ui.nav

import android.bluetooth.BluetoothManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mycampus.billingapp.data.models.UserDetails
import com.mycampus.billingapp.ui.detail.BillDetailScreen
import com.mycampus.billingapp.ui.home.BluetoothUiState
import com.mycampus.billingapp.ui.home.BluetoothViewModel
import com.mycampus.billingapp.ui.home.HeaderScreen
import com.mycampus.billingapp.ui.home.HomeScreen
import com.mycampus.billingapp.ui.home.MainColor
import com.mycampus.billingapp.ui.home.PrinterPopup
import com.mycampus.billingapp.ui.home.SettingsPopup
import com.mycampus.billingapp.ui.home.UserViewModel

@Composable
fun AppNavigation(onEnableBluetooth:()-> Unit) {
    var title by remember{ mutableStateOf("Mobile Billing") }
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

    Column {
        HeaderLayout(title,state,userViewModel, onStartScan = {
            if (!isBluetoothEnabled) {
                onEnableBluetooth()
                btViewModel.startScan()
            }
            btViewModel.startScan()
        }, onStopScan = btViewModel::stopScan)
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen(hiltViewModel(), navController)
                title = "Mobile Billing"
            }
            composable(Screen.Details.route) {
                BillDetailScreen(hiltViewModel(), navController)
                title = "Billing Details"
            }
        }
    }
}
@Composable
fun HeaderLayout(
    title:String,
    state: BluetoothUiState,
    viewModel: UserViewModel,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit
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
                                Text("Settings")
                            }
                            DropdownMenuItem(
                                onClick = {
                                    isMenuExpanded = false
                                    // Handle Printer Connection menu item click
                                    isPrinterExpanded = true
                                }
                            ) {
                                Text("Printer Connection")
                            }
                            DropdownMenuItem(
                                onClick = {
                                    isMenuExpanded = false
                                    // Handle Others menu item click
                                }
                            ) {
                                Text("Others")
                            }
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