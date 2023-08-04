package com.mycampus.billingapp.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mycampus.billingapp.R
import com.mycampus.billingapp.common.Utils
import com.mycampus.billingapp.data.room.entities.BillItemCollectionWithBillItems
import com.mycampus.billingapp.ui.home.MainColor
import com.mycampus.billingapp.ui.home.UserViewModel


@Composable
fun BillDetailScreen(viewModel: UserViewModel, navController: NavController) {
    var isFilterClick by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter), "",
                modifier = Modifier.clickable {
                    isFilterClick = !isFilterClick
                },
                tint = MainColor
            )
        }
        var itemCol by remember { mutableStateOf(listOf<BillItemCollectionWithBillItems>()) }
        viewModel.allItemCollections.observeForever {
            itemCol = it
        }
        Spacer(modifier = Modifier.height(5.dp))
        BillDetails(itemCol.filter {
            Utils.convertLongToDate(
                it.itemCollection.creation_date,
                "DDMMYY"
            ) == Utils.convertLongToDate(selectedDate, "DDMMYY")
        })
    }


}

@Composable
fun BillDetails(list: List<BillItemCollectionWithBillItems>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(.5f)) {
            Text(
                "Customer Info: ",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                list[0].itemCollection.customer_name,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                list[0].itemCollection.customer_name,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}
