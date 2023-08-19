package com.mycampus.billingapp.ui.customer

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.mycampus.billingapp.R
import com.mycampus.billingapp.common.uicomponents.ErrorMessage
import com.mycampus.billingapp.data.room.entities.CustomerItem
import com.mycampus.billingapp.ui.home.LightMainColor
import com.mycampus.billingapp.ui.home.MainColor

@Composable
fun CustomerScreen(viewModel : CustomerViewModel,navController: NavController) {
    var isAddCustomer by remember{ mutableStateOf(false) }
    var customerCol by remember{ mutableStateOf(listOf<CustomerItem>()) }
    var customerColOg by remember{ mutableStateOf(listOf<CustomerItem>()) }
    viewModel.allCustomers.observeForever {
        customerColOg = it
        customerCol = customerColOg
    }
    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            SearchBar(onTextChanged = {value ->
                customerCol = if(value.isNotEmpty())
                    customerColOg.filter { it.name.lowercase().contains(value.lowercase()) }
                else
                    customerColOg
            })
            if(customerCol.isNotEmpty()) {
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
            }else{
                if(customerColOg.isNotEmpty())
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
    if(isAddCustomer){
        AddCustomerPopupScreen(customer = CustomerItem(0,"","","",""), onDismiss = {
                                                                                   isAddCustomer = !isAddCustomer
        }, onConfirm = {
            viewModel.addCustomer(it)
        })
    }
}

@Composable
fun SearchBar(onTextChanged: (String) -> Unit) {
    var value by remember{ mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth().background(LightMainColor),
    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Card(
            modifier = Modifier.fillMaxWidth(.9f).padding(vertical = 15.dp),
            border = BorderStroke(.5.dp, Color.Gray),
            elevation = CardDefaults.cardElevation(
                18.dp
            )
        ) {
            TextField(
                value = value, onValueChange = {
                    value = it
                    onTextChanged(value)
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search, contentDescription = "",
                        tint = MainColor
                    )
                },
                trailingIcon = {
                    if(value.isNotEmpty()) {
                        Icon(
                            Icons.Default.Clear, "",
                            tint = MainColor,
                            modifier = Modifier.clickable {
                                value = ""
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    backgroundColor = Color.White,
                    cursorColor = MainColor
                )
            )
        }
    }

}
@Composable
fun CustomerItemSample(customer: CustomerItem) {
    Card(modifier = Modifier.fillMaxWidth(.95f),
    border = BorderStroke(.5.dp, Color.Gray),
        elevation = CardDefaults.cardElevation(18.dp)
    ) {
        Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 5.dp)
            .padding(start = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(id = R.drawable.customer_service), contentDescription = "",
            modifier = Modifier.size(40.dp))
            Column() {

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
                )            }

            }
        }
    }

}




@Composable
fun AddCustomerPopupScreen(
    isEdit : Boolean = false,
    customer: CustomerItem,
    onDismiss: () -> Unit,
    onConfirm: (CustomerItem) -> Unit
) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        var name by remember{ mutableStateOf("") }
        var mobile by remember{ mutableStateOf("") }
        var email by remember{ mutableStateOf("") }
        var address by remember{ mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
                .background(Color.Transparent)
                .clickable {
                    onDismiss()
                },
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(modifier = Modifier
                .fillMaxWidth(.95f)
                .clickable { }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(25.dp)
                            .background(Color.White),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier
                                .height(6.dp)
                                .width(50.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MainColor
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "${if(isEdit) "Update" else "Add"} Customer",
                        style = MaterialTheme.typography.titleSmall,
                        color = MainColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(.95f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(5.dp))
                        AddCustomerTextField(label = "Name", onTextChanged = {
                            name = it
                        })
                        Spacer(modifier = Modifier.height(5.dp))
                        AddCustomerTextField(label = "Mobile", onTextChanged = {
                            mobile = it
                        },
                        KeyboardType.Phone)
                        Spacer(modifier = Modifier.height(5.dp))
                        AddCustomerTextField(label = "Email", onTextChanged = {
                            email = it
                        },
                        KeyboardType.Email)
                        Spacer(modifier = Modifier.height(5.dp))
                        AddCustomerTextField(label = "Address", onTextChanged = {
                            address = it
                        })

                        Spacer(modifier = Modifier.height(5.dp))
                        Button(modifier = Modifier
                            .fillMaxWidth(.9f),
                            enabled = name.isNotEmpty() && mobile.isNotEmpty() && email.isNotEmpty() && address.isNotEmpty(),
                            onClick = {
                                val cus = CustomerItem(0,name,mobile,email,address)
                                onConfirm(cus)
                                onDismiss()
                            }) {
                            Text(if(isEdit) "Update" else "Add")
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
@Composable
fun AddCustomerTextField(label : String,onTextChanged:(String)->Unit,keyboardType : KeyboardType = KeyboardType.Text) {
    var tv by remember{ mutableStateOf("") }
    androidx.compose.material.OutlinedTextField(value = tv, onValueChange = {tv = it
        onTextChanged(it)},
        label = {androidx.compose.material.Text(label,color = MainColor)},
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MainColor
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        )
    )
}