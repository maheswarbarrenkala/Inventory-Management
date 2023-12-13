package com.example.inventorytest1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.example.inventorytest1.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.buttonRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name = binding.departmentName.text.toString().trim()
        val email = binding.emailRegister.text.toString()
        val password = binding.passwordRegister.text.toString().trim()
        val cpassword = binding.confirmPassword.text.toString().trim()

        if (email.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Email and Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailRegister.error = "Not a valid email address"
            binding.emailRegister.requestFocus()
            return
        }

        if (password.isEmpty() || password.length < 6) {
            binding.passwordRegister.error = "Password must be at least 6 characters long"
            binding.passwordRegister.requestFocus()
            return
        }

        if (!password.equals(cpassword)) {
            binding.confirmPassword.error = "Passwords do not match"
            binding.confirmPassword.requestFocus()
            return
        }

        binding.progressbar.visibility = View.VISIBLE

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    val user = User(name, email)
                    val currentUser: FirebaseUser? = mAuth.currentUser
                    val userID: String = currentUser?.email?.replace(".", "") ?: ""

                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(userID).child("UserDetails")
                        .setValue(user)
                        .addOnCompleteListener { task1: Task<Void> ->
                            binding.progressbar.visibility = View.GONE
                            if (task1.isSuccessful) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Registration Success",
                                    Toast.LENGTH_LONG
                                ).show()
                                startActivity(Intent(this@RegisterActivity, dashboardActivity::class.java))
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Failed to save user details",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(this@RegisterActivity, "Registration Failed", Toast.LENGTH_LONG)
                        .show()
                }
            })
    }
}
