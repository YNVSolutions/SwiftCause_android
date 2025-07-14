package com.swiftcause.swiftcause_android.ui.screen.campaign_details


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.swiftcause.swiftcause_android.ui.shared.SharedViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.swiftcause.swiftcause_android.ui.navigation.Routes
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CampaignDetailsScreen(
    navController: NavController,
    campId: String,
    viewModel: CampaignDetailsViewModel = hiltViewModel(),
) {
    val parentEntry = remember{navController.getBackStackEntry("campaignFlow")}
    val sharedViewModel : SharedViewModel = hiltViewModel(parentEntry)
    LaunchedEffect(Unit) {
        viewModel.getCampaignDetails(campId, sharedViewModel)
    }

    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.campaign != null -> {
            val campaign = uiState.campaign
            sharedViewModel.selectedCampaign = campaign

            Scaffold(
                bottomBar = {
                    Button(
                        onClick = {
                            navController.navigate(Routes.paymentOptionsScreen + "/$campId")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Donate Now", style = MaterialTheme.typography.titleMedium)
                    }
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Cover Image
                    item {
                        campaign?.coverImageUrl?.let { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = "Cover Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                    }

                    // Title & Status
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = campaign?.title ?: "No title",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                        }
                    }
                    item {
                        StatusBadge(status = campaign?.status.toString())
                    }

                    // Description
                    item {
                        Text(
                            text = campaign?.description ?: "No description provided.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Goal & Collected
                    item {
                        Text(
                            text = "${campaign?.currency} ${campaign?.collectedAmount?.div(100)} raised of ${
                                campaign?.goalAmount?.div(
                                    100
                                )
                            }",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        val collected = campaign?.collectedAmount ?: 0
                        val goal = campaign?.goalAmount ?: 1
                        val progressPercent = (collected.toFloat() / goal.toFloat()) * 100

                        val progressFraction = progressPercent.coerceIn(0f, 100f) / 100f
                        Spacer(modifier = Modifier.height(20.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round,
                        )


                    }

                    // Tags
                    if (campaign?.tags?.isNotEmpty() ?: false) {
                        item {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                campaign.tags.forEach { tag ->
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(tag) }
                                    )
                                }
                            }
                        }
                    }

                    // Dates & GiftAid
                    item {
                        Column {
                            campaign?.startDate?.let {
                                val formattedDate = it.toDate().let {
                                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
                                } ?: "Unknown"
                                Text(
                                    "Start Date: $formattedDate",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            campaign?.endDate?.let {
                                val formattedDate = it.toDate().let {
                                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
                                } ?: "Unknown"
                                Text(
                                    "End Date: $formattedDate",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            Text("Gift Aid: ${if (campaign?.giftAidEnabled == true) "Enabled" else "Not Enabled"}")
                            Text("Donations: ${campaign?.donationCount}")
                            campaign?.lastUpdated?.let {
                                val formattedDate = it.toDate().let {
                                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
                                } ?: "Unknown"
                                Text(
                                    "Last Updated: $formattedDate",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }

        uiState.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.error ?: "Unknown Error", color = Color.Red)
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status.lowercase()) {
        "active" -> Color(0xFF4CAF50)
        "completed" -> Color(0xFF2196F3)
        "paused" -> Color(0xFFFF9800)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.capitalize(),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
    }
}


// navController.navigate(Routes.paymentOptionsScreen + "/$campId")