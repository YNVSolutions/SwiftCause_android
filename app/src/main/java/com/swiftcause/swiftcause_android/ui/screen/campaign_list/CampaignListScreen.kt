package com.swiftcause.swiftcause_android.ui.screen.campaign_list

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swiftcause.swiftcause_android.ui.navigation.Routes
import com.swiftcause.swiftcause_android.ui.screen.login.AuthUiState
import com.swiftcause.swiftcause_android.ui.screen.login.AuthViewModel
import com.swiftcause.swiftcause_android.ui.shared.SharedViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun CampaignListScreen(
    navController: NavController,
    viewModel: CampaignListViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel = hiltViewModel(),
    onLogoutRedirect: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.getCampaignList(sharedViewModel)
    }
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 5.dp, horizontal = 5.dp),
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
                            goalAmount = campaign.goalAmount / 100,
                            tags = campaign.tags,
                            imageUrl = campaign.coverImageUrl,
                            onClickAction = {
                                navController.navigate("${Routes.campaignDetailsScreen}/${campaign.id}")}

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
    modifier: Modifier = Modifier,
    title: String,
    description: String?,
    goalAmount: Double,
    tags: List<String>,
    imageUrl: String? = null,
    onClickAction : () -> Unit

    ) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable {
                onClickAction()
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {

            imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Campaign Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                description?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Goal: $${"%,.2f".format(goalAmount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                if (tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        tags.forEach { tag ->
                            AssistChip(
                                onClick = {},
                                label = { Text(tag) }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Heading(authViewModel: AuthViewModel, onLogoutRedirect: () -> Unit) {
    val authState by authViewModel.authUiState.collectAsState()
    val context = LocalContext.current

    when (authState) {
        is AuthUiState.Authenticated -> {
            val user = (authState as AuthUiState.Authenticated).user.currentUser
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Welcome, ${user?.displayName ?: user?.email}!")
                Button(onClick = { authViewModel.signOut(context) }) {
                    Text("Sign Out")
                }
            }
        }

        else -> {
            onLogoutRedirect()
        }
    }

}
