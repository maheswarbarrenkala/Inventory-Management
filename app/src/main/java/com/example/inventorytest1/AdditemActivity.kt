package com.example.inventorytest1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.inventorytest1.databinding.ActivityAdditemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdditemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdditemBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReferencecat: DatabaseReference

    // Request code for SMS permission
    private val SMS_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdditemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        databaseReferencecat = FirebaseDatabase.getInstance().reference.child("Users")

        binding.additembuttontodatabase.setOnClickListener { additem() }
        binding.buttonscan.setOnClickListener { startActivity(Intent(applicationContext, ScanCodeActivity::class.java)) }
    }

    private fun additem() {
        val itemnameValue = binding.edititemname.text.toString()
        val itemcategoryValue = binding.editcategory.text.toString()
        val itempriceValue = binding.editprice.text.toString()

        // Retrieve the scanned barcode value
        val scannedBarcode = intent.getStringExtra("scannedBarcode")
        if (scannedBarcode != null) {
            binding.barcodeview.text = scannedBarcode
        }

        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        val finalUser = currentUser?.email?.replace(".", "")

        if (binding.barcodeview.text.isEmpty()) {
            binding.barcodeview.error = "It's Empty"
            binding.barcodeview.requestFocus()
            return
        }

        if (!TextUtils.isEmpty(itemnameValue) && !TextUtils.isEmpty(itemcategoryValue) && !TextUtils.isEmpty(itempriceValue)) {
            val items = Items(itemnameValue, itemcategoryValue, itempriceValue, binding.barcodeview.text.toString())

            // Adding the item to the user's items in the database
            databaseReference.child(finalUser!!).child("Items").child(binding.barcodeview.text.toString()).setValue(items)



            // Check for SMS permission
            if (isSmsPermissionGranted()) {
                val userPhoneNumber = binding.editPhoneNumber.text.toString().trim()

                if (userPhoneNumber.isNotEmpty()) {
                    sendSms(userPhoneNumber, "$itemnameValue Added successfully. Thank you!")
                    Toast.makeText(this@AdditemActivity, "Product added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@AdditemActivity, "Please enter a phone number", Toast.LENGTH_SHORT).show()
                }
            } else {
                requestSmsPermission()
            }

            binding.edititemname.setText("")
            binding.editcategory.setText("")
            binding.editprice.setText("")
            binding.barcodeview.setText("")

            Toast.makeText(this@AdditemActivity, "$itemnameValue Added", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@AdditemActivity, "Please Fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSms(phoneNumber: String, message: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    private fun isSmsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@AdditemActivity,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            this@AdditemActivity,
            arrayOf(Manifest.permission.SEND_SMS),
            SMS_PERMISSION_REQUEST_CODE
        )
    }

    private fun logout() {
        firebaseAuth.signOut()
        finish()
        startActivity(Intent(this@AdditemActivity, LoginActivity::class.java))
        Toast.makeText(this@AdditemActivity, "LOGOUT SUCCESSFUL", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logoutMenu -> logout()
        }
        return super.onOptionsItemSelected(item)
    }

    // Handle the result of the permission request
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send SMS
                val userPhoneNumber = binding.editPhoneNumber.text.toString().trim()
                sendSms(userPhoneNumber, "Permission granted. SMS sent successfully.")
                Toast.makeText(this@AdditemActivity, "SMS sent successfully", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                Toast.makeText(this@AdditemActivity, "SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
