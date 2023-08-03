package com.mycampus.billingapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mycampus.billingapp.R
import com.mycampus.billingapp.common.Utils
import com.mycampus.billingapp.data.models.UserDetails
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.BillItemCollectionWithBillItems
import com.mycampus.billingapp.domain.bluetooth.BluetoothDevice
import com.mycampus.billingapp.ui.nav.AppNavigation
import com.mycampus.billingapp.ui.theme.BiilingappTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if (canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            )
        }
        setContent {
            BiilingappTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation() {
                        enableBluetoothLauncher.launch(
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        )
                    }
                    /*Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        HeaderLayout(state, userViewModel, onStartScan = {
                            if (!isBluetoothEnabled) {
                                enableBluetoothLauncher.launch(
                                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                )
                                viewModel.startScan()
                            }
                            viewModel.startScan()
                        }, onStopScan = viewModel::stopScan)
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                        ) {
                            MainScreenFees(
                                userViewModel.getUserDetails() ?: UserDetails(),
                                onProceedClicked = {},
                                navController = NavController(LocalContext.current)
                            ) { billCol, list ->
                                Log.d("billCol", billCol.toString())
                                Log.d("BillItemList", list.toString())
                                userViewModel.addItemCollection(billCol, list)
                            }

                            if (itemCol.isNotEmpty()) {
                                val date = Utils.convertLongToDate(System.currentTimeMillis(),"dd MMMM yyyy")
                                val totalAmount =
                                    itemCol.filter {
                                    Utils.convertLongToDate(it.itemCollection.creation_date,"DDMMYY") == Utils.convertLongToDate(System.currentTimeMillis(),"DDMMYY")
                                }.sumOf {
                                    it.itemCollection.total_amount
                                    }
                                val cashAmount = itemCol.filter {
                                    Utils.convertLongToDate(it.itemCollection.creation_date,"DDMMYY") == Utils.convertLongToDate(System.currentTimeMillis(),"DDMMYY") &&
                                            it.itemCollection.bill_pay_mode == "Paid by Cash"
                                }.sumOf {
                                    it.itemCollection.total_amount
                                }
                                val onlineAmount = itemCol.filter {
                                    Utils.convertLongToDate(it.itemCollection.creation_date,"DDMMYY") == Utils.convertLongToDate(System.currentTimeMillis(),"DDMMYY") &&
                                            it.itemCollection.bill_pay_mode != "Paid by Cash"
                                }.sumOf {
                                    it.itemCollection.total_amount
                                }
                                BottomCard(date,totalAmount,cashAmount,onlineAmount){

                                }

                            }

                            Spacer(Modifier.height(150.dp))

                        }

                    }*/
                }
            }
        }
    }
}