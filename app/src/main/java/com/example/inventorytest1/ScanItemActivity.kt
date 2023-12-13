package com.example.inventorytest1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.zxing.integration.android.IntentIntegrator
import com.example.inventorytest1.databinding.ActivityScanItemsBinding

class ScanItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanItemsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mdatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = firebaseAuth.currentUser
        val finalUser = user?.email
        val resultEmail = finalUser?.replace(".", "")
        mdatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(resultEmail!!).child("Items")

        binding.scantosearch.setOnClickListener {
            startBarcodeScanner()
        }

        binding.searchbtn.setOnClickListener {
            val searchText = binding.searchfield.text.toString().trim()
            if (searchText.isNotEmpty()) {
                firebasesearch(searchText)
            } else {
                showToast("Please enter a barcode to search.")
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViews.layoutManager = layoutManager
        binding.recyclerViews.setHasFixedSize(true)
    }
    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
    private fun startBarcodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                // Set the scanned barcode to the search field
                binding.searchfield.setText(result.contents)
            } else {
                showToast("Barcode scanning canceled.")
            }
        }
    }


    private fun firebasesearch(searchText: String) {
        val firebaseSearchQuery: Query =
            mdatabaseReference.orderByChild("itembarcode").startAt(searchText).endAt(searchText + "\uf8ff")

        val options: FirebaseRecyclerOptions<Items> =
            FirebaseRecyclerOptions.Builder<Items>()
                .setQuery(firebaseSearchQuery, Items::class.java)
                .build()

        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Items, UsersViewHolder>(options) {
            override fun onDataChanged() {
                super.onDataChanged()
                // Handle no search results (e.g., show a message to the user)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_layout, parent, false)
                return UsersViewHolder(view)
            }

            override fun onBindViewHolder(holder: UsersViewHolder, position: Int, model: Items) {
                holder.setDetails(
                    applicationContext,
                    model.itembarcode,
                    model.itemcategory,
                    model.itemname,
                    model.itemprice
                )
            }
        }

        binding.recyclerViews.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mView: View = itemView

        fun setDetails(
            context: Context,
            itembarcode: String,
            itemcategory: String,
            itemname: String,
            itemprice: String
        ) {
            val item_barcode: TextView = mView.findViewById(R.id.itembarcode)
            val item_name: TextView = mView.findViewById(R.id.itemname)
            val item_category: TextView = mView.findViewById(R.id.itemcategory)
            val item_price: TextView = mView.findViewById(R.id.itemprice)

            item_barcode.text = itembarcode
            item_category.text = itemcategory
            item_name.text = itemname
            item_price.text = itemprice
        }
    }
}
