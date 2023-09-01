package com.mycampus.billingapp.ui.backuprestore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun BackupRestoreScreen(viewModel: BackupRestoreViewModel) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(20.dp))
        TransactionDetailCard(
            title = "Income", list = listOf(
                LedgerGraph("Pencil vendor - Salim", 1250.0),
                LedgerGraph("Pencil vendor - Salim", 1250.0),
                LedgerGraph("Pencil vendor - Salim", 1250.0),
                LedgerGraph("Pencil vendor - Salim", 1250.0),
            )
        )
    }

}

val INR = "₹"
data class LedgerGraph(
    val category: String,
    val amount: Double
)
@Composable
fun TransSampleItem(data: LedgerGraph) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            data.category,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(.7f)
        )
        Divider(
            Modifier
                .height(28.dp)
                .width(1.dp)
                .background(Color.Gray)
                .border(.5.dp, Gray))
        Text(
            INR + " " + data.amount.toString(),
            modifier = Modifier.weight(.3f).padding(end =5.dp),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Right
        )
    }
}



@Composable
fun TransactionDetailCard(title: String, list: List<LedgerGraph>) {
    Card(
        modifier = Modifier
            .fillMaxWidth(.95f),
        elevation = CardDefaults.cardElevation(30.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(10.dp)
                .border(.5.dp, Color.Gray, RectangleShape),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title + " Group",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().background(Color(0xFFEAEAEA)),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight(700)
            )

            Divider(modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .height(1.dp))
            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFEAEAEA)), verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    "Category Name",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(.7f),
                    fontWeight = FontWeight(600)
                )
                Divider(
                    Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(Color.Gray)
                        .border(.5.dp, Gray))
                Text(
                    "Amount",
                    modifier = Modifier.weight(.3f).padding(end =5.dp),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Right,
                    fontWeight = FontWeight(600)
                )
            }
            Divider(modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .height(1.dp))
            list.forEach {
                TransSampleItem(data = it)
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
                    .height(1.dp))
            }
        }
    }
}