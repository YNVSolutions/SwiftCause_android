package com.swiftcause.swiftcause_android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swiftcause.swiftcause_android.ui.screen.campaign_details.CampaignDetailsScreen
import com.swiftcause.swiftcause_android.ui.screen.login.LoginScreen
import com.swiftcause.swiftcause_android.ui.screen.campaign_list.CampaignListScreen
import com.swiftcause.swiftcause_android.ui.screen.dummy_payment.CheckOutScreen
import com.swiftcause.swiftcause_android.ui.screen.payment_options.PaymentOptionsScreen
import com.swiftcause.swiftcause_android.ui.screen.thankYou.ThankYouScreen


@Composable
fun NavController() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.loginScreen,
        builder = {

            composable(Routes.loginScreen) {
                LoginScreen(navController = navController)
            }

            composable(Routes.campaignListScreen) {
                CampaignListScreen(
                    navController,
                    onLogoutRedirect = {
                        navController.navigate(Routes.loginScreen) {
                            popUpTo(Routes.loginScreen) {
                                inclusive = true
                            }
                        }
                    })
            }

            composable(Routes.campaignDetailsScreen + "/{campId}") {
                val campId = it.arguments?.getString("campId")
                CampaignDetailsScreen(navController, campId ?: "null")
            }

            composable(Routes.paymentOptionsScreen + "/{campId}") {
                val campId = it.arguments?.getString("campId")
                PaymentOptionsScreen(navController, campId ?: "null")

            }

            composable(Routes.dummyPaymentScreen + "/{campId}/{amount}"){
                val campId = it.arguments?.getString("campId")
                val amount = it.arguments?.getString("amount")
                CheckOutScreen(campId?:"null", amount?:"default_amt", navController)
            }

            composable(Routes.thankYouScreen) {
                ThankYouScreen(navController)
            }
        }
    )
}