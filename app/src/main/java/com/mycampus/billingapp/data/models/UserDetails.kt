package com.mycampus.billingapp.data.models

data class UserDetails(
    var name: String = "",
    var email: String = "",
    var address: String = "",
    var mobile: String = "",
    var GST: String = "",
    var promotionMessage : String = "",
    var isDynamicLinkEnabled : Boolean = false,
    var website: String = ""
)
