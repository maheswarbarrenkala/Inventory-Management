package com.example.inventorytest1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventorytest1.databinding.ActivityViewInventoryBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class viewInventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewInventoryBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var adapter: FirebaseRecyclerAdapter<Items, ItemViewHolder>

    private var countTotalNoOfItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = firebaseAuth.currentUser
        val finalUser = user?.email
        val resultEmail = finalUser?.replace(".", "")
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(resultEmail!!).child("Items")

        binding.recyclerViews.layoutManager = LinearLayoutManager(this)

        mDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    countTotalNoOfItem = dataSnapshot.childrenCount.toInt()
                    binding.totalnoitem.text = countTotalNoOfItem.toString()
                } else {
                    binding.totalnoitem.text = "0"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseData", "onCancelled: ${databaseError.message}")
            }
        })

        mDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sum = 0
                for (ds in dataSnapshot.children) {
                    val item = ds.getValue(Items::class.java)
                    val price = item?.itemprice?.toIntOrNull() ?: 0
                    sum += price
                }
                binding.totalsum.text = sum.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseData", "onCancelled: ${databaseError.message}")
            }
        })
    }

    override fun onStart() {
        super.onStart()

        val options = FirebaseRecyclerOptions.Builder<Items>()
            .setQuery(mDatabaseReference, Items::class.java)
            .build()

        adapter = object : FirebaseRecyclerAdapter<Items, ItemViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
                return ItemViewHolder(view)
            }

            override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: Items) {
                holder.bind(model)
            }
        }

        binding.recyclerViews.adapter = adapter
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Items) {
            with(itemView) {
                Log.d("ViewHolderDebug", "Binding item: $item")

                val barcodeTextView = findViewById<TextView>(R.id.viewitembarcode)
                barcodeTextView?.text = "Barcode: ${item.itembarcode}" ?: run {
                    Log.e("ViewHolderDebug", "Barcode TextView is null")
                    ""
                }
                findViewById<TextView>(R.id.viewitembarcode).text = "Barcode: ${item.itembarcode}"
                findViewById<TextView>(R.id.viewitemcategory).text = "Category: ${item.itemcategory}"
                findViewById<TextView>(R.id.viewitemname).text = "Name: ${item.itemname}"
                findViewById<TextView>(R.id.viewitemprice).text = "Price: $${item.itemprice}" // Removed toString()
            }
        }
    }
}
