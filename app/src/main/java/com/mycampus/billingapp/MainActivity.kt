package com.mycampus.billingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            onDismissRequest = { isSettingsExpanded = false }
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
fun SettingsPopup(
    userDetails: UserDetails,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    /*Dialog(onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(5.dp))
                Text("Settings",
                style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(5.dp))
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .height(.5.dp), color = Color.Gray)
                Spacer(modifier = Modifier.height(5.dp))
                Column {
                    Text("Name: ${userDetails.name}")
                    Text("Email: ${userDetails.email}")
                    Text("Address: ${userDetails.address}")
                    Text("Mobile: ${userDetails.mobile}")
                    Text("Website: ${userDetails.website}")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                        onClick = { onDismissRequest() }
                ) {
                Text("Close")
            }
            }
        }
    }*/
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Settings") },
        confirmButton = {
            Button(
                onClick = { onDismissRequest() }
            ) {
                Text("Close")
            }
        },
        text = {
            Column {
                Text("Name: ${userDetails.name}")
                Text("Email: ${userDetails.email}")
                Text("Address: ${userDetails.address}")
                Text("Mobile: ${userDetails.mobile}")
                Text("Website: ${userDetails.website}")
            }
        },
    )
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
    val name: String,
    val email: String,
    val address: String,
    val mobile: String,
    val website: String
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


