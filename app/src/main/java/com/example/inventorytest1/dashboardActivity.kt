package com.example.inventorytest1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventorytest1.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class dashboardActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val users: FirebaseUser? = firebaseAuth.currentUser
        val finaluser = users?.email
        val result = finaluser?.substring(0, finaluser.indexOf("@"))
        val resultemail = result?.replace(".", "")
        binding.firebasename.text = "Welcome, $resultemail"

        binding.addItems.setOnClickListener(this)
        binding.deleteItems.setOnClickListener(this)
        binding.scanItems.setOnClickListener(this)
        binding.viewInventory.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val intent: Intent = when (view.id) {
            R.id.addItems -> Intent(this, AdditemActivity::class.java)
            R.id.deleteItems -> Intent(this, deleteItemsActivity::class.java)
            R.id.scanItems -> Intent(this, ScanItemActivity::class.java)
            R.id.viewInventory -> Intent(this, viewInventoryActivity::class.java)
            else -> throw IllegalArgumentException("Unexpected view ID")
        }
        startActivity(intent)
    }

    private fun logout() {
        firebaseAuth.signOut()
        finish()
        startActivity(Intent(this@dashboardActivity, LoginActivity::class.java))
        Toast.makeText(this@dashboardActivity, "LOGOUT SUCCESSFUL", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logoutMenu -> {
                logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
