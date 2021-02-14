package com.mhapp.event.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.mhapp.event.databinding.FragmentUserBinding
import com.mhapp.event.service.FirebaseDatabaseService
import java.lang.Exception
 

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentUserBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseDatabaseService: FirebaseDatabaseService
    private lateinit var userName: String
    private lateinit var userEmail: String
    private lateinit var userPassword: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        binding = FragmentUserBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPreferences = context!!.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        userName = sharedPreferences.getString("username", null) as String
        userEmail = sharedPreferences.getString("userEmail", null) as String
        userPassword = sharedPreferences.getString("userPassword", null) as String

        binding.tvUserName.text = userName
        firebaseDatabaseService = FirebaseDatabaseService(context!!)
        firebaseDatabaseService.getUsersData()
        logOut()
        deleteAccount()
    }

    private fun logOut() {
        binding.btnLogout.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.remove("username")
            editor.remove("userEmail")
            editor.remove("userPassword")
            editor.apply()
            Toast.makeText(context, "Logout is successful", Toast.LENGTH_SHORT).show()
            startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    private fun deleteAccount() {
        binding.btnDeleteAccount.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val credential =
                EmailAuthProvider.getCredential(userEmail, userPassword)
            try {
                user!!.reauthenticate(credential).addOnCompleteListener {
                    user.delete().addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(context, "User account deleted", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                }
            } catch (e: Exception) {
                Snackbar.make(binding.btnDeleteAccount, "Error: $e", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}