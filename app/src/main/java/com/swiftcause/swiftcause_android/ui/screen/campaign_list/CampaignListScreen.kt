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
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.swiftcause.swiftcause_android.ui.navigation.Routes
import com.swiftcause.swiftcause_android.ui.screen.login.AuthUiState
import com.swiftcause.swiftcause_android.ui.screen.login.AuthViewModel

@Composable
fun CampaignListScreen(
    navController: NavController,
    name: String,
    viewModel: CampaignListViewModel = hiltViewModel(),
    authViewModel : AuthViewModel = hiltViewModel(),
    onLogoutRedirect: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
       Heading(authViewModel, onLogoutRedirect)
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
                            tags = campaign.tags,
                            navController = navController,
                            campId = campaign.id

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
    navController: NavController,
    campId : String,
    modifier: Modifier = Modifier,

) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate(Routes.campaignDetailsScreen + "/${campId}")
            },
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

@Composable
fun Heading(authViewModel: AuthViewModel,onLogoutRedirect: () -> Unit ) {
    val authState by authViewModel.authUiState.collectAsState()
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (authState) {
            is AuthUiState.Authenticated -> {
                val user = (authState as AuthUiState.Authenticated).user.currentUser
                Text("Welcome, ${user?.displayName ?: user?.email}!")
                Button(onClick = { authViewModel.signOut(context) }) {
                    Text("Sign Out")
                }

            }
            else -> {
                onLogoutRedirect()
            }
        }
    }
}
