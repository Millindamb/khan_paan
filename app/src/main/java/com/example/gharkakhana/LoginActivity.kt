package com.example.gharkakhana

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gharkakhana.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.loginbutton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.donthavebutton.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
    }
}