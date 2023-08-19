package com.mycampus.billingapp.ui.backuprestore

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mycampus.billingapp.common.Utils.Companion.BACKUP_DIR
import com.mycampus.billingapp.common.Utils.Companion.convertLongToDate
import java.io.File


@Composable
fun BackupRestoreScreen(viewModel: BackupRestoreViewModel) {
    val backupFileName by remember { mutableStateOf("billingapp_database_${convertLongToDate(System.currentTimeMillis(),"dd_MM_yyyy")}") }
    val backupDir = File(BACKUP_DIR)
    if(!backupDir.exists())
        backupDir.mkdirs()
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val backupFile = File( "$BACKUP_DIR/$backupFileName.db")
            if(!backupFile.exists())
                backupFile.createNewFile()

            viewModel.backupDatabase(context,backupFile)
            // Show a message that backup was successful
        }) {
            Text("Backup Database")
        }

        val getContent = rememberGetContentContractLauncher(onResult = {
            if(it != null){
                if(validFile(context,it)){
                    viewModel.restoreDatabaseFromUri(context,it)
                }else{
                    Toast.makeText(context,"Choose Valid Backup File", Toast.LENGTH_SHORT).show()
                }
            }
        })
        Button(onClick = {
//            getContent.launch("*/*")
            viewModel.restoreDatabase(context,File("$BACKUP_DIR/billingapp_database_20_08_2023.db"))
            // Show a message that restore was successful
        }) {
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