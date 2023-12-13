    package com.example.inventorytest1

    import android.content.Intent
    import android.os.Bundle
    import android.os.Handler
    import android.view.animation.Animation
    import android.view.animation.AnimationUtils
    import android.widget.ImageView
    import androidx.appcompat.app.AppCompatActivity
    import com.example.inventorytest1.databinding.ActivitySplashsBinding

    class SplashsActivity : AppCompatActivity() {

        private lateinit var binding: ActivitySplashsBinding
        private val splashTimeOut: Long = 2000

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivitySplashsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val logo: ImageView = binding.logo

            Handler().postDelayed({
                val intent = Intent(this@SplashsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, splashTimeOut)

            val myAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.mysplashanimation)
            logo.startAnimation(myAnim)
        }
    }
