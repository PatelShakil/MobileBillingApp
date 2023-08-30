package com.mycampus.billingapp.common.uicomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mycampus.billingapp.ui.home.LightMainColor
import com.mycampus.billingapp.ui.home.MainColor


@Composable
fun GetAmount(amount:Int,isSubmitted: MutableState<Boolean>,onAmountSet: (Int) -> Unit,onSubmit:()->Unit) {
    var value by remember { mutableStateOf(amount) }
    if(amount == 0 && isSubmitted.value){
        value = 0
        onSubmit()
    }
    TextField(
        value = if (value == 0) "" else value.toString(),
        onValueChange = {
            if (it.isEmpty()) {
                value = 0
                onAmountSet(value)
            } else {
                value = it.toInt()
                onAmountSet(value)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier,
        label = { Text("Amount", style = MaterialTheme.typography.bodySmall) },
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
    )
}

@Composable
fun SampleTextFieldDouble(
    modifier: Modifier = Modifier,
    label: String,
    text: Int,
    isSubmitted : MutableState<Boolean>,
            onAmountSet: (Int) -> Unit,
    onSubmit:()->Unit
) {
    var value by remember { mutableStateOf(text) }
    if(text == 0 && isSubmitted.value){
        value = 0
        onSubmit()
    }
    TextField(
        value = if (value == 0) "" else value.toString(),
        modifier = modifier,
        onValueChange = {
            if (it.isEmpty()) {
                value = 0
                onAmountSet(value)
            } else {
                value = it.toInt()
                onAmountSet(value)
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
    )
}
@Composable
fun SettingsTextFieldSample(
    label: String,
    value: String = "",
    onTextChanged: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    lineCount: Int = 1
) {
    var value by remember { mutableStateOf(value) }
    androidx.compose.material.TextField(
        value = value,
        onValueChange = {
            onTextChanged(it.trim())
            value = it
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, style = MaterialTheme.typography.titleSmall) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            focusedIndicatorColor = MainColor
        ),
        minLines = lineCount,
        singleLine = lineCount <= 1,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
@Composable
fun SearchBar(hint:String,onTextChanged: (String) -> Unit) {
    var value by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightMainColor),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(.95f)
                .padding(vertical = 15.dp),
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
                    if (value.isNotEmpty()) {
                        Icon(
                            Icons.Default.Clear, "",
                            tint = MainColor,
                            modifier = Modifier.clickable {
                                value = ""
                            }
                        )
                    }
                },
                label = {
                    Text(
                        "Search $hint",
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    backgroundColor = Color.White,
                    cursorColor = MainColor
                )
            )
        }
    }

}
