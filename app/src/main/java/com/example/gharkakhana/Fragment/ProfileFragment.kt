package com.example.gharkakhana.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gharkakhana.databinding.FragmentProfileBinding
import com.example.gharkakhana.model.UserModel
import com.example.gharkakhana.network.SupabaseClient
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()   // ← fixed (was wrong syntax)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        loadUserData()

        // 🔹 Disable fields initially
        setFieldsEnabled(false)

        // 🔹 Edit button toggle
        binding.editButton.setOnClickListener {
            val isEnabled = binding.name.isEnabled
            setFieldsEnabled(!isEnabled)

            binding.editButton.text = if (isEnabled) "Edit" else "Cancel"
        }

        binding.SaveInformation.setOnClickListener {
            saveUserData()
            setFieldsEnabled(false)
        }

        return binding.root
    }

    // ── Load user data from Supabase and populate fields ───────────────────
    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        // ── Show email from Firebase Auth immediately ──────────────────────
        binding.email.setText(auth.currentUser?.email ?: "")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = SupabaseClient.client.postgrest
                    .from("users")
                    .select {
                        filter { eq("id", userId) }
                    }
                    .decodeList<UserModel>()

                withContext(Dispatchers.Main) {
                    if (result.isNotEmpty()) {
                        val user = result.first()

                        // ── Populate all fields ────────────────────────────
                        binding.name.setText(user.name ?: "")
                        binding.email.setText(user.email ?: auth.currentUser?.email ?: "")
                        binding.address.setText(user.address ?: "")
                        binding.phone.setText(user.phone ?: "")

                        // ── Set hints for empty fields ─────────────────────
                        if (user.address.isNullOrBlank()) {
                            binding.address.hint = "Enter your address"
                        }
                        if (user.phone.isNullOrBlank()) {
                            binding.phone.hint = "Enter your phone number"
                        }
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load profile: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setFieldsEnabled(enabled: Boolean) {
        binding.name.isEnabled = enabled
        binding.email.isEnabled = enabled
        binding.address.isEnabled = enabled
        binding.phone.isEnabled = enabled
    }

    // ── Save / update user data in Supabase ────────────────────────────────
    private fun saveUserData() {
        val userId = auth.currentUser?.uid ?: return

        val name    = binding.name.text.toString().trim()
        val email   = binding.email.text.toString().trim()
        val address = binding.address.text.toString().trim()
        val phone   = binding.phone.text.toString().trim()

        // ── Validate fields ────────────────────────────────────────────────
        if (name.isBlank()) {
            binding.name.error = "Name is required"
            return
        }
        if (address.isBlank()) {
            binding.address.error = "Address is required"
            return
        }
        if (phone.isBlank()) {
            binding.phone.error = "Phone is required"
            return
        }

        // ── Show loading state on button ───────────────────────────────────
        binding.SaveInformation.isEnabled = false
        binding.SaveInformation.text = "Saving..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Upsert: updates if exists, inserts if new ──────────────
                SupabaseClient.client.postgrest
                    .from("users")
                    .upsert(
                        UserModel(
                            id      = userId,
                            name    = name,
                            email   = email,
                            address = address,
                            phone   = phone
                        )
                    )

                withContext(Dispatchers.Main) {
                    binding.SaveInformation.isEnabled = true
                    binding.SaveInformation.text = "Save Information"
                    Toast.makeText(
                        requireContext(),
                        "Profile saved successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.SaveInformation.isEnabled = true
                    binding.SaveInformation.text = "Save Information"
                    Toast.makeText(
                        requireContext(),
                        "Failed to save: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}