package com.mycampus.billingapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.End
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mycampus.billingapp.ui.theme.BiilingappTheme
import kotlin.math.roundToInt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN),1)
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        setContent {
            BiilingappTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        HeaderLayout()
                        MainScreenFees(
                                onProceedClicked = {},
                        navController = NavController(LocalContext.current)
                        )
                    }

                }
            }
        }
    }
}
val MainColor = Color(0xFF00638E)
data class CollectFeeData(
    val totalAmount: Double=0.0,
    val discount: Double=0.0,
//    val othrItemlist: List<OtherItemsInfo>
)

@Composable
fun MainScreenFees(onProceedClicked: (List<CollectFeeData>) -> Unit,navController: NavController ) {
    val context = LocalContext.current
//    var studentName = viewModel.studentName.collectAsState()
//    var className = viewModel.className.collectAsState()
//    var sectionName = viewModel.sectionName.collectAsState()
//    val schoolAdmno = viewModel.schoolAdmno.collectAsState()
//    val contactNo = viewModel.contactNo.collectAsState()
//    val schoolName = viewModel.schoolName.collectAsState()
//
//    val schoolAddress = viewModel.schoolAddress.collectAsState()
//    val schoolCity = viewModel.schoolCity.collectAsState()
//    val schoolState = viewModel.schoolState.collectAsState()
//    val schoolNo = viewModel.schoolNo.collectAsState()
//    val schoolEmail = viewModel.schoolEmail.collectAsState()

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val openPostPaymnetDialog = remember { mutableStateOf(false) }
    val openPrePaymnetDialog=remember { mutableStateOf(false) }
//    val payFee = viewModel.payFee.collectAsState()
//    val othrItemlist: List<OtherItemsInfo>
    var transactionRemark by remember { mutableStateOf("Paid by Cash") }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 10.dp, end = 10.dp, top = 10.dp),
        border = BorderStroke(.5.dp, Color.Gray),
        elevation = CardDefaults.cardElevation(18.dp)
    ) {
        val isAddClick = remember {
            mutableStateOf(1)
        }
        val feeDataList = mutableListOf<CollectFeeData>()
//        val listOfFinalData = mutableListOf<OtherItemsInfo>()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            //.verticalScroll(ScrollState(0), true),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            Row(modifier = Modifier.fillMaxWidth(.95f), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Collect other Fee",

                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                )
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(.2f)

                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "",
                        modifier = Modifier
                            .height(30.dp)
                            .width(30.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterEnd)
                            .background(Color.White)
                            .clickable {
                                isAddClick.value++
                            })
                }
            }
            Divider(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp))
            Spacer(modifier = Modifier.height(5.dp))
            var discountAmount by remember{ mutableStateOf(0.0) }
            var totalAmount  = 0.0

            Box(modifier = Modifier.fillMaxWidth(.94f), contentAlignment = Alignment.Center) {
                Text(
                    "Item Name", modifier = Modifier.align(Alignment.TopStart),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "Item Amount", modifier = Modifier.align(Alignment.TopEnd),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Divider(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(isAddClick.value) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val amount = remember {
                            mutableStateOf(0.0)
                        }
                        val item = remember { mutableStateOf("") }
                        Row(
                            modifier = Modifier.fillMaxWidth(.95f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
//                         DropDownSample(label = "", placeHolder = "", list = itemsList, onSelect = {
//                             item.value = it
//                         }, onAmountFixed = {
//                             amount.value = it
//                         })
                            Text(text = "${it + 1}.", modifier = Modifier.weight(.08f))
                            TextField(
                                value = item.value, onValueChange = { item.value = it },
                                modifier = Modifier.weight(.62f),
                                placeholder = {
                                    Text(
                                        "Enter item name",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                //label = {Text("Item No. ${it+1}",style = MaterialTheme.typography.bodySmall)},
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.White
                                )
                            )
                            Box(modifier = Modifier.weight(.3f)) {
                                getAmount(onAmountSet = { amount.value = it

                                })
                            }
                            totalAmount += amount.value
                        }
//                        listOfFinalData.add(OtherItemsInfo( amount.value, item.value))
                    }
                    Divider(modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp))
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                    Text("Discount",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(start= 10.dp,end = 20.dp)
                    )
                    SampleTextFieldDouble(label = "Discount", text = 0.0, onAmountSet = {
                        discountAmount = it
                        })
                }
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp))
            }

            val isOnileModeChecked = remember { mutableStateOf(false) }
            Spacer(modifier = Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .clickable {
                    isOnileModeChecked.value = !isOnileModeChecked.value
                }
                .fillMaxWidth(.95f)) {
                Text(
                    text = "Payment Mode Online ?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Checkbox(
                    modifier = Modifier.weight(.1f),
                    checked = isOnileModeChecked.value,
                    onCheckedChange = {
                        isOnileModeChecked.value = !isOnileModeChecked.value
                        if(it){
                            transactionRemark="Paid By PhonePay"
                        }
                        else{
                            transactionRemark="Paid by Cash"
                        }
                    })
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
            //val discount = remember { mutableStateOf(0.0) }
            Spacer(modifier = Modifier.height(5.dp))

            if (isOnileModeChecked.value) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            "Enter Transaction Remarks",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    value = transactionRemark,
                    onValueChange = {
                        transactionRemark=it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done),
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = White)
                )
            } else {
                transactionRemark = "Paid by Cash"
            }
            //feeDataList.add(CollectFeeData(totalAmount.toDouble(), discount.value, listOfFinalData))

            Spacer(modifier = Modifier.height(10.dp))

            //Progress Indicator on Paymnet

            /*Row {
                payFee.value?.let {
                    when (it) {
                        is Resource.Failure -> {
                            try {
                                val responsejson = JSONObject(it.errorMsgBody)
                                val errormsg = responsejson.getString("details")
                                //apiResponse= errormsg
                            } catch (e: Exception) {
                                //apiResponse= it.errorMsgBody
                            }
                        }
                        is Resource.Success -> {
                            openPostPaymnetDialog.value = true
                            if (openPostPaymnetDialog.value) {
                                ConfirmationDialog(
                                    details = "Dear ${studentName.value} \nYour payment is successfully for amount of $INR ${totalAmount}",
                                    generatedIdInfo = "Your Fee Receipt No : ${it.result.receiptno}",
                                    onDismiss = {
                                        //feeViewModel.resetPayInfo()
                                        openPostPaymnetDialog.value = false
                                        //onBackPressedDispatcher?.onBackPressed()
                                        navController.navigate(AdminAppScreen.StudentsDetails.Home.route){
                                            popUpTo(AdminAppScreen.AdminDashboard.route)
                                            *//* Remove all nav stack
                                            popUpTo(navController.graph.findStartDestination().id){
                                                inclusive=true
                                            }
                                             *//*
                                        }
                                        //navController.navigate(AdminAppScreen.AdminDashboard.route)
                                    },
                                    onPrint = {
                                        var printItemList = mutableListOf<PrintCategoryItem>()
                                        listOfFinalData.forEach{
                                            printItemList.add(PrintCategoryItem(it.description,it.amount))
                                        }
                                        //date
                                        val c = Calendar.getInstance()
                                        val year = c.get(Calendar.YEAR)
                                        val month = c.get(Calendar.MONTH) + 1
                                        val day = c.get(Calendar.DAY_OF_MONTH)

                                        val hour = c.get(Calendar.HOUR_OF_DAY)
                                        val minute = c.get(Calendar.MINUTE)
                                        val amPm = if (c.get(Calendar.AM_PM).toString() == "0") "AM " else "PM "

                                        val datetime = "${day}-${month}-${year}-${hour}:${minute} ${amPm}"

                                        var otherItems : MutableList<OtherItem> = listOf<OtherItem>().toMutableList()
                                        otherItems.add((OtherItem(otheritemsinfo=listOfFinalData)))
                                        val text = OtherFeeText(
                                            studentName.value,
                                            className.value,
                                            sectionName.value,
                                            schoolName.value,
                                            schoolAddress.value,
                                            schoolCity.value,
                                            schoolState.value,
                                            schoolNo.value,
                                            schoolEmail.value,
                                            it.result.receiptno,
                                            datetime,
                                            totalAmount,
                                            totalAmount,
                                            transactionRemark,
                                            printItemList
                                        )
                                        val thermalPrinter = BluetoothPrinter(context)
                                        thermalPrinter.printFeeReciept(text)

                                    },
                                    isFeeInfo = true,
                                )
                            }



                        }
                        Resource.Loading -> {
                            Box(
                                contentAlignment = Alignment.Center, // you apply alignment to all children
                                modifier = Modifier.fillMaxSize()
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center) // or to a specific child
                                )
                            }
                        }
                    }
                }
            }*/

            Button(onClick = {
                openPrePaymnetDialog.value = true
                //onProceedClicked(feeDataList)
            },
                enabled = totalAmount>0
//                        && listOfFinalData.isNotEmpty()
            ) {
                Text(
                    "Pay Rs ${if (totalAmount > 0 )totalAmount - discountAmount else totalAmount}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                )

            }

            Spacer(modifier = Modifier.height(5.dp))

            if (openPrePaymnetDialog.value) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = {
                        openPrePaymnetDialog.value = false
                    },
                    title = {
                        androidx.compose.material3.Text(
                            text = "Are you sure you want to proceed ?",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    text = {
                        Row {
                            val INR = "₹"
                            androidx.compose.material3.Text(
                                text = "Total  Amount  - $INR ${totalAmount} \n" +
                                        "Discount Amount - $INR ${discountAmount}  \n" +
                                        "Going to Pay    - $INR ${totalAmount-discountAmount.roundToInt()}  \n" +
                                        //"Balance Amount  - $INR ${0.0}",
                                        "Payment Mode - ${transactionRemark} ",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            //Call API here
                            /*if(isOnileModeChecked.value)
                                viewModel.feePayInfo.value.paymode=2
                            else
                                viewModel.feePayInfo.value.paymode=1
                            viewModel.feePayInfo.value.isotheritempayments=1
                            viewModel.feePayInfo.value.isdiscount=false //discount.value > 0
                            viewModel.feePayInfo.value.discountamount=0.0//discount.value
                            viewModel.feePayInfo.value.payamount=totalAmount
                            viewModel.feePayInfo.value.transactionremarks=transactionRemark


                            viewModel.feePayInfo.value.totalamountwithoutax=totalAmount
                            viewModel.feePayInfo.value.taxamount=0.0
                            viewModel.feePayInfo.value.newbalance=0.0

                            var otherItems : MutableList<OtherItem> = listOf<OtherItem>().toMutableList()
                            otherItems.add((OtherItem(otheritemsinfo=listOfFinalData)))
                            viewModel.feePayInfo.value.otheritems=otherItems
                            viewModel.payFee()*/
                            openPrePaymnetDialog.value = false

                        }) {
                            androidx.compose.material3.Text(text = "Pay Fee")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            openPrePaymnetDialog.value = false
                        }) {
                            androidx.compose.material3.Text(text = "Cancel")
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun HeaderLayout() {
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isSettingsExpanded by remember { mutableStateOf(false) }
    var isPrinterExpanded by remember { mutableStateOf(false) }
    var selectedPrinter by remember { mutableStateOf("") }

    // Replace with your desired user details
    val userDetails = remember{mutableStateOf(UserDetails("John Doe", "johndoe@example.com", "123 Main St", "555-123-4567", "www.example.com"))}

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
                    text = "Mobile Billing",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Column(horizontalAlignment = Alignment.End) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Menu Icon",
                        tint = White,
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



    if (isSettingsExpanded) {
        SettingsPopup(
            userDetails.value,
            onDismissRequest = { isSettingsExpanded = false },
            onSaveClicked = {
                Log.d("UserDetails",it.toString())
            }
        )
    }

    if (isPrinterExpanded) {
        PrinterPopup(
            selectedPrinter,
            onPrinterSelected = { printer ->
                selectedPrinter = printer
                isPrinterExpanded = false
            },
            onDismissRequest = { isPrinterExpanded = false }
        )
    }
}

@Composable
fun BluetoothScreen() {
    val context = LocalContext.current
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    var isBluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }
    var pairedDevices by remember { mutableStateOf(emptyList<BluetoothDevice>()) }
    var discoveredDevices by remember { mutableStateOf(emptyList<BluetoothDevice>()) }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null) {
                        discoveredDevices = discoveredDevices + device
                    }
                    Log.i("bluetooth", "device found")
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.i("bluetooth", "started discovery")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.i("bluetooth", "finished discovery")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        if (isBluetoothEnabled) {
            context.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
            context.registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
            context.registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
            bluetoothAdapter?.startDiscovery()
            pairedDevices = bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
        }
        onDispose {
            if (!isBluetoothEnabled) {
//                context.unregisterReceiver(receiver)
                bluetoothAdapter?.cancelDiscovery()
            }
        }
    }

    if (!isBluetoothEnabled) {
        requestBluetoothPermission(context)
    }


    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bluetooth Devices",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    if (isBluetoothEnabled) {
                        bluetoothAdapter?.disable().apply {
                            if (this!!)
                                isBluetoothEnabled = false
                        }
                    } else {
                        bluetoothAdapter?.enable().apply {
                            if (this!!)
                                isBluetoothEnabled = true
                        }
                    }
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = White
                )
            ) {
                Icon(painter = painterResource(id = if (isBluetoothEnabled) R.drawable.ic_bluetooth_enable else R.drawable.ic_bluetooth_disable), contentDescription = "",
                tint = if(isBluetoothEnabled) Green else Red)
            }
        }

        Text("Paired Devices:", style = MaterialTheme.typography.titleSmall)

        // List of recently connected Bluetooth pairing devices
        LazyColumn {
            items(pairedDevices) { device ->
                Text(device.name ?: "Unnamed Device",
                style = MaterialTheme.typography.bodySmall)
            }
        }

        Text("Discovered Devices:", style = MaterialTheme.typography.titleSmall)

        // List of recently discovered Bluetooth pairing devices
        LazyColumn {
            items(discoveredDevices) { device ->
                Text(device.name ?: "Unnamed Device",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun requestBluetoothPermission(context: Context) {
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                Log.i("bluetooth", "request permission result OK")
            } else {
                Log.i("bluetooth", "request permission result CANCELED/DENIED")
            }
        }
    LaunchedEffect(activityResultLauncher) {
        activityResultLauncher.launch(enableBluetoothIntent)
    }
}


@Composable
fun BluetoothScreenNew() {
    val context = LocalContext.current
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    val isBluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }
    var pairedDevices by remember { mutableStateOf(emptyList<BluetoothDevice>()) }
    var discoveredDevices by remember { mutableStateOf(emptyList<BluetoothDevice>()) }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null) {
                        discoveredDevices = discoveredDevices + device
                    }
                    Log.i("bluetooth", "device found")
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.i("bluetooth", "started discovery")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.i("bluetooth", "finished discovery")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val foundFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        val startFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        val endFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(receiver, foundFilter)
        context.registerReceiver(receiver, startFilter)
        context.registerReceiver(receiver, endFilter)
        Log.i("bluetooth", "filters registered")
    }

    if (!isBluetoothEnabled) {
        requestBluetoothPermission(context)
    }

    pairedDevices = bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()

    var isBluetoothElseCall by remember{ mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bluetooth Devices",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    if (isBluetoothEnabled) {
                        bluetoothAdapter?.disable()
                    } else {
                        bluetoothAdapter?.enable()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isBluetoothEnabled) Color.Red else Color.Green
                )
            ) {
                Text(text = if (isBluetoothEnabled) "Disable Bluetooth" else "Enable Bluetooth")
            }
        }

        Text("Paired Devices:", style = MaterialTheme.typography.bodySmall)

        // List of recently connected Bluetooth pairing devices
        LazyColumn {
            items(pairedDevices) { device ->
                Text(device.name ?: "Unnamed Device")
            }
        }

        Text("Discovered Devices:", style = MaterialTheme.typography.bodySmall)

        // List of recently discovered Bluetooth pairing devices
        LazyColumn {
            items(discoveredDevices) { device ->
                Text(device.name ?: "Unnamed Device")
            }
        }
    }
}
@Composable
private fun requestBluetoothPermissionNew(context: Context) {
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                Log.i("bluetooth", "request permission result OK")
            } else {
                Log.i("bluetooth", "request permission result CANCELED/DENIED")
            }
        }
    LaunchedEffect(activityResultLauncher) {
        activityResultLauncher.launch(enableBluetoothIntent)
    }
}
@Composable
fun SettingsPopup(
    userDetails: UserDetails,
    onDismissRequest: () -> Unit,
    onSaveClicked: (UserDetails) -> Unit
) {
    val context = LocalContext.current
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Settings") },
        confirmButton = {
            Button(
                onClick = { onDismissRequest() }
            ) {
                Text("Close")
            }
            Button(
                onClick = {
                    onSaveClicked(userDetails)
                    onDismissRequest()
                }
            ) {
                Text("Save")
            }
        },
        text = {
            Column {
                SettingsTextFieldSample(label = "Name", onTextChanged = {
                    userDetails.name = it
                })
                SettingsTextFieldSample(label = "Email", onTextChanged = {
                                                                         userDetails.email = it
                }, KeyboardType.Email)
                SettingsTextFieldSample(label = "Address", onTextChanged = {
                    userDetails.address = it
                })
                SettingsTextFieldSample(label = "Mobile", onTextChanged = {
                                                                          userDetails.mobile = it
                }, KeyboardType.Phone)
                SettingsTextFieldSample(label = "Website", onTextChanged = {
                    userDetails.website = it
                })
            }
        },
    )
}

@Composable
fun SettingsTextFieldSample(label:String,onTextChanged:(String) -> Unit,keyboardType: KeyboardType = KeyboardType.Text) {
    var value by remember{ mutableStateOf("") }
    androidx.compose.material.TextField(value = value, onValueChange = {
        onTextChanged(it)
        value = it
    },
    modifier = Modifier.fillMaxWidth(.95f),
    label = {Text(label,style = MaterialTheme.typography.titleSmall)},
    colors = TextFieldDefaults.textFieldColors(
        backgroundColor = White,
        focusedIndicatorColor = MainColor
    ))
}

@Composable
fun PrinterPopup(
    selectedPrinter: String,
    onPrinterSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val availablePrinters = listOf("Printer A", "Printer B", "Printer C")

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Printer Connection") },
        text = {
/*
            Column {
                Text("Selected Printer: $selectedPrinter")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Available Printers:")
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    availablePrinters.forEach { printer ->
                        Text(
                            text = printer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPrinterSelected(printer) }
                        )
                        Divider(color = Color.Gray, thickness = 1.dp)
                    }
                }
            }
*/
               BluetoothScreen()
        },
        confirmButton = {
            Button(
                onClick = { onDismissRequest() }
            ) {
                Text("Close")
            }
        },
    )
}

data class UserDetails(
    var name: String,
    var email: String,
    var address: String,
    var mobile: String,
    var website: String
)


@Composable
fun getAmount(onAmountSet: (Double) -> Unit) {
    val value = remember { mutableStateOf(0.0) }
    TextField(
        value = if(value.value == 0.0) "" else value.value.toString(), onValueChange = {

            if (it.isEmpty()){
                value.value = 0.0
            } else {
                value.value = when (it.toDoubleOrNull()) {
                    null -> value.value //old value
                    else -> it.toDouble()   //new value
                }
            }
            onAmountSet(value.value)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier,
        label = { Text("Amount", style = MaterialTheme.typography.bodySmall) },
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
    )
}
@Composable
fun SampleTextFieldDouble(label :String,text:Double,onAmountSet:(Double)-> Unit) {
    val value = remember{ mutableStateOf(text) }
    TextField(
        value = if(value.value == 0.0) "" else value.value.toString(),
        modifier = Modifier.fillMaxWidth(),
        onValueChange = {
            if (it.isEmpty()){
                value.value = 0.0
            } else {
                value.value = when (it.toDoubleOrNull()) {
                    null -> value.value //old value
                    else -> it.toDouble()   //new value
                }
            }
            onAmountSet(value.value)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label ={androidx.compose.material.Text(label)},
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
    )
}


