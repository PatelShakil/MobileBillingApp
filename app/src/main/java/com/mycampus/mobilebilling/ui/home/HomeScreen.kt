package com.mycampus.mobilebilling.ui.home

import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mycampus.mobilebilling.common.LightMainColor
import com.mycampus.mobilebilling.common.MainColor
import com.mycampus.mobilebilling.common.Utils
import com.mycampus.mobilebilling.common.uicomponents.AddCustomerPopupScreen
import com.mycampus.mobilebilling.common.uicomponents.ConfirmationDialog
import com.mycampus.mobilebilling.common.uicomponents.CusDropdownSearch
import com.mycampus.mobilebilling.common.uicomponents.DateTimePicker
import com.mycampus.mobilebilling.common.uicomponents.DropDownItemData
import com.mycampus.mobilebilling.common.uicomponents.GetAmount
import com.mycampus.mobilebilling.common.uicomponents.ProgressBarCus
import com.mycampus.mobilebilling.common.uicomponents.SampleTextFieldDouble
import com.mycampus.mobilebilling.data.models.BillItemCollectionPrint
import com.mycampus.mobilebilling.data.models.CollectFeeData
import com.mycampus.mobilebilling.data.models.UserDetails
import com.mycampus.mobilebilling.data.room.entities.BillItem
import com.mycampus.mobilebilling.data.room.entities.BillItemCollection
import com.mycampus.mobilebilling.data.room.entities.BillItemCollectionWithBillItems
import com.mycampus.mobilebilling.data.room.entities.CustomerItem
import com.mycampus.mobilebilling.ui.customer.CustomerViewModel
import com.mycampus.mobilebilling.ui.nav.Screen
import com.shakilpatel.mobilebilling.R
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.roundToInt


@Composable
fun HomeScreen(
    viewModel: UserViewModel,
    customerViewModel: CustomerViewModel,
    navController: NavController
) {
    var userDetails by remember{ mutableStateOf(UserDetails()) }
    LaunchedEffect(true){
        viewModel.getUser()
    }
    viewModel.userDetails.observeForever {
        if(it != null) {
            userDetails = it
        }
    }
    var isSettingsExpanded by remember { mutableStateOf(false) }
    var itemCol by remember { mutableStateOf(listOf<BillItemCollectionWithBillItems>()) }
    viewModel.allItemCollections.observeForever {
        itemCol = it
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderScreen(userDetails = userDetails) {
            isSettingsExpanded = true
        }
        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            var customerCol by remember { mutableStateOf(listOf<CustomerItem>()) }
            customerViewModel.allCustomers.observeForever {
                customerCol = it
            }

            var billItemCollectionPrint by remember { mutableStateOf<BillItemCollectionPrint?>(null) }

            if (userDetails != UserDetails()) {
                MainScreenFees(
                    userDetails,
                    customerCol,
                    onProceedClicked = {},
                    navController = NavController(LocalContext.current), { billCol, list ->
                        billItemCollectionPrint = BillItemCollectionPrint(
                            billCol.id,
                            customerCol.filter { it.id == billCol.customerid }[0],
                            billCol.bill_no,
                            billCol.bill_pay_mode,
                            billCol.tax,
                            billCol.total_amount,
                            billCol.paid_amount,
                            billCol.balance_amount,
                            billCol.discount,
                            billCol.remarks,
                            billCol.creation_date,
                            billCol.created_by,
                            list,
                            billCol.is_sync
                        )
                        viewModel.addItemCollection(billCol, list)
                    }) {
                    customerViewModel.addCustomer(it)
                }
            }
            var insertResult = viewModel.insertResult.collectAsState()
            if (insertResult.value) {
                ProgressBarCus() {
                }
            }
            val isInserted = viewModel.isInserted.collectAsState()
            if (isInserted.value) {
                ConfirmationDialog(onDismiss = {
                    viewModel.isInserted.value = false
                }) {
                    //use this object for printing bill receipt
                    Log.d("Bill Receipt Print", billItemCollectionPrint.toString())

                }
            }

            if (itemCol.isNotEmpty()) {
                val date = Utils.convertLongToDate(System.currentTimeMillis(), "dd MMMM yyyy")
                val totalAmount =
                    itemCol.filter {
                        Utils.convertLongToDate(
                            it.itemCollection.creation_date,
                            "DDMMYY"
                        ) == Utils.convertLongToDate(System.currentTimeMillis(), "DDMMYY")
                    }.sumOf { bill ->
                        bill.itemCollection.total_amount + (bill.itemCollection.total_amount * bill.itemCollection.tax) / 100 - bill.itemCollection.discount
                    }
                val cashAmount = itemCol.filter {
                    Utils.convertLongToDate(
                        it.itemCollection.creation_date,
                        "DDMMYY"
                    ) == Utils.convertLongToDate(System.currentTimeMillis(), "DDMMYY") &&
                            it.itemCollection.bill_pay_mode == "Paid by Cash"
                }.sumOf { bill ->
                    bill.itemCollection.total_amount + (bill.itemCollection.total_amount * bill.itemCollection.tax) / 100 - bill.itemCollection.discount
                }
                val onlineAmount = itemCol.filter {
                    Utils.convertLongToDate(
                        it.itemCollection.creation_date,
                        "DDMMYY"
                    ) == Utils.convertLongToDate(System.currentTimeMillis(), "DDMMYY") &&
                            it.itemCollection.bill_pay_mode != "Paid by Cash"
                }.sumOf { bill ->
                    bill.itemCollection.total_amount + (bill.itemCollection.total_amount * bill.itemCollection.tax) / 100 - bill.itemCollection.discount
                }
                BottomCard(date, totalAmount, cashAmount, onlineAmount) {
                    navController.navigate(Screen.Details.route)

                }

            }

            Spacer(Modifier.height(150.dp))

        }

    }
    if (isSettingsExpanded) {
        LaunchedEffect(key1 = true){
            navController.navigate(Screen.Setting.route)
        }
    }
}


@Composable
fun BottomCard(
    date: String,
    totalAmount: Double,
    cashAmount: Double,
    onlineAmount: Double,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        border = BorderStroke(.5.dp, Gray),
        elevation = CardDefaults.cardElevation(30.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Total bill of $date")
            Spacer(modifier = Modifier.height(5.dp))
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(LightMainColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_rupee),
                    "",
                    tint = MainColor,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                "₹ $totalAmount",
                fontSize = 16.sp
            )
            Text("Cash ₹ $cashAmount", fontSize = 12.sp)
            Text("Online ₹ $onlineAmount", fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainColor
                ),
                modifier = Modifier.fillMaxWidth(.8f)
            ) {
                Text("View Details")
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

    }
}


@Composable
fun MainScreenFees(
    userDetails: UserDetails,
    customersList: List<CustomerItem>,
    onProceedClicked: (List<CollectFeeData>) -> Unit,
    navController: NavController,
    onFeePaid: (BillItemCollection, List<BillItem>) -> Unit,
    onCustomerAddClicked: (CustomerItem) -> Unit
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
        var remarks by remember { mutableStateOf("") }
        val dateTime = LocalDateTime.now()
        var selectedDateTime = remember { mutableStateOf(dateTime) }
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
                    "Create Bill",

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
            var customerid by remember { mutableStateOf("") }
            var isCustomerAdd by remember { mutableStateOf(false) }
            val isSubmitted = remember { mutableStateOf(false) }
            val itemsList = ArrayList<BillItem>()
            val selectedCustomer = remember { mutableStateOf(DropDownItemData()) }

            Row(verticalAlignment = Alignment.CenterVertically) {

                CusDropdownSearch(
                    selectedItem = selectedCustomer.value,
                    isSubmitted.value,
                    label = "Add/Search Customer",
                    options = customersList.map { DropDownItemData(it.id.toString(), it.name) }
                        .sortedBy { it.name },
                    onSelected = {
                        selectedCustomer.value = it
                        customerid = it.id
                    }) {
                    isCustomerAdd = !isCustomerAdd
                }

            }
            if (isCustomerAdd) {
                AddCustomerPopupScreen(customer = CustomerItem(0, "", "", "", ""), onDismiss = {
                    isCustomerAdd = !isCustomerAdd
                }, onConfirm = {
                    onCustomerAddClicked(it)
                })
            }

            Box(modifier = Modifier
                .background(Color(0xFFEBEBEB))
                .fillMaxWidth()
                .padding(10.dp)) {
                Text(
                    "Item Name", modifier = Modifier.align(Alignment.CenterStart),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "Item Amount", modifier = Modifier.align(Alignment.CenterEnd),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(isAddClick.value) {
                    val amount = remember {
                        mutableStateOf(0.0)
                    }
                    val itemName = remember { mutableStateOf("") }
                    if (isSubmitted.value) {
                        amount.value = 0.0
                        itemName.value = ""
                        itemsList.clear()
                    }
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
                                GetAmount(amount.value.roundToInt(), isSubmitted, onAmountSet = {
                                    amount.value = it.toDouble()
                                    item.item_amount = it.toDouble()
                                }) {
                                }
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
                        label = "Tax",
                        text = taxPer.roundToInt(),
                        isSubmitted = isSubmitted,
                        onAmountSet = {
                            taxPer = it.toDouble()
                        },
                        modifier = Modifier.weight(.3f)
                    ) {

                    }
                    Spacer(Modifier.width(10.dp))
                    SampleTextFieldDouble(
                        label = "Discount",
                        text = taxPer.roundToInt(),
                        isSubmitted = isSubmitted,
                        onAmountSet = {
                            discountAmount = it.toDouble()
                        },
                        modifier = Modifier.weight(.3f)
                    ) {

                        isSubmitted.value = false
                    }
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                )
                TextField(
                    value = remarks, onValueChange = {
                        remarks = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Remarks", style = MaterialTheme.typography.bodySmall)
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Transparent,
                        focusedIndicatorColor = MainColor
                    )
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                )
                DateTimePicker(
                    "Date and Time",
                    "Select Date and Time",
                    selectedDateTime = selectedDateTime,
                    onDateTimeSelected = {
                        selectedDateTime.value = it
                    }
                )
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
                                customerid.toLong(),
                                Utils.generateRandomValue(9),
                                transactionRemark,
                                taxPer,
                                totalAmount.roundToInt().toDouble(),
                                (totalAmount * (1 + taxPer / 100)).roundToInt() - discountAmount.roundToInt()
                                    .toDouble(),
                                0.0,
                                discountAmount.roundToInt().toDouble(),
                                remarks,
                                System.currentTimeMillis(),
                                selectedDateTime.value.atZone(ZoneId.systemDefault()).toInstant()
                                    .toEpochMilli(),
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
                            taxPer = 0.0
                            totalAmount = 0.0
                            discountAmount = 0.0
                            selectedDateTime.value = LocalDateTime.now()
                            isAddClick.value = 1
                            remarks = ""
                            isSubmitted.value = true
                            customerid = ""
                            selectedCustomer.value = DropDownItemData()
                            isOnileModeChecked.value = false
                            openPrePaymnetDialog.value = false

                        }) {
                            androidx.compose.material3.Text(text = "Pay Bill")
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


