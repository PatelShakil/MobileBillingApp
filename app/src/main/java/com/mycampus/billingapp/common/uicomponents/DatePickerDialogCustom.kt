package com.mycampus.billingapp.common.uicomponents

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mycampus.billingapp.R
import com.mycampus.billingapp.ui.home.MainColor

@Composable
fun DatePickerDialogCustom(
    date: String,
    label: String,
    onDateSelect: (String, String, String) -> Unit
) {
    var date by remember {
        mutableStateOf(date)
    }
    var isDatePickerShow by remember { mutableStateOf(false) }
    androidx.compose.material.OutlinedTextField(value = date, onValueChange = {},
        leadingIcon = {
            Icon(
                painterResource(id = R.drawable.ic_calendar), contentDescription = "",
                tint = MainColor
            )
        },
        label = { androidx.compose.material.Text("Date", color = MainColor) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MainColor
        ),
        trailingIcon = {
            Box(
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape)
                    .border(.5.dp, MainColor, CircleShape)
                    .clickable {
                        isDatePickerShow = !isDatePickerShow
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit, "",
                    tint = MainColor,
                    modifier = Modifier
                        .size(17.dp)
                )
            }
        }
    )
    if (isDatePickerShow) {
        val datePicker = android.app.DatePickerDialog(LocalContext.current)
        datePicker.setOnDateSetListener { datePicker, year, month, day ->
            var monthUp = month.toString()
            var dayUp = day.toString()

            if (month < 10)
                monthUp = "0${month + 1}"
            if (day < 10)
                dayUp = "0$day"
            date = "$dayUp-$monthUp-$year"
            isDatePickerShow = !isDatePickerShow
            onDateSelect(year.toString(), monthUp, dayUp)
        }
        datePicker.show()
    }
}