package com.mycampus.billingapp.common.uicomponents


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mycampus.billingapp.ui.home.MainColor


data class DropDownItemData(
    val id:String="",
    val name:String=""
)

@Composable
fun CusDropdown(label:String,options : List<DropDownItemData>,onSelected:(DropDownItemData) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(DropDownItemData()) }
    Column {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MainColor,
                backgroundColor = Color.Transparent
            ),
            value = TextFieldValue(selected.name),
            onValueChange = {},
            readOnly = true,
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            trailingIcon = {
                IconButton(
                    onClick = { expanded = true },
                ) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 16.sp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selected = option
                        expanded = false
                        onSelected(selected)
                    }
                ) {
                    Text(option.name)
                }
            }
        }
    }

}