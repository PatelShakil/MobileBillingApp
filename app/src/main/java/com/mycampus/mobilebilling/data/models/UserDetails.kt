package com.mycampus.mobilebilling.data.models

data class UserDetails(
    var name: String = "",
    var email: String = "",
    var address: String = "",
    var mobile: String = "",
    var GST: String = "",
    var dynamicLink: String = "",
    var promotionMessageENG : String = "",
    var promotionMessageHND : String = "",
    var isDynamicLinkEnabled : Boolean = false,
    var website: String = ""
)
