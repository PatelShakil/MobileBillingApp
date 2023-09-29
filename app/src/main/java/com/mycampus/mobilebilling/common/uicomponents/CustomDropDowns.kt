package com.mycampus.mobilebilling.common.uicomponents


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mycampus.mobilebilling.common.MainColor


data class DropDownItemData(
    val id:String="",
    val name:String=""
)

@Composable
fun CusDropdown(label:String,selected:DropDownItemData,options : List<DropDownItemData>,onSelected:(DropDownItemData) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(selected) }
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
            label = { Text(label, style = MaterialTheme.typography.titleSmall) },
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

@Composable
fun CusDropdownSearchOld(
    label: String,
    options: List<DropDownItemData>,
    onSelected: (DropDownItemData) -> Unit,
    onCreate:()->Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(DropDownItemData()) }
    var searchText by remember { mutableStateOf("") }

    val filteredOptions = options.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Column {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MainColor,
                backgroundColor = Color.Transparent
            ),
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            trailingIcon = {
                IconButton(
                    onClick = { expanded = true },
                ) {
                    if(expanded)
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = null)
                    else
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                }
            },
            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 16.sp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if(filteredOptions.isNotEmpty()) {
                filteredOptions.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            selected = option
                            searchText = selected.name
                            expanded = false
                            onSelected(selected)
                        }
                    ) {
                        Text(option.name)
                    }
                }
            }else{
                DropdownMenuItem(onClick = onCreate) {
                    Text("Create Customer")
                }

            }
        }
    }
}
@Composable
fun CusDropdownSearch(
    selectedItem : DropDownItemData,
    isSubmit:Boolean,
    label: String,
    options: List<DropDownItemData>,
    onSelected: (DropDownItemData) -> Unit,
    onCreate: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<DropDownItemData?>(selectedItem) }

    if(selectedItem == DropDownItemData() && isSubmit){
        selected = DropDownItemData()
        searchText = ""
    }

    val filteredOptions = options.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Column() {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MainColor,
                backgroundColor = Color.Transparent
            ),
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                selected = null // Clear the selected item
            },
            leadingIcon = {
                          Icon(Icons.Default.Search,"")
            },
            trailingIcon = {
                androidx.compose.material3.Icon(imageVector = Icons.Default.Add,
                    contentDescription = "",
                    modifier = Modifier
                        .height(30.dp)
                        .width(30.dp)
                        .clip(CircleShape)
                        .border(.5.dp, Color.Gray, CircleShape)
                        .background(Color.White)
                        .clickable {
                            onCreate()
                        })
            },
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 16.sp),
        )

        // Show suggestions below the text field
        if (searchText.isNotEmpty() && filteredOptions.isNotEmpty() && selected == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)) {
                    filteredOptions.forEachIndexed { index, option ->
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selected = option
                                    searchText =
                                        option.name // Update search text with selected option
                                    // You can call onSelected(option) here if needed
                                    onSelected(selected!!)
                                }
                                .padding(16.dp),
                            text = option.name,
                            style = TextStyle(fontSize = 16.sp)
                        )
                        if(index < filteredOptions.size)
                            Divider(modifier = Modifier.fillMaxWidth().height(.5.dp).background(Color.Gray))
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .clickable {
                                onCreate()
                            }
                            .padding(16.dp),
                        text = "Create Customer",
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
            }
        }
        if(filteredOptions.isEmpty() && searchText.isNotEmpty()){
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .clickable {
                                    onCreate()
                                }
                                .padding(16.dp),
                            text = "Create Customer",
                            style = TextStyle(fontSize = 16.sp)
                        )
            }
        }
    }
}





