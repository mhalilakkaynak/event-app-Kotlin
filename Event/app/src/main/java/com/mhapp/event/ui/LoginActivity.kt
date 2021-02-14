package com.mhapp.event.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mhapp.event.service.FirebaseDatabaseService
import com.mhapp.event.databinding.ActivityLoginBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var binding: ActivityLoginBinding
    private val firebase = FirebaseDatabase.getInstance()
    private val firebaseDatabaseReference = firebase.getReference("userInfo")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginControl()

    }

    private fun loginControl() {
        binding.shimmer.startShimmerAnimation()
        val sharedPreferences =
            this.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", null)
        val userPassword = sharedPreferences.getString("userPassword", null)
        if (userEmail != null && userPassword != null) {
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(userEmail,
                    userPassword).addOnCompleteListener {
                    if (it.isSuccessful) {
                        gotoMain()
                    } else {
                        loginVisibility()
                        initAuthStateListener()
                        register()
                        login()
                    }
                }
        } else {
            loginVisibility()
            initAuthStateListener()
            register()
            login()
        }
    }

    private fun loginVisibility() {
        binding.shimmer.stopShimmerAnimation()
        binding.shimmer.visibility = View.INVISIBLE
        binding.textView.visibility = View.INVISIBLE
        binding.btnLogin.visibility = View.VISIBLE
        binding.btnRegisterLogin.visibility = View.VISIBLE
        binding.edtUserNameLogin.visibility = View.VISIBLE
        binding.edtEmailLogin.visibility = View.VISIBLE
        binding.edtPasswordLogin.visibility = View.VISIBLE
    }

    private fun gotoMain() {
        val getData = FirebaseDatabaseService(this)
        getData.getAllEventData()
        val intent = Intent(this, MainActivity::class.java)
        GlobalScope.launch {
            delay(2000)
            intent.putExtra("allEventList",
                getData.allEventList)
            intent.putExtra("eventKeyList", getData.eventKeyList)
            intent.putExtra("location", "login")
            startActivity(intent)
        }
    }

    private fun initAuthStateListener() {
        authStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val user = p0.currentUser
                if (user != null) {
                    if (user.isEmailVerified) {
                        Snackbar.make(binding.btnLogin,
                            "Your e-mail address is approved",
                            Snackbar.LENGTH_SHORT).show()
                        gotoMain()
                        finish()
                    } else {
                        Snackbar.make(binding.btnLogin,
                            "Wait for your e-mail address to be confirmed",
                            Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun login() {
        binding.btnLogin.setOnClickListener {
            if (emptyControl()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(binding.edtEmailLogin.text.toString(),
                        binding.edtPasswordLogin.text.toString())
                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {
                            if (p0.isSuccessful) {
                                Snackbar.make(binding.btnLogin,
                                    "Login successful",
                                    Snackbar.LENGTH_SHORT).show()
                                FirebaseAuth.getInstance().signOut()
                                saveUsername()
                                gotoMain()
                            } else {
                                Snackbar.make(binding.btnLogin,
                                    "Login is wrong",
                                    Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    })
            }
        }
    }

    private fun register() {
        binding.btnRegisterLogin.setOnClickListener {
            if (emptyControl()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(binding.edtEmailLogin.text.toString(),
                        binding.edtPasswordLogin.text.toString())
                    .addOnCompleteListener { p0 ->
                        if (p0.isSuccessful) {
                            sendmail()
                            saveUsername()
                            Snackbar.make(binding.btnRegisterLogin,
                                "User registered",
                                Snackbar.LENGTH_SHORT)
                                .show()
                            FirebaseAuth.getInstance().signOut()

                        } else {
                            Snackbar.make(binding.btnRegisterLogin,
                                "Error: ${p0.exception?.message}",
                                Snackbar.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun sendmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                Snackbar.make(binding.btnRegisterLogin,
                    "Please check your email",
                    Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.btnRegisterLogin,
                    "Error: ${p0.exception?.message}",
                    Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun emptyControl(): Boolean {
        return !(binding.edtEmailLogin.text.isEmpty() && binding.edtPasswordLogin.text.isEmpty() && binding.edtUserNameLogin.text.isEmpty())
    }

    private fun saveUsername() {
        firebaseDatabaseReference.child(binding.edtUserNameLogin.text.toString())
            .setValue(binding.edtUserNameLogin.text.toString())
        val sharedPreferences =
            this.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sharedPreferences.edit()
        try {
            edit.putString("username", binding.edtUserNameLogin.text.toString())
            edit.putString("userEmail", binding.edtEmailLogin.text.toString())
            edit.putString("userPassword", binding.edtPasswordLogin.text.toString())
            edit.apply()
        } catch (e: Exception) {
            Snackbar.make(binding.edtUserNameLogin,
                "Username not registered: ${e.message}",
                Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}