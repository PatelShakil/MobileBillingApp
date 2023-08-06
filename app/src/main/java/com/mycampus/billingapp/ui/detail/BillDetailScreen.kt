package com.mycampus.billingapp.ui.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mycampus.billingapp.R
import com.mycampus.billingapp.common.Utils
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollectionWithBillItems
import com.mycampus.billingapp.ui.home.MainColor
import com.mycampus.billingapp.ui.home.UserViewModel


@Composable
fun BillDetailScreen(viewModel: UserViewModel, navController: NavController) {
    var isFilterClick by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(.95f),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter), "",
                modifier = Modifier.clickable {
                    isFilterClick = !isFilterClick
                }.size(35.dp),
                tint = MainColor,
            )
        }
        var itemCol by remember { mutableStateOf(listOf<BillItemCollectionWithBillItems>()) }
        viewModel.allItemCollections.observeForever {
            itemCol = it
        }
        Spacer(modifier = Modifier.height(5.dp))
        if (itemCol.isNotEmpty()) {
            BillDetails(itemCol.filter {
                Utils.convertLongToDate(
                    it.itemCollection.creation_date,
                    "DDMMYY"
                ) == Utils.convertLongToDate(selectedDate, "DDMMYY")
            })
        }
    }


}

@Composable
fun BillDetails(list: List<BillItemCollectionWithBillItems>) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(.97f),
        ) {
            Column(modifier = Modifier.weight(.5f)) {
                Text(
                    "Customer Info: ",
                    fontSize = 14.sp
                )
                Text(
                    list[0].itemCollection.customer_name,
                    fontSize = 12.sp
                )
                Text(
                    list[0].itemCollection.mobile,
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
                    list[0].itemCollection.bill_no,
                    fontSize = 12.sp
                )
                Text(
                    list[0].itemCollection.created_by,
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
                text = list[0].itemCollection.customer_address,
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
                Utils.convertLongToDate(list[0].itemCollection.creation_date, "dd-MMMM-yyyy"),
                fontSize = 12.sp,
                modifier = Modifier.padding(10.dp)
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(.5.dp, Color.Black)
            )
            Row(modifier = Modifier.fillMaxWidth()) {
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
                    modifier = Modifier.weight(.4f),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400)
                )
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(.5.dp, Color.Black)
            )
            list[0].itemList.forEachIndexed() { index, billItem ->
                BillItemWithAmountOnBill(index = index + 1, data = billItem)
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(.5.dp, Color.Black)
                )
            }
            val totalAmount =
                list.filter {
                    Utils.convertLongToDate(
                        it.itemCollection.creation_date,
                        "DDMMYY"
                    ) == Utils.convertLongToDate(System.currentTimeMillis(), "DDMMYY")
                }.sumOf {
                    it.itemCollection.total_amount
                }
            BillItemWithAmountOnBillBelow(
                data = "Amount",
                amount = ((totalAmount * list[0].itemCollection.tax) / 100) - list[0].itemCollection.discount + totalAmount
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(.5.dp, Color.Black)
            )
            BillItemWithAmountOnBillBelow(data = "Tax", amount = list[0].itemCollection.tax)
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(.5.dp, Color.Black)
            )
            BillItemWithAmountOnBillBelow(
                data = "Discount",
                amount = list[0].itemCollection.discount
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(.5.dp, Color.Black)
            )
            BillItemWithAmountOnBillBelow(data = "Total Payable Amount", amount = totalAmount)
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(.5.dp, Color.Black)
            )
            Text("Payment mode : " + if(list[0].itemCollection.bill_pay_mode == "Pay by Cash") "Cash" else "Online",
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 5.dp))
            Divider(modifier = Modifier
                .fillMaxWidth()
                .border(.5.dp, Color.Black))
            Text(list[0].itemCollection.remarks,
            fontSize = 12.sp,
                modifier = Modifier.padding(start = 5.dp),
            style = MaterialTheme.typography.bodySmall)
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
            modifier = Modifier.weight(.4f),
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}

@Composable
fun BillItemWithAmountOnBillBelow(data: String, amount: Double) {
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
            modifier = Modifier.weight(.4f),
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}