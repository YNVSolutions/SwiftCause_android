package com.swiftcause.swiftcause_android.data.remote

import android.os.Build
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.swiftcause.swiftcause_android.MyApplication
import java.sql.Timestamp

fun logDebugStep(step: String) {
    val sessionId = MyApplication.sessionId
    val user = FirebaseAuth.getInstance().currentUser

    val db = FirebaseFirestore.getInstance()
    val sessionDocRef = db.collection("debug_sessions").document(sessionId)

    val stepEntry = mapOf(
        "step" to step,
        "timestamp" to com.google.firebase.Timestamp.now()
    )

    sessionDocRef.get().addOnSuccessListener { doc ->
        if (!doc.exists()) {
            val sessionData = mapOf(
                "username" to (user?.email ?: "Unknown"),
                "device" to Build.MODEL,
                "os" to "Android ${Build.VERSION.RELEASE}",
                "createdAt" to FieldValue.serverTimestamp(),
                "logs" to listOf(stepEntry)
            )
            sessionDocRef.set(sessionData)
        } else {
            sessionDocRef.update("logs", FieldValue.arrayUnion(stepEntry))
        }
    }.addOnFailureListener {
        Log.e("DebugLogger", "Failed to fetch or create session", it)
    }
}
