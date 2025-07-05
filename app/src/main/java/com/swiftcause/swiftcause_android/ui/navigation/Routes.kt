package com.swiftcause.swiftcause_android.ui.navigation

object Routes {
    val campaignListScreen = "campaign_list_screen"
    val loginScreen = "login_screen"
    val campaignDetailsScreen = "campaign_details_screen"
    val paymentOptionsScreen = "payment_options_screen"
    val checkOutScreenBase = "checkout_screen"
    val checkOutScreen = "$checkOutScreenBase/{campId}/{amount}"
    val thankYouScreen = "thank_you_screen"
}