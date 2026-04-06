package com.example.gharkakhana

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gharkakhana.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class PayOutActivity : AppCompatActivity() {

    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Safe Firebase user fetch
        val user = auth.currentUser

        if (user != null) {
            binding.email.setText(user.email ?: "")
            binding.name.setText(user.displayName ?: "")
        }

        // ✅ Back button
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // ✅ Save button click
        binding.SaveInformation.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val address = binding.address.text.toString().trim()
            val phone = binding.phone.text.toString().trim()

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }
}