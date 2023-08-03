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
                val viewModel = hiltViewModel<BluetoothViewModel>()
                val userViewModel = hiltViewModel<UserViewModel>()
                val state by viewModel.state.collectAsState()
                var itemCol by remember { mutableStateOf(listOf<BillItemCollectionWithBillItems>()) }
                userViewModel.allItemCollections.observeForever {
                    itemCol = it
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
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

                    }
                }
            }
        }
    }

    @Composable
    fun BottomCard(
        date: String,
        totalAmount: Double,
        cashAmount: Double,
        onlineAmount: Double,
        onClick:() -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            border = BorderStroke(.5.dp, Gray),
            elevation = CardDefaults.cardElevation(30.dp)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(White),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(10.dp))
                Text("Total bill of $date")
                Spacer(modifier = Modifier.height(5.dp))
                Box(modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(LightMainColor),
                    contentAlignment = Alignment.Center){
                    Icon(
                        painter = painterResource(id = R.drawable.ic_rupee),
                        "",
                        tint = MainColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text("₹ $totalAmount",
                fontSize = 16.sp)
                Text("Cash ₹ $cashAmount", fontSize = 12.sp)
                Text("Online ₹ $onlineAmount", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainColor
                ),
                modifier = Modifier.fillMaxWidth(.8f)) {
                    Text("View Details")
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

        }
    }

    val MainColor = Color(0xFF00638E)
    val LightMainColor = Color(0xFFD2E1F1)
    data class CollectFeeData(
        val totalAmount: Double = 0.0,
        val discount: Double = 0.0,
//    val othrItemlist: List<OtherItemsInfo>
    )

    @Composable
    fun MainScreenFees(
        userDetails: UserDetails,
        onProceedClicked: (List<CollectFeeData>) -> Unit,
        navController: NavController,
        onFeePaid: (BillItemCollection, List<BillItem>) -> Unit
    ) {
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

        val onBackPressedDispatcher =
            LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
        val openPostPaymnetDialog = remember { mutableStateOf(false) }
        val openPrePaymnetDialog = remember { mutableStateOf(false) }
//    val payFee = viewModel.payFee.collectAsState()
//    val othrItemlist: List<OtherItemsInfo>
        var transactionRemark by remember { mutableStateOf("Paid by Cash") }


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 30.dp),
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
                Row(
                    modifier = Modifier.fillMaxWidth(.95f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                )
                Spacer(modifier = Modifier.height(5.dp))
                var discountAmount by remember { mutableStateOf(0.0) }
                var taxPer by remember { mutableStateOf(0.0) }
                var totalAmount = 0.0

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
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                )
                val itemsList = ArrayList<BillItem>()

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(isAddClick.value) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val item by remember {
                                mutableStateOf(
                                    BillItem(
                                        0,
                                        0,
                                        "",
                                        0.0,
                                        System.currentTimeMillis(),
                                        userDetails.name
                                    )
                                )
                            }
                            val amount = remember {
                                mutableStateOf(0.0)
                            }
                            val itemName = remember { mutableStateOf("") }
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
                                    value = itemName.value, onValueChange = {
                                        itemName.value = it
                                        item.item_name = it
                                    },
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
                                    getAmount(onAmountSet = {
                                        amount.value = it.toDouble()
                                        item.item_amount = it.toDouble()

                                    })
                                }
                                totalAmount += amount.value
                                itemsList.add(item)
                            }
//                        listOfFinalData.add(OtherItemsInfo( amount.value, item.value))
                        }
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(.95f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(modifier = Modifier.weight(.4f))
                        SampleTextFieldDouble(
                            label = "Tax", text = 0, onAmountSet = {
                                taxPer = it.toDouble()
                            },
                            modifier = Modifier.weight(.3f)
                        )
                        Spacer(Modifier.width(10.dp))
                        SampleTextFieldDouble(
                            label = "Discount", text = 0, onAmountSet = {
                                discountAmount = it.toDouble()
                            },
                            modifier = Modifier.weight(.3f)
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                    )
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
                            if (it) {
                                transactionRemark = "Paid By PhonePay"
                            } else {
                                transactionRemark = "Paid by Cash"
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
                            transactionRemark = it
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
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

                Button(
                    onClick = {
                        openPrePaymnetDialog.value = true
                        //onProceedClicked(feeDataList)
                    },
                    enabled = totalAmount > 0
//                        && listOfFinalData.isNotEmpty()
                ) {
                    Text(
                        "Pay Rs ${
                            if (totalAmount > 0) (totalAmount * (1 + taxPer / 100)).roundToInt()
                                    - discountAmount else (totalAmount * (1 + taxPer / 100)).roundToInt()
                        }",
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
                            Column {
                                val INR = "₹"
                                androidx.compose.material3.Text(
                                    text = "Total  Amount  : $INR ${totalAmount} \n" +
                                            "Tax Amount : $INR ${(totalAmount * (1 + taxPer / 100)).roundToInt() - totalAmount}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Row {
                                    Text(
                                        text = "Discount Amount : ",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "$INR -${discountAmount}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MainColor
                                    )
                                }
                                Text(
                                    text = "Going to Pay    : $INR ${(totalAmount * (1 + taxPer / 100)).roundToInt() - discountAmount.roundToInt()}  \n" +
                                            //"Balance Amount  - $INR ${0.0}",
                                            "Payment Mode - ${transactionRemark} ",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                Log.d("Items List", itemsList.toString())
                                val billCol = BillItemCollection(
                                    0,
                                    userDetails.name,
                                    userDetails.address,
                                    userDetails.mobile,
                                    userDetails.email,
                                    "billno",
                                    transactionRemark,
                                    taxPer,
                                    totalAmount,
                                    (totalAmount * (1 + taxPer / 100)).roundToInt() - discountAmount.roundToInt()
                                        .toDouble(),
                                    0.0,
                                    discountAmount,
                                    "remarks",
                                    System.currentTimeMillis(),
                                    userDetails.name,
                                    false
                                )
                                onFeePaid(billCol, itemsList)
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
    fun HeaderLayout(
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
                            text = "Mobile Billing",
                            fontWeight = FontWeight.Bold,
                            color = White
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
            HeaderScreen(userDetails) {
                isSettingsExpanded = true
            }
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

    @Composable
    fun HeaderScreen(userDetails: UserDetails?, onSetUserDetails: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth(.95f)
                .padding(top = 10.dp),
            border = BorderStroke(.5.dp, Black),
            elevation = CardDefaults.cardElevation(30.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (userDetails == null || userDetails == UserDetails()) {
                    Button(
                        onClick = onSetUserDetails,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text("Add Details")
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(.95f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            userDetails.name,
                            modifier = Modifier.padding(start = 0.dp),
                            fontSize = 20.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                userDetails.mobile + " | " + userDetails.email,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                userDetails.address,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                userDetails.GST,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.width(5.dp))
                            Text(
                                userDetails.website,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Blue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
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
                        .padding(20.dp)
                ) {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(10.dp)
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
    fun SettingsTextFieldSample(
        label: String,
        value: String = "",
        onTextChanged: (String) -> Unit,
        keyboardType: KeyboardType = KeyboardType.Text,
        lineCount: Int = 1
    ) {
        var value by remember { mutableStateOf(value) }
        androidx.compose.material.TextField(
            value = value,
            onValueChange = {
                onTextChanged(it)
                value = it
            },
            modifier = Modifier.fillMaxWidth(.95f),
            label = { Text(label, style = MaterialTheme.typography.titleSmall) },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xFFEAE8F0),
                focusedIndicatorColor = MainColor
            ),
            minLines = lineCount,
            singleLine = lineCount <= 1,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }

    @Composable
    fun PrinterPopup(
        pairedDevices: List<com.mycampus.billingapp.domain.bluetooth.BluetoothDevice>,
        scannedDevices: List<com.mycampus.billingapp.domain.bluetooth.BluetoothDevice>,
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
                        .padding(20.dp)
                ) {
                    Text(
                        "Printer Connection",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                    )
                    if (pairedDevices.isNotEmpty() && scannedDevices.isNotEmpty()) {
                        BluetoothDeviceList(
                            pairedDevices = pairedDevices,
                            scannedDevices = scannedDevices,
                            onClick = {},
                            modifier = Modifier
                                .padding(vertical = 20.dp)
                                .fillMaxHeight(.7f)
                        )
                    } else {
                        //TODO//handle devices if no device detected | make call OnAPIFailed() composable
                        Text("no device found", textAlign = TextAlign.Center)
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

    @SuppressLint("MissingPermission")
    @Composable
    fun BluetoothDeviceList(
        pairedDevices: List<com.mycampus.billingapp.domain.bluetooth.BluetoothDevice>,
        scannedDevices: List<com.mycampus.billingapp.domain.bluetooth.BluetoothDevice>,
        onClick: (com.mycampus.billingapp.domain.bluetooth.BluetoothDevice) -> Unit,
        modifier: Modifier = Modifier
    ) {
        LazyColumn(
            modifier = modifier.wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Paired Devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
            items(pairedDevices) { device ->
                BTDeviceItem(device = device, onClick = onClick)
            }

            item {
                Text(
                    text = "Scanned Devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
            items(scannedDevices) { device ->
                BTDeviceItem(device = device, onClick = onClick)
            }
        }
    }

    @Composable
    fun BTDeviceItem(device: BluetoothDevice, onClick: (BluetoothDevice) -> Unit) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onClick(device) }) {
            Text(
                text = device.name ?: "(No name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 7.dp, horizontal = 5.dp)
                    .padding(start = 10.dp),
                style = MaterialTheme.typography.titleSmall
            )
        }

    }

    @Composable
    fun getAmount(onAmountSet: (Int) -> Unit) {
        var value by remember { mutableStateOf(0) }
        TextField(
            value = if (value == 0) "" else value.toString(),
            onValueChange = {
                if (it.isEmpty()) {
                    value = 0
                    onAmountSet(value)
                } else {
                    value = it.toInt()
                    onAmountSet(value)
                }
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
    fun SampleTextFieldDouble(
        modifier: Modifier = Modifier,
        label: String,
        text: Int,
        onAmountSet: (Int) -> Unit
    ) {
        var value by remember { mutableStateOf(text) }
        TextField(
            value = if (value == 0) "" else value.toString(),
            modifier = modifier,
            onValueChange = {
                if (it.isEmpty()) {
                    value = 0
                    onAmountSet(value)
                } else {
                    value = it.toInt()
                    onAmountSet(value)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
        )
    }
}


