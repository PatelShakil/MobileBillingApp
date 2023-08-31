package com.mycampus.billingapp.ui.detail

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.mycampus.billingapp.R
import com.mycampus.billingapp.common.ScrollableCapturable
import com.mycampus.billingapp.common.Utils
import com.mycampus.billingapp.common.Utils.Companion.convertLongToDate
import com.mycampus.billingapp.common.Utils.Companion.saveFile
import com.mycampus.billingapp.common.Utils.Companion.sendWhatsAppMessage
import com.mycampus.billingapp.common.Utils.Companion.viewPdf
import com.mycampus.billingapp.common.rememberCaptureController
import com.mycampus.billingapp.common.uicomponents.DatePickerDialogCustom
import com.mycampus.billingapp.common.uicomponents.DottedDivider
import com.mycampus.billingapp.common.uicomponents.ErrorMessage
import com.mycampus.billingapp.common.uicomponents.ProgressBarCus
import com.mycampus.billingapp.data.models.BillItemCollectionPrint
import com.mycampus.billingapp.data.models.UserDetails
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollectionWithBillItems
import com.mycampus.billingapp.data.room.entities.CustomerItem
import com.mycampus.billingapp.ui.customer.CustomerViewModel
import com.mycampus.billingapp.ui.home.MainColor
import com.mycampus.billingapp.ui.home.UserViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt


@Composable
fun BillDetailScreen(
    viewModel: UserViewModel,
    customerViewModel: CustomerViewModel,
    navController: NavController,
) {
    var isFilterClick by remember { mutableStateOf(false) }
    var selectedDate by remember {
        mutableStateOf(
            Utils.convertLongToDate(
                System.currentTimeMillis(),
                "dd-MM-yyyy"
            )
        )
    }
    var itemCol by remember { mutableStateOf(listOf<BillItemCollectionWithBillItems>()) }
    var itemColOg by remember { mutableStateOf(listOf<BillItemCollectionWithBillItems>()) }
    viewModel.allItemCollections.observeForever {
        itemColOg = it
        itemCol = itemColOg.filter {
            Utils.convertLongToDate(
                it.itemCollection.bill_date,
                "dd-MM-yyyy"
            ) == selectedDate
        }
    }
    var customerCol by remember { mutableStateOf(listOf<CustomerItem>()) }

    var isPdfClick by remember { mutableStateOf(false) }
    var pdfIndex by remember { mutableStateOf("") }
    customerViewModel.allCustomers.observeForever {
        customerCol = it
    }
    var user by remember{mutableStateOf(UserDetails())}
    viewModel.userDetails.observeForever {
        user = it
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(.95f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = CenterVertically
        ) {
            Text(
                SimpleDateFormat(
                    "dd-MMMM-yyyy",
                    Locale.getDefault()
                ).format(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(selectedDate)),
                modifier = Modifier
                    .weight(.7f)
                    .padding(start = 5.dp),
                style = MaterialTheme.typography.titleSmall
            )
            Box(contentAlignment = CenterEnd) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter), "",
                    modifier = Modifier
                        .clickable {
                            isFilterClick = !isFilterClick
                        }
                        .size(35.dp),
                    tint = MainColor,
                )
            }
        }




        Spacer(modifier = Modifier.height(5.dp))
        if (itemCol.isNotEmpty() && customerCol.isNotEmpty()) {
            BillDetails(
                itemCol,
                customerCol,
                user!!,
                onBillDelete = {
                    viewModel.deleteBillItemCol(it)
                },
                onPdfClick = {
                    isPdfClick = true
                    pdfIndex = it
                }
            ) {
                //on Print Click
                Log.d("BillItemPrint", it.toString())
            }
        } else {
            ErrorMessage(msg = "No Corresponding record found...")
        }
        val deleteProgress = viewModel.deleteProcess.collectAsState()
        if (deleteProgress.value) {
            ProgressBarCus {
                //onDismiss
            }
        }
    }
    val context = LocalContext.current
    if (isFilterClick) {
        FilterPopup(onDismiss = {
            isFilterClick = !isFilterClick
        },
            onConfirm = { date, isOnline, isCash ->
                if (date.isNotEmpty()) {
                    selectedDate = date
                }

                if (isOnline) {
                    itemCol = itemColOg.filter {
                        Utils.convertLongToDate(
                            it.itemCollection.bill_date,
                            "dd-MM-yyyy"
                        ) == selectedDate &&
                                it.itemCollection.bill_pay_mode != "Paid by Cash"
                    }
                }
                if (isCash) {
                    itemCol = itemColOg.filter {
                        Utils.convertLongToDate(
                            it.itemCollection.bill_date,
                            "dd-MM-yyyy"
                        ) == selectedDate &&
                                it.itemCollection.bill_pay_mode == "Paid by Cash"
                    }
                }
                if (!isCash && !isOnline || (isCash && isOnline)) {
                    itemCol = itemColOg.filter {
                        Utils.convertLongToDate(
                            it.itemCollection.bill_date,
                            "dd-MM-yyyy"
                        ) == selectedDate
                    }
                }
            })
    }
    if (isPdfClick) {
        val billItems = itemCol.filter { it.itemCollection.bill_no == pdfIndex }[0]
        val customerItem =
            customerCol.filter { it.id == itemCol.filter { it.itemCollection.bill_no == pdfIndex }[0].itemCollection.customerid }[0]
        Dialog(
            onDismissRequest = {
                isPdfClick = !isPdfClick
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                val briView = ComposeView(context).apply {
                    setContent {
                            BillReceiptItemReceipt(
                                bill = billItems, customerItem = customerItem,
                                shop = user!!
                            )
                    }
                }

                var isViewLaidOut by remember { mutableStateOf(false) }
                val captureController = rememberCaptureController()
                ScrollableCapturable(controller = captureController, onCaptured ={bitmap , e ->
                    if(bitmap != null) {
                        GlobalScope.launch {
                            val filename = billItems.itemCollection.bill_no + convertLongToDate(
                                billItems.itemCollection.creation_date,
                                "_dd_MM_yyyy"
                            )
                            saveFile(filename, bitmap)
                            viewPdf(context as Activity, filename)
                            isPdfClick = !isPdfClick
                        }
                    }else{
                        Log.d("ERROR",e.toString())
                    }

                } ) {
                    AndroidView(factory = { briView })
                }
                LaunchedEffect(true) {
                    captureController.capture()
                }
                Spacer(modifier = Modifier.height(50.dp))
                ProgressBarCus {

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
fun BillReceiptItem(
    modifier: Modifier = Modifier,
    isNotSavePDF: Boolean = true,
    bill: BillItemCollectionWithBillItems,
    customerItem: CustomerItem,
    shop: UserDetails,
    onBillDelete: (BillItemCollectionWithBillItems) -> Unit,
    onPrintClick: (BillItemCollectionPrint) -> Unit,
    onPdfClick: (String) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth(.97f)
            .background(Color.Transparent),
        border = BorderStroke(1.dp, Color.Gray),
        elevation = CardDefaults.cardElevation(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 5.dp)
                .padding(bottom = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(.97f),
            ) {
                Column(modifier = Modifier.weight(.5f)) {
                    Text(
                        "Customer Info: ",
                        fontSize = 14.sp
                    )
                    Text(
                        "Name : " + customerItem.name,
                        fontSize = 12.sp
                    )
                    Text(
                        "Mobile : " +
                                customerItem.mobile,
                        fontSize = 12.sp
                    )
                }
                Column(
                    modifier = Modifier.weight(.5f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "Bill Info: ",
                        fontSize = 14.sp
                    )
                    Text(
                        "Bill No : " +
                                bill.itemCollection.bill_no,
                        fontSize = 12.sp
                    )
                    Text(
                        "Created By : " +
                                bill.itemCollection.created_by,
                        fontSize = 12.sp
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(.97f)) {
                Text(
                    text = "Address : ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight(500)
                )
                Text(
                    text = customerItem.address,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Column(
                modifier = Modifier
                    .border(1.dp, Color.Gray)
                    .fillMaxWidth(.97f)
            ) {
                Text(
                    "Bill at : " + Utils.convertLongToDate(
                        bill.itemCollection.bill_date,
                        "dd-MMMM-yyyy hh:mma"
                    ),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(5.dp),
                    color = Color.Black
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEBEBEB)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Item Name",
                        modifier = Modifier
                            .weight(.6f)
                            .padding(5.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight(400)
                    )
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(35.dp)
                            .border(1.dp, Color.Gray)
                    )
                    Text(
                        text = "Amount",
                        modifier = Modifier
                            .weight(.4f)
                            .padding(end = 5.dp),
                        textAlign = TextAlign.End,
                        fontSize = 14.sp,
                        fontWeight = FontWeight(400)
                    )
                }

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                )
                bill.itemList.forEachIndexed() { index, billItem ->
                    BillItemWithAmountOnBill(index = index + 1, data = billItem)
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray)
                    )
                }
                val totalAmount = bill.itemCollection.total_amount
                BillItemWithAmountOnBillBelow(
                    data = "Sub Total",
                    amount = (((totalAmount * bill.itemCollection.tax) / 100) + totalAmount).toString()
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                )
                BillItemWithAmountOnBillBelow(
                    data = "Tax" + " (@${bill.itemCollection.tax.roundToInt()}%)",
                    amount = ((totalAmount * bill.itemCollection.tax) / 100).toString()
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                )
                BillItemWithAmountOnBillBelow(
                    data = "Discount",
                    amount = bill.itemCollection.discount.toString()
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                )
                BillItemWithAmountOnBillBelow(
                    data = "Total Amount",
                    amount = (totalAmount + ((totalAmount * bill.itemCollection.tax) / 100) - bill.itemCollection.discount).toString()
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                )
                Text(
                    "Payment mode : " + if (bill.itemCollection.bill_pay_mode.trim() == "Paid by Cash") "Cash" else "Online",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .padding(vertical = 6.dp),
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                )
                Text(
                    "*" + bill.itemCollection.remarks,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .padding(vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(5.dp))

            if (isNotSavePDF) {
                BillReceiptButtonRow(
                    bill = bill,
                    customerItem = customerItem,
                    userDetails = shop,
                    onBillDelete = onBillDelete,
                    onPdfClick = {
                        onPdfClick(bill.itemCollection.bill_no)
                    },
                    onPrintClick = onPrintClick
                )
            }
        }
    }
}

@Composable
fun BillReceiptItemReceipt(
    modifier: Modifier = Modifier,
    bill: BillItemCollectionWithBillItems,
    customerItem: CustomerItem,
    shop: UserDetails
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(.97f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    shop.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = shop.address, style = MaterialTheme.typography.titleSmall)
                Text(
                    shop.mobile + " | " + shop.email,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            DottedDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = modifier
                    .fillMaxWidth(.97f)
                    .background(Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 5.dp)
                        .padding(bottom = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(.97f),
                    ) {
                        Column(modifier = Modifier.weight(.5f)) {
                            Text(
                                "Customer Info: ",
                                fontSize = 14.sp
                            )
                            Text(
                                "Name : " + customerItem.name,
                                fontSize = 12.sp
                            )
                            Text(
                                "Mobile : " +
                                        customerItem.mobile,
                                fontSize = 12.sp
                            )
                        }
                        Column(
                            modifier = Modifier.weight(.5f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                "Bill Info: ",
                                fontSize = 14.sp
                            )
                            Text(
                                "Bill No : " +
                                        bill.itemCollection.bill_no,
                                fontSize = 12.sp
                            )
                            Text(
                                "Created By : " +
                                        bill.itemCollection.created_by,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(.97f)) {
                        Text(
                            text = "Address : ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight(500)
                        )
                        Text(
                            text = customerItem.address,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Column(
                        modifier = Modifier
                            .border(1.dp, Color.Gray)
                            .fillMaxWidth(.97f)
                    ) {
                        Text(
                            "Bill at : " + Utils.convertLongToDate(
                                bill.itemCollection.bill_date,
                                "dd-MMMM-yyyy hh:mma"
                            ),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(5.dp),
                            color = Color.Black
                        )
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEBEBEB)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Item Name",
                                modifier = Modifier
                                    .weight(.6f)
                                    .padding(5.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight(400)
                            )
                            Spacer(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(35.dp)
                                    .border(1.dp, Color.Gray)
                            )
                            Text(
                                text = "Amount",
                                modifier = Modifier
                                    .weight(.4f)
                                    .padding(end = 5.dp),
                                textAlign = TextAlign.End,
                                fontSize = 14.sp,
                                fontWeight = FontWeight(400)
                            )
                        }

                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray)
                        )
                        bill.itemList.forEachIndexed() { index, billItem ->
                            BillItemWithAmountOnBill(index = index + 1, data = billItem)
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.Gray)
                            )
                        }
                        val totalAmount = bill.itemCollection.total_amount
                        BillItemWithAmountOnBillBelow(
                            data = "Sub Total",
                            amount = (((totalAmount * bill.itemCollection.tax) / 100) + totalAmount).toString()
                        )
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray)
                        )
                        BillItemWithAmountOnBillBelow(
                            data = "Tax" + " (@${bill.itemCollection.tax.roundToInt()}%)",
                            amount = ((totalAmount * bill.itemCollection.tax) / 100).toString()
                        )
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray)
                        )
                        BillItemWithAmountOnBillBelow(
                            data = "Discount",
                            amount = bill.itemCollection.discount.toString()
                        )
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray)
                        )
                        BillItemWithAmountOnBillBelow(
                            data = "Total Amount",
                            amount = (totalAmount + ((totalAmount * bill.itemCollection.tax) / 100) - bill.itemCollection.discount).toString()
                        )
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray)
                        )
                        Text(
                            "Payment mode : " + if (bill.itemCollection.bill_pay_mode.trim() == "Paid by Cash") "Cash" else "Online",
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .padding(vertical = 6.dp)
                        )
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray)
                        )
                        Text(
                            "*" + bill.itemCollection.remarks,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .padding(vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            DottedDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Bill Receipt generated by Billing App - No Signature required",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth(0.97f),
                textAlign = TextAlign.Center,
                softWrap = true,
                maxLines = Int.MAX_VALUE // Allow unlimited lines

            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun BillReceiptButtonRow(
    bill: BillItemCollectionWithBillItems,
    customerItem: CustomerItem,
    userDetails: UserDetails,
    onBillDelete: (BillItemCollectionWithBillItems) -> Unit,
    onPdfClick: () -> Unit,
    onPrintClick: (BillItemCollectionPrint) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        val context = LocalContext.current
        Box(
            modifier = Modifier
                .background(MainColor, RoundedCornerShape(15.dp))
                .clip(RoundedCornerShape(15.dp))
                .clickable {
                    var msg = "Dear ${customerItem.name},\n\n" +
                        "Thanks for your visit at ${userDetails.name}.\n" +
                        "Total bill amount : Rs ${bill.itemCollection.total_amount + (bill.itemCollection.total_amount * bill.itemCollection.tax) / 100}\n\n" +
                        "Bill Item\n"
                    bill.itemList.forEachIndexed { index, billItem ->
                        msg += "${index + 1}.${billItem.item_name} - Rs ${billItem.item_amount}\n"
                    }

                    msg += "\nAmount paid - Rs ${bill.itemCollection.total_amount + (bill.itemCollection.total_amount * bill.itemCollection.tax) / 100 - bill.itemCollection.discount}\n" +
                            "Payment Mode : ${if (bill.itemCollection.bill_pay_mode.trim() == "Paid by Cash") "Cash" else "Online"}.\n"

                    msg += if (bill.itemCollection.discount > 0.0) "Discount - Rs ${bill.itemCollection.discount}\n" else ""
                    msg += if (bill.itemCollection.balance_amount > 0.0) "Balance - Rs ${bill.itemCollection.balance_amount}\n" else ""

                    msg += "\nRegards,\nBilling App"

                    sendWhatsAppMessage(context,msg,"91${customerItem.mobile}")

                },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painterResource(id = R.drawable.ic_whatsapp), "",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
//                    Text("Print", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .background(MainColor, RoundedCornerShape(15.dp))
                .clip(RoundedCornerShape(15.dp))
                .clickable {
                    onPdfClick()
                },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painterResource(id = R.drawable.ic_pdf), "",
                    tint = Color.White
                )
//                    Text("Print", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .background(MainColor, RoundedCornerShape(15.dp))
                .clip(RoundedCornerShape(15.dp))
                .clickable {
                    val billPrint = BillItemCollectionPrint(
                        bill.itemCollection.id,
                        customerItem,
                        bill.itemCollection.bill_no,
                        bill.itemCollection.bill_pay_mode,
                        bill.itemCollection.tax,
                        bill.itemCollection.total_amount,
                        bill.itemCollection.paid_amount,
                        bill.itemCollection.balance_amount,
                        bill.itemCollection.discount,
                        bill.itemCollection.remarks,
                        bill.itemCollection.creation_date,
                        bill.itemCollection.created_by,
                        bill.itemList,
                        bill.itemCollection.is_sync
                    )
                    onPrintClick(billPrint)
                },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painterResource(id = R.drawable.ic_printer), "",
                    tint = Color.White
                )
//                    Text("Print", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .background(MainColor, RoundedCornerShape(15.dp))
                .clip(RoundedCornerShape(15.dp))
                .clickable {
                    onBillDelete(bill)
                },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Default.Delete, "",
                    tint = Color.White
                )
//                    Text("Delete", color = Color.White)
            }
        }
    }
}

@Composable
fun BillDetails(
    list: List<BillItemCollectionWithBillItems>,
    customerList: List<CustomerItem>,
    shop: UserDetails,
    onBillDelete: (BillItemCollectionWithBillItems) -> Unit,
    onPdfClick: (String) -> Unit,
    onPrintClick: (BillItemCollectionPrint) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(list) { bill ->


            BillReceiptItem(
                bill = bill,
                customerItem = customerList.filter { it.id == bill.itemCollection.customerid }[0],
                shop = shop,
                onBillDelete = {
                    onBillDelete(it)
                }, onPrintClick = { onPrintClick(it) }) {
                onPdfClick(it)

            }

            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }


}

@Composable
fun BillItemWithAmountOnBill(index: Int, data: BillItem) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$index. ${data.item_name}",
            modifier = Modifier
                .weight(.6f)
                .padding(5.dp),
            fontSize = 12.sp
        )
        Spacer(
            modifier = Modifier
                .width(1.dp)
                .height(30.dp)
                .border(2.dp, Color.Gray)
        )
        Text(
            text = "₹${data.item_amount}",
            modifier = Modifier
                .weight(.4f)
                .padding(end = 5.dp),
            textAlign = TextAlign.End,
            fontSize = 12.sp
        )
    }
}

@Composable
fun BillItemWithAmountOnBillBelow(data: String, amount: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = data,
            modifier = Modifier
                .weight(.6f)
                .padding(end = 4.dp),
            textAlign = TextAlign.End,
            fontSize = 12.sp
        )
        Spacer(
            modifier = Modifier
                .width(1.dp)
                .height(30.dp)
                .border(2.dp, Color.Gray)
        )
        Text(
            text = "₹ $amount",
            modifier = Modifier
                .weight(.4f)
                .padding(end = 5.dp),
            textAlign = TextAlign.End,
            fontSize = 12.sp,
        )
    }
}