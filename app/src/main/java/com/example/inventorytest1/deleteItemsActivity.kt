package com.example.inventorytest1

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import android.widget.TextView
import com.example.inventorytest1.databinding.ActivityDeleteItemsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference


import com.google.firebase.database.FirebaseDatabase

class

deleteItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeleteItemsBinding
    private lateinit var resultdeleteview: TextView
    private

    lateinit

    var firebaseAuth: FirebaseAuth
    private

    lateinit

    var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultdeleteview = findViewById(R.id.barcodedelete)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        // Retrieve scanned barcode from intent
        val scannedBarcode = intent.getStringExtra("scannedBarcode")
        resultdeleteview.text = scannedBarcode

        binding.buttonscandelete.setOnClickListener {
            startActivity(Intent(applicationContext, ScanCodeActivitydel::class.java))
        }

        binding.deleteItemToTheDatabasebtn.setOnClickListener {
            deleteFromDatabase()
        }
    }

    private fun deleteFromDatabase() {
        val deleteBarcodeValue = resultdeleteview.text.toString()
        val users: FirebaseUser? = firebaseAuth.currentUser
        val finalUser = users?.email
        val resultEmail = finalUser?.replace(".", "")

        if (!TextUtils.isEmpty(deleteBarcodeValue)) {
            // Use the scanned barcode to delete the item from the database
            databaseReference.child(resultEmail!!).child("Items").child(deleteBarcodeValue).removeValue()
            Toast.makeText(this, "Item is Deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please scan Barcode", Toast.LENGTH_SHORT).show()
        }
    }
}