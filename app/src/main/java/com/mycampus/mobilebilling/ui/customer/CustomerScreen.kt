package com.mycampus.mobilebilling.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shakilpatel.mobilebilling.R
import com.mycampus.mobilebilling.common.MainColor
import com.mycampus.mobilebilling.common.uicomponents.AddCustomerPopupScreen
import com.mycampus.mobilebilling.common.uicomponents.ErrorMessage
import com.mycampus.mobilebilling.common.uicomponents.SearchBar
import com.mycampus.mobilebilling.data.room.entities.CustomerItem

@Composable
fun CustomerScreen(viewModel: CustomerViewModel, navController: NavController) {
    var isAddCustomer by remember { mutableStateOf(false) }
    var customerCol by remember { mutableStateOf(listOf<CustomerItem>()) }
    var customerColOg by remember { mutableStateOf(listOf<CustomerItem>()) }
    viewModel.allCustomers.observeForever {
        customerColOg = it
        customerCol = customerColOg
    }
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar("Customer",onTextChanged = { value ->
                customerCol = if (value.isNotEmpty())
                    customerColOg.filter { it.name.lowercase().contains(value.lowercase()) }
                else
                    customerColOg
            })
            if (customerCol.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                    items(customerCol) {
                        CustomerItemSample(customer = it)
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            } else {
                if (customerColOg.isNotEmpty())
                    ErrorMessage(msg = "No Corresponding record found...")
                else
                    ErrorMessage(msg = "No Data found...")
            }
        }
        FloatingActionButton(
            onClick = {
                isAddCustomer = !isAddCustomer
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(30.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add, "",
                tint = MainColor
            )
        }
    }
    if (isAddCustomer) {
        AddCustomerPopupScreen(customer = CustomerItem(0, "", "", "", ""), onDismiss = {
            isAddCustomer = !isAddCustomer
        }, onConfirm = {
            viewModel.addCustomer(it)
        })
    }
}


@Composable
fun CustomerItemSample(customer: CustomerItem) {
    Card(
        modifier = Modifier.fillMaxWidth(.95f),
        border = BorderStroke(.5.dp, Color.Gray),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 5.dp)
                .padding(start = 5.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Column(modifier = Modifier.weight(.7f)) {

                    Text(
                        text = customer.name,
                        modifier = Modifier.padding(start = 5.dp),
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Phone No. : " + customer.mobile,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                    Text(
                        text = "Email : " + customer.email,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("Address: " + customer.address)
                        },
                        style = TextStyle(
                            fontSize = 13.sp,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 5.dp)
                            .background(Color.White), // Just for visualization, to see the boundaries of the Text composable
                        softWrap = true,
                        maxLines = Int.MAX_VALUE // Allow unlimited lines
                    )
                }
                Image(
                    painterResource(id = R.drawable.customer_service), contentDescription = "",
                    modifier = Modifier.size(70.dp).padding(10.dp),
                    colorFilter = ColorFilter.tint(MainColor)
                )
            }
        }
    }

}


@Composable
fun AddCustomerTextField(
    label: String,
    onTextChanged: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var tv by remember { mutableStateOf("") }
    androidx.compose.material.OutlinedTextField(
        value = tv, onValueChange = {
            tv = it
            onTextChanged(it)
        },
        label = { androidx.compose.material.Text(label, color = MainColor) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MainColor
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        )
    )
}