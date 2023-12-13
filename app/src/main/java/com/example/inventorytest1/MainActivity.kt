package com.example.inventorytest1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.inventorytest1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val user: FirebaseUser? = auth.currentUser

        if (user != null) {
            finish()
            startActivity(Intent(this, dashboardActivity::class.java))
        }
    }

    fun login(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun register(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    override fun onStart() {
        super.onStart()

        // Check if the user is signed in with Google
        val currentUser = auth.currentUser
        if (currentUser != null) {
            finish()
            startActivity(Intent(this, dashboardActivity::class.java))
        }
    }
}
