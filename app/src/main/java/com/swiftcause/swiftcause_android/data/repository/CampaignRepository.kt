package com.swiftcause.swiftcause_android.data.repository

import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.swiftcause.swiftcause_android.data.model.Campaign
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class CampaignRepository(){
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val campaignCollection = db.collection("campaigns")

    suspend fun fetchCampaigns() : List<Campaign>{
        return try{
            val snapshot = campaignCollection.get().await()

            val campaigns = snapshot.documents.mapNotNull { it.toObject(Campaign::class.java) }
            Log.i("FirestoreTag", "got: ${campaigns.size} campaigns")
            return campaigns
        }catch (e : Exception){
            Log.e("FirestoreTag", e.message?:"some error occurred")
            emptyList()
        }
    }
}