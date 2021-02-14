package com.mhapp.event.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.mhapp.event.service.FirebaseDatabaseService
import com.mhapp.event.databinding.ActivityJoinEventBinding
import com.mhapp.event.model.Event

class JoinEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinEventBinding
    private lateinit var firebaseDatabaseService: FirebaseDatabaseService
    private val firebase = FirebaseDatabase.getInstance()
    private val firebaseDatabaseReference = firebase.getReference("event")
    private lateinit var eventKey: String
    private var participants = ArrayList<String>()
    private lateinit var event: Event
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseDatabaseService = FirebaseDatabaseService(this)
        eventKey = intent.getStringExtra("key") as String
        firebaseDatabaseService.getUsersData()
        event = intent.getSerializableExtra("event") as Event
        getData()
        join()
    }

    private fun join() {
        val sharedPreferences =
            this.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val user = sharedPreferences.getString("username", null) as String
        binding.ibtnJoin.setOnClickListener {
            if (participants.contains("$user X")) {
                participants[participants.indexOf("$user X")] = "$user ✓"
                addParticipants()
            } else if (!(participants.contains("$user ✓"))) {
                participants.add("$user ✓")
                addParticipants()
            } else {
                Snackbar.make(binding.ibtnJoin,
                    "You are already participating in this event.",
                    Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.ibtnReject.setOnClickListener {
            if (participants.contains("$user ✓")) {
                participants[participants.indexOf("$user ✓")] = "$user X"
                addParticipants()
            } else if (!participants.contains("$user X")) {
                participants.add("$user X")
                addParticipants()
            } else {
                Snackbar.make(binding.ibtnReject,
                    "You are not already participating in this event.",
                    Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun addParticipants() {
        for (i in firebaseDatabaseService.userNamelist) {
            firebaseDatabaseReference.child(i).child(eventKey).child("0")
                .child("participants")
                .setValue(participants)
            setParticipantsText()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData() {
        binding.tvCreateby.text = "Created by ${event.sender}"
        binding.tvJoinDate.text = event.date
        binding.tvDescriptionLocation.text =
            event.description + "\n\n" + event.location
        participants.addAll(event.participants)
        setParticipantsText()
    }

    @SuppressLint("SetTextI18n")
    private fun setParticipantsText() {
        binding.tvParticipants.text = ""
        for (i in 1..participants.size) {
            binding.tvParticipants.text =
                binding.tvParticipants.text.toString() + "\n" + participants[i - 1]
        }
    }
}