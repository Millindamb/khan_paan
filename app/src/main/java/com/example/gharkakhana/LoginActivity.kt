package com.example.gharkakhana

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.gharkakhana.databinding.ActivityLoginBinding
import com.example.gharkakhana.model.UserModel
import com.example.gharkakhana.network.SupabaseClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
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

        // ── Email login ────────────────────────────────────────────────────
        binding.loginbutton.setOnClickListener {
            val email    = binding.emailAddressLgn.text.toString().trim()
            val password = binding.passwordLgn.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                loginWithEmail(email, password)
            }
        }

        // ── Google login ───────────────────────────────────────────────────
        binding.googleButton.setOnClickListener {
            launcher.launch(googleSignInClient.signInIntent)
        }

        binding.donthavebutton.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    // ── Email login: user already in Supabase from signup ──
                    // No need to upsert again, just navigate
                    goToMain()
                } else {
                    Toast.makeText(
                        this,
                        "Login Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun ensureUserInSupabase(userId: String, name: String, email: String) {
        // ── Only for Google login: upsert so user row exists in Supabase ──
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.client.postgrest
                    .from("users")
                    .upsert(
                        UserModel(
                            id      = userId,
                            name    = name,
                            email   = email,
                            address = "",
                            phone   = ""
                        )
                    )

                withContext(Dispatchers.Main) {
                    goToMain()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // ── Still navigate even if Supabase upsert fails ───────
                    goToMain()
                    Toast.makeText(
                        this@LoginActivity,
                        "Profile sync failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            goToMain()
        }
    }

    // ── Google Sign-In launcher ────────────────────────────────────────────
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount = task.result
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                val user   = auth.currentUser
                                val userId = user?.uid ?: return@addOnCompleteListener
                                // ── Ensure Google user exists in Supabase ──
                                ensureUserInSupabase(
                                    userId = userId,
                                    name   = user.displayName ?: "",
                                    email  = user.email ?: ""
                                )
                            } else {
                                Toast.makeText(
                                    this,
                                    "Google Login Failed: ${authTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
}