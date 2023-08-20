package com.mycampus.billingapp.ui.backuprestore

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.CustomerItem
import com.mycampus.billingapp.ui.home.MainColor


@Composable
fun BackupRestoreScreen(viewModel: BackupRestoreViewModel) {
    val context = LocalContext.current
    var billitemsCol: List<BillItemCollection>? = null
    viewModel.billitemsCol.observeForever {
        billitemsCol = it
    }
    var billitems : List<BillItem> ? = null
    viewModel.billitems.observeForever {
        billitems = it
    }
    var customers : List<CustomerItem> ? = null
    viewModel.customers.observeForever {
        customers = it
    }
    Column(
        modifier = Modifier.fillMaxSize(.95f).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            viewModel.backupDatabase(billitemsCol!!,billitems!!,customers!!,context)
            // Show a message that backup was successful
        },
            modifier = Modifier.fillMaxWidth(.8f),
            colors= ButtonDefaults.buttonColors(
                MainColor
            )) {
            Text("Backup Database")
        }

        Button(onClick = {
            viewModel.restoreDatabase(context)
        // Show a message that restore was successful
        },
            modifier = Modifier.fillMaxWidth(.8f),
            colors= ButtonDefaults.buttonColors(
                MainColor
            )) {
            Text("Restore Database")
        }
    }
}

private fun validFile(context: Context,fileUri: Uri): Boolean {
    val cr: ContentResolver = context.contentResolver
    val mime = cr.getType(fileUri)
    return "application/octet-stream" == mime
}
class GetContentContract : ActivityResultContract<String, Uri?>() {
    override fun createIntent(context: Context, input: String): Intent {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
        return i.apply {
            type = input
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == Activity.RESULT_OK && intent != null) intent.data else null
    }
}

@Composable
fun rememberGetContentContractLauncher(
    onResult: (Uri?) -> Unit
): ManagedActivityResultLauncher<String, Uri?> {
    val getContentContract = remember { GetContentContract() }
    return rememberLauncherForActivityResult(contract = getContentContract) { result ->
        onResult(result)
    }
}