package com.mycampus.billingapp.ui.detail

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.mycampus.billingapp.R
import com.mycampus.billingapp.common.Utils
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollectionWithBillItems
import com.mycampus.billingapp.data.room.entities.CustomerItem
import com.mycampus.billingapp.ui.customer.CustomerViewModel
import com.mycampus.billingapp.ui.home.MainColor
import com.mycampus.billingapp.ui.home.UserViewModel
import kotlin.math.roundToInt


@Composable
fun BillDetailScreen(
    viewModel: UserViewModel,
    customerViewModel: CustomerViewModel,
    navController: NavController
) {
    var isFilterClick by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Utils.convertLongToDate(System.currentTimeMillis(),"dd-MM-yyyy")) }
    var itemCol by remember { mutableStateOf(listOf<BillItemCollectionWithBillItems>()) }
    var itemColOg by remember { mutableStateOf(listOf<BillItemCollectionWithBillItems>()) }
    viewModel.allItemCollections.observeForever {
        itemColOg = it
        itemCol = itemColOg.filter {
            Utils.convertLongToDate(
                it.itemCollection.creation_date,
                "dd-MM-yyyy"
            ) == selectedDate
        }
    }
    var customerCol by remember { mutableStateOf(listOf<CustomerItem>()) }

    customerViewModel.allCustomers.observeForever {
        customerCol = it
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(.95f),
            horizontalArrangement = Arrangement.End
        ) {
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



        Spacer(modifier = Modifier.height(5.dp))
        if (itemCol.isNotEmpty() && customerCol.isNotEmpty()) {
            BillDetails(
                itemCol,
                customerCol
            )
        }else{
            Text("No Corresponding record found...",modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center)
        }
    }
    if (isFilterClick) {
        FilterPopup(onDismiss = {
            isFilterClick = !isFilterClick
        },
            onConfirm = { date, isOnline, isCash ->
                if(date.isNotEmpty()) {
                    selectedDate = date
                }
                Log.d("IsOnline",isOnline.toString())
                Log.d("IsCash",isCash.toString())

                if(isOnline){
                    itemCol = itemColOg.filter {
                        Utils.convertLongToDate(
                            it.itemCollection.creation_date,
                            "dd-MM-yyyy"
                        ) == selectedDate &&
                                it.itemCollection.bill_pay_mode != "Paid by Cash"
                    }
                }
                if(isCash){
                    itemCol = itemColOg.filter {
                        Utils.convertLongToDate(it.itemCollection.creation_date,"dd-MM-yyyy") == selectedDate &&
                                it.itemCollection.bill_pay_mode == "Paid by Cash"
                    }
                }
                if(!isCash && !isOnline) {
                    itemCol = itemColOg.filter {
                        Utils.convertLongToDate(
                            it.itemCollection.creation_date,
                            "dd-MM-yyyy"
                        ) == selectedDate
                    }
                }
            })
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
        var date by remember{ mutableStateOf("") }
        var isCash by remember{ mutableStateOf(false) }
        var isOnline by remember{ mutableStateOf(false) }
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
                            DatePickerDialogCustom(date = "", label = "Date", onDateSelect = { year,month,day ->
                                date = "$day-$month-$year"
                            })
                            Row(modifier = Modifier.fillMaxWidth(.95f), verticalAlignment = Alignment.CenterVertically) {
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
                                onConfirm(date,isOnline,isCash)
                                onDismiss()
                            },modifier = Modifier.fillMaxWidth(.8f)) {
                                Text(text = "Apply")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DatePickerDialogCustom(date: String, label: String, onDateSelect: (String,String,String) -> Unit) {
    var date by remember {
        mutableStateOf(date)
    }
    var isDatePickerShow by remember { mutableStateOf(false) }
    androidx.compose.material.OutlinedTextField(value = date, onValueChange = {},
        leadingIcon = {
            Icon(
                painterResource(id = R.drawable.ic_calendar), contentDescription = "",
                tint = MainColor
            )
        },
        label = { androidx.compose.material.Text("Date", color = MainColor) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MainColor
        ),
        trailingIcon = {
            Box(
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape)
                    .border(.5.dp, MainColor, CircleShape)
                    .clickable {
                        isDatePickerShow = !isDatePickerShow
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit, "",
                    tint = MainColor,
                    modifier = Modifier
                        .size(17.dp)
                )
            }
        }
    )
    if (isDatePickerShow) {
        val datePicker = android.app.DatePickerDialog(LocalContext.current)
        datePicker.setOnDateSetListener { datePicker, year, month, day ->
            var monthUp = month.toString()
            var dayUp = day.toString()

            if(month < 10)
                monthUp = "0${month+1}"
            if (day < 10)
                dayUp = "0$day"
            date = "$dayUp-$monthUp-$year"
            isDatePickerShow = !isDatePickerShow
            onDateSelect(year.toString(),monthUp,dayUp)
        }
        datePicker.show()
    }
}

@Composable
fun BillReceiptItem(bill: BillItemCollectionWithBillItems, customerItem: CustomerItem) {
    Card(
        modifier = Modifier.fillMaxWidth(.97f),
        border = BorderStroke(.5.dp, Color.Gray),
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
                        customerItem.name,
                        fontSize = 12.sp
                    )
                    Text(
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
                        bill.itemCollection.bill_no,
                        fontSize = 12.sp
                    )
                    Text(
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
                    .border(.7.dp, Color.Black)
                    .fillMaxWidth(.97f)
            ) {
                Text(
                    Utils.convertLongToDate(bill.itemCollection.creation_date, "dd-MMMM-yyyy"),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(10.dp)
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(.5.dp, Color.Black)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            .border(.5.dp, Color.Black)
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
                        .border(.5.dp, Color.Black)
                )
                bill.itemList.forEachIndexed() { index, billItem ->
                    BillItemWithAmountOnBill(index = index + 1, data = billItem)
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(.5.dp, Color.Black)
                    )
                }
                val totalAmount = bill.itemList.sumOf {
                    it.item_amount
                }
                BillItemWithAmountOnBillBelow(
                    data = "Sub Total",
                    amount = (((totalAmount * bill.itemCollection.tax) / 100) + bill.itemCollection.discount + totalAmount).toString()
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(.5.dp, Color.Black)
                )
                BillItemWithAmountOnBillBelow(
                    data = "Tax",
                    amount = ((totalAmount * bill.itemCollection.tax) / 100).toString() + "*${bill.itemCollection.tax.roundToInt()}%"
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(.5.dp, Color.Black)
                )
                BillItemWithAmountOnBillBelow(
                    data = "Discount",
                    amount = bill.itemCollection.discount.toString()
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(.5.dp, Color.Black)
                )
                BillItemWithAmountOnBillBelow(
                    data = "Total Amount",
                    amount = totalAmount.toString()
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(.5.dp, Color.Black)
                )
                Text(
                    "Payment mode : " + if (bill.itemCollection.bill_pay_mode.trim() == "Paid by Cash") "Cash" else "Online",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 5.dp)
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(.5.dp, Color.Black)
                )
                Text(
                    "*" + bill.itemCollection.remarks,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 5.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun BillDetails(list: List<BillItemCollectionWithBillItems>, customerList: List<CustomerItem>) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(list) { bill ->
            BillReceiptItem(
                bill = bill,
                customerItem = customerList.filter { it.id == bill.itemCollection.customerid }[0]
            )
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
                .border(.5.dp, Color.Black)
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
                .border(.5.dp, Color.Black)
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