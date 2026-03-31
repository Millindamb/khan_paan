package com.example.gharkakhana

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.gharkakhana.databinding.ActivitySigninBinding
import com.example.gharkakhana.model.UserModel
import com.example.gharkakhana.network.SupabaseClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SigninActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivitySigninBinding by lazy {
        ActivitySigninBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth

        val googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.createAccountButton.setOnClickListener {
            val userName = binding.userName.text.toString().trim()
            val email    = binding.emailAddress.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (userName.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password, userName)
            }
        }

        binding.alreadyhaveaccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.googleButton.setOnClickListener {
            launcher.launch(googleSignInClient.signInIntent)
        }
    }

    private fun createAccount(email: String, password: String, userName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    // ── Save to Supabase after Firebase Auth success ────────
                    saveUserToSupabase(
                        userId   = userId,
                        name     = userName,
                        email    = email,
                        address  = "",
                        phone    = ""
                    )
                } else {
                    Toast.makeText(
                        this,
                        "Account Creation Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveUserToSupabase(
        userId: String,
        name: String,
        email: String,
        address: String,
        phone: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
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
                    Toast.makeText(
                        this@SigninActivity,
                        "Account Created Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@SigninActivity, MainActivity::class.java))
                    finish()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SigninActivity,
                        "Failed to save user: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // ── Google Sign-In launcher ────────────────────────────────────────────
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

                auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val user   = auth.currentUser
                        val userId = user?.uid ?: return@addOnCompleteListener
                        // ── Save Google user to Supabase ───────────────────
                        saveUserToSupabase(
                            userId  = userId,
                            name    = user.displayName ?: "",
                            email   = user.email ?: "",
                            address = "",
                            phone   = ""
                        )
                    } else {
                        Toast.makeText(this, "Google Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}