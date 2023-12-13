package com.example.inventorytest1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.inventorytest1.databinding.ActivityScanCodeBinding
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class ScanCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanCodeBinding
    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var captureManager: CaptureManager
    private val MY_PERMISSIONS_REQUEST_CAMERA = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barcodeView = binding.barcodeScanner
        captureManager = CaptureManager(this, barcodeView)

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_CAMERA
            )
        } else {
            setupScannerView()
        }
    }

    private fun setupScannerView() {
        barcodeView.decodeContinuous { result ->
            // Handle the scanned result
            if (result != null) {
                val scannedText = result.text
                Toast.makeText(this, "Scanned barcode: $scannedText", Toast.LENGTH_SHORT).show()

                // Pass the scanned barcode to AdditemActivity
                val intent = Intent(this, AdditemActivity::class.java)
                intent.putExtra("scannedBarcode", scannedText)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            setupScannerView()
        } else {
            Toast.makeText(
                this,
                "Camera permission denied. Barcode scanning is disabled.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        captureManager.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        barcodeView.barcodeView?.stopDecoding()
    }
}
