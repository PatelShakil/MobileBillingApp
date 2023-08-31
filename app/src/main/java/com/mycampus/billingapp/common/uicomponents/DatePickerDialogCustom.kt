package com.mycampus.billingapp.common.uicomponents

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mycampus.billingapp.R
import com.mycampus.billingapp.common.MainColor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
            var monthUp = (month + 1).toString()
            var dayUp = day.toString()

            if (month < 9)
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
@Composable
fun DateTimePicker(
    label: String,
    hint:String,
    selectedDateTime: MutableState<LocalDateTime>,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var datePickerDialogShown by remember { mutableStateOf(false) }
    var timePickerDialogShown by remember { mutableStateOf(false) }

    val context = LocalContext.current

    TextField(
        value = selectedDateTime.value.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")),
        onValueChange = {},
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
                       Icon(Icons.Default.Edit,"",
                       modifier = Modifier.clickable {
                           datePickerDialogShown = true

                       },
                       tint = MainColor
                       )
        },
        label = {
                Text(label,
                style = MaterialTheme.typography.bodySmall)
        },
        placeholder = {Text(hint,
            style = MaterialTheme.typography.bodySmall)},
        readOnly = true,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = MainColor
        )
    )

    if (datePickerDialogShown) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val updatedDateTime = selectedDateTime.value.withYear(year)
                    .withMonth(month + 1)
                    .withDayOfMonth(dayOfMonth)
                selectedDateTime.value = updatedDateTime
                timePickerDialogShown = true
                datePickerDialogShown = false
            },
            selectedDateTime.value.year,
            selectedDateTime.value.monthValue - 1,
            selectedDateTime.value.dayOfMonth
        ).show()
    }

    if (timePickerDialogShown) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val updatedDateTime = selectedDateTime.value.withHour(hourOfDay)
                    .withMinute(minute)
                selectedDateTime.value = updatedDateTime
                timePickerDialogShown = false
                onDateTimeSelected(updatedDateTime) // Call the callback
            },
            selectedDateTime.value.hour,
            selectedDateTime.value.minute,
            false
        ).show()
    }
}


