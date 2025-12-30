package com.markoala.tomoandroid.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var userId by mutableStateOf("")

    init {
        loadUser()
    }

    private fun loadUser() {
        auth.currentUser?.let { user ->
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    name = doc.getString("name") ?: ""
                    email = doc.getString("email") ?: ""
                    userId = doc.getString("uid") ?: ""
                }
        }
    }
}
