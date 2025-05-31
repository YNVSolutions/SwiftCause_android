package com.swiftcause.swiftcause_android.ui.screen.campaign_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CampaignListScreen(
    navController: NavController,
    name: String,
    viewModel: CampaignListViewModel = CampaignListViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(top = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Hello $name", fontSize = 24.sp)
        Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp),
        contentAlignment = Alignment.Center
        ) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            else -> LazyColumn {
                items(uiState.campaigns) { campaign ->
                    CampaignCard(
                        title = campaign.title,
                        description = campaign.description,
                        goalAmount = campaign.goalAmount,
                        tags = campaign.tags
                    )

                }
            }
        }
    }
    }


}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CampaignCard(
    title: String,
    description: String?,
    goalAmount: Double,
    tags: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {},
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Goal: $${"%.2f".format(goalAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tags.forEach { tag ->
                    AssistChip(
                        onClick = {},
                        label = { Text(text = tag) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CampaignCardPreview() {
    CampaignCard(
        title = "Save the Forests",
        description = "We're raising funds to protect endangered rainforests and plant new trees across the globe.",
        goalAmount = 5000.0,
        tags = listOf("Environment", "Urgent", "TreePlanting")
    )
}
