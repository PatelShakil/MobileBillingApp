package com.mycampus.billingapp.ui.detail

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.View
import android.widget.ScrollView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.mycampus.billingapp.R
import com.mycampus.billingapp.common.Utils
import com.mycampus.billingapp.common.uicomponents.DatePickerDialogCustom
import com.mycampus.billingapp.common.uicomponents.ErrorMessage
import com.mycampus.billingapp.common.uicomponents.ProgressBarCus
import com.mycampus.billingapp.data.models.BillItemCollectionPrint
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollectionWithBillItems
import com.mycampus.billingapp.data.room.entities.CustomerItem
import com.mycampus.billingapp.ui.customer.CustomerViewModel
import com.mycampus.billingapp.ui.home.MainColor
import com.mycampus.billingapp.ui.home.UserViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt


@Composable
fun BillDetailScreen(
    viewModel: UserViewModel,
    customerViewModel: CustomerViewModel,
    navController: NavController
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
                customerCol
            ){
                viewModel.deleteBillItemCol(it)
            }
        } else {
            ErrorMessage(msg = "No Corresponding record found...")
        }
        val deleteProgress = viewModel.deleteProcess.collectAsState()
        if(deleteProgress.value){
            ProgressBarCus {
                //onDismiss
            }
        }
    }
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
                            it.itemCollection.creation_date,
                            "dd-MM-yyyy"
                        ) == selectedDate &&
                                it.itemCollection.bill_pay_mode != "Paid by Cash"
                    }
                }
                if (isCash) {
                    itemCol = itemColOg.filter {
                        Utils.convertLongToDate(
                            it.itemCollection.creation_date,
                            "dd-MM-yyyy"
                        ) == selectedDate &&
                                it.itemCollection.bill_pay_mode == "Paid by Cash"
                    }
                }
                if (!isCash && !isOnline || (isCash && isOnline)) {
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
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BillReceiptItem(isPrint : Boolean = true,bill: BillItemCollectionWithBillItems, customerItem: CustomerItem,onBillDelete:(BillItemCollectionWithBillItems)->Unit,onPdfClick: () -> Unit) {
    Card(
        modifier = Modifier
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
                        bill.itemCollection.creation_date,
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
                        .border(1.dp, Color.Gray)
                )
                BillItemWithAmountOnBillBelow(
                    data = "Tax",
                    amount = ((totalAmount * bill.itemCollection.tax) / 100).toString() + " ( @ ${bill.itemCollection.tax.roundToInt()}% )"
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
                    modifier = Modifier.padding(start = 5.dp)
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                )
                Text(
                    "*" + bill.itemCollection.remarks,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 5.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            if(isPrint){
                BillReceiptButtonRow(
                    bill = bill,
                    customerItem = customerItem,
                    onBillDelete =onBillDelete,
                    onPdfClick = {

                    },
                    onPrintClick = {}
                )
            }
        }
    }
}

@Composable
fun BillReceiptButtonRow(bill : BillItemCollectionWithBillItems,customerItem : CustomerItem,onBillDelete : (BillItemCollectionWithBillItems)->Unit,onPdfClick:()->Unit,onPrintClick:(BillItemCollectionPrint)->Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

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
fun BillDetails(list: List<BillItemCollectionWithBillItems>, customerList: List<CustomerItem>,onBillDelete:(BillItemCollectionWithBillItems)->Unit) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(list) { bill ->
            var isBit by remember{ mutableStateOf(false) }

            val context = LocalContext.current
            val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

// Create the ComposeView and measure/layout it within the Compose context
            ComposeView(context).apply {
                setContent {
                    BillReceiptItem(
                        bill = bill,
                        customerItem = customerList.filter { it.id == bill.itemCollection.customerid }[0], onBillDelete = {}
                    ) {
                        // Content of the ComposeView
                    }
                }

                // Measure and layout the ComposeView
                measure(
                    View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY)
                )
                layout(0, 0, measuredWidth, measuredHeight)

                // Draw the ComposeView onto the canvas
                draw(canvas)

                // Show the bitmap in a Dialog
                Dialog(onDismissRequest = { /*TODO*/ }) {
                    Image(bitmap = bitmap.asImageBitmap(), contentDescription = "")
                }
            }


//            BillReceiptItem(
//                bill = bill,
//                customerItem = customerList.filter { it.id == bill.itemCollection.customerid }[0]
//                , onBillDelete = {
//                    onBillDelete(it)
//                }){
//            }
//            ScrollableCapturable(controller = rememberCaptureController(), onCaptured = {bitmap, throwable ->
//                    if (bitmap != null) {
//                        bit.value = bitmap
//                        isBit = true
////                        saveFile(customerList.filter { it.id == bill.itemCollection.customerid }[0].name + bill.itemCollection.bill_no + ".pdf" ,bitmap)
//                    }
//            } ) {
//                BillReceiptItem(
//                bill = bill,
//                customerItem = customerList.filter { it.id == bill.itemCollection.customerid }[0]
//                , onBillDelete = {
//                    onBillDelete(it)
//                }){
//
//                }
////                BillReceiptItem(
////                    bill = bill,
////                    customerItem = customerList.filter { it.id == bill.itemCollection.customerid }[0]
////                    , onBillDelete = {
////                    }){
////                }
//            }

            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }


}
fun saveBitmapToFile(bitmap: Bitmap, file: File) {
    try {
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
fun saveFile(filename: String,bitmap : Bitmap) {
    val storageDir = File("/storage/emulated/0/Download/myCampus")
    val file = File(filename)
    if(storageDir.exists())
        storageDir.mkdirs()
    if(file.exists())
        file.createNewFile()
    saveBitmapToFile(bitmap,file)
}

//        implementation "dev.shreyaspatil:capturable:1.0.3"

fun getPermissions():Array<String>{
    return if(Build.VERSION.SDK_INT >= 33){
        arrayOf(Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.READ_MEDIA_VIDEO)
    }else{
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}
@Composable
fun ScrollableCapturable(
    modifier: Modifier = Modifier,
    controller: CaptureController,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
    content: @Composable () -> Unit
) {
    AndroidView(
        factory = { context ->
            val scrollView = ScrollView(context)
            val composeView = ComposeView(context).apply {
                setContent {
                    content()
                }
            }
            scrollView.addView(composeView)
            scrollView
        },
        update = { scrollView ->
            if (controller.readyForCapture) {
                // Hide scrollbars for capture
                scrollView.isVerticalScrollBarEnabled = false
                scrollView.isHorizontalScrollBarEnabled = false
                try {
                    val bitmap = loadBitmapFromScrollView(scrollView)
                    onCaptured(bitmap, null)
                } catch (throwable: Throwable) {
                    onCaptured(null, throwable)
                }
                scrollView.isVerticalScrollBarEnabled = true
                scrollView.isHorizontalScrollBarEnabled = true
                controller.captured()
            }
        },
        modifier = modifier
    )
}

/**
 * Need to use view.getChildAt(0).height instead of just view.height,
 * so you can get all ScrollView content.
 */
private fun loadBitmapFromScrollView(scrollView: ScrollView): Bitmap {
    val bitmap = Bitmap.createBitmap(
        scrollView.width,
        scrollView.getChildAt(0).height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    scrollView.draw(canvas)
    return bitmap
}

class CaptureController {
    var readyForCapture by mutableStateOf(false)
        private set

    fun capture() {
        readyForCapture = true
    }

    internal fun captured() {
        readyForCapture = false
    }
}

@Composable
fun rememberCaptureController(): CaptureController {
    return remember { CaptureController() }
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