package com.example.sleepkerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_login_main.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_main)

        val login_button = findViewById<Button>(R.id.login_button)
        val sign_button = findViewById<Button>(R.id.sign_button)

        BottomSheetBehavior.from(sheet).apply {
            peekHeight = 100
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        login_button.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        sign_button.setOnClickListener{
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
    }
}