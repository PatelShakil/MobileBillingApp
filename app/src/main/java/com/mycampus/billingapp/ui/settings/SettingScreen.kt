package com.mycampus.billingapp.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mycampus.billingapp.common.MainColor
import com.mycampus.billingapp.common.uicomponents.CusDropdown
import com.mycampus.billingapp.common.uicomponents.DropDownItemData
import com.mycampus.billingapp.common.uicomponents.SettingsTextFieldSample
import com.mycampus.billingapp.data.models.UserDetails
import com.mycampus.billingapp.ui.home.UserViewModel

@Composable
fun SettingMainScreen(viewModel : UserViewModel,navController: NavController) {
    var userDetails by remember{ mutableStateOf(UserDetails()) }
    viewModel.userDetails.observeForever {
        if(it != null)
            userDetails = it
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            elevation = CardDefaults.cardElevation(18.dp),
            border = BorderStroke(.5.dp, Gray),
            shape = RoundedCornerShape(15.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Settings, "",
                        tint = MainColor
                    )
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                    )
                }
                Spacer(Modifier.height(5.dp))
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(.5.dp), color = Color.Gray
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column {
                        SettingsTextFieldSample(
                            label = "Name",
                            value = userDetails.name,
                            onTextChanged = {
                                userDetails.name = it
                            })
                        SettingsTextFieldSample(
                            label = "Email",
                            value = userDetails.email,
                            onTextChanged = {
                                userDetails.email = it
                            },
                            KeyboardType.Email
                        )
                        SettingsTextFieldSample(
                            label = "Address",
                            value = userDetails.address,
                            onTextChanged = {
                                userDetails.address = it
                            },
                            lineCount = 3
                        )
                        SettingsTextFieldSample(
                            label = "Mobile",
                            value = userDetails.mobile,
                            onTextChanged = {
                                userDetails.mobile = it
                            },
                            KeyboardType.Phone
                        )
                        SettingsTextFieldSample(
                            label = "GST No.",
                            value = userDetails.GST,
                            onTextChanged = {
                                userDetails.GST = it
                            }
                        )

                        SettingsTextFieldSample(
                            label = "Website",
                            value = userDetails.website,
                            onTextChanged = {
                                userDetails.website = it
                            })

                        CusDropdown(label = "Dynamic Link", selected = DropDownItemData(
                            if (userDetails.isDynamicLinkEnabled) "Enabled" else "Disabled",
                            if (userDetails.isDynamicLinkEnabled) "Enabled" else "Disabled"
                        ), options = listOf(
                            DropDownItemData("Enabled", "Enabled"),
                            DropDownItemData("Disabled", "Disabled")
                        ), onSelected = {
                            userDetails.isDynamicLinkEnabled = it.id == "Enabled"
                        })

                        SettingsTextFieldSample(
                            label = "Dynamic Link",
                            value = userDetails.dynamicLink,
                            onTextChanged = {
                                userDetails.dynamicLink = it
                            },
                        )
                        SettingsTextFieldSample(
                            label = "Promotion Message(English)",
                            value = userDetails.promotionMessageENG,
                            onTextChanged = {
                                userDetails.promotionMessageENG = it
                            },
                            lineCount = 3
                        )
                        SettingsTextFieldSample(
                            label = "Promotion Message(Hindi)",
                            value = userDetails.promotionMessageHND,
                            onTextChanged = {
                                userDetails.promotionMessageHND = it
                            },
                            lineCount = 3
                        )
                    }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        viewModel.saveUserDetails(userDetails)
                        navController.popBackStack()
                    }) {
                        Text(if (userDetails == UserDetails()) "Add" else "Save")
                    }
                }
                Spacer(Modifier.height(10.dp))


            }

        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}