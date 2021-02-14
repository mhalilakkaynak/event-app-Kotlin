package com.mhapp.event.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.mhapp.event.service.FirebaseDatabaseService
import com.mhapp.event.model.Event
import com.mhapp.event.service.SetDateTime
import com.mhapp.event.databinding.ActivityEventBinding
import com.mhapp.event.model.NotificationData
import com.mhapp.event.model.PushNotification
import com.mhapp.event.service.RetrofitObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventBinding
    private val firebase = FirebaseDatabase.getInstance()
    private val firebaseDatabaseReference = firebase.getReference("event")
    private lateinit var firebaseDatabaseService: FirebaseDatabaseService
    private val eventList = ArrayList<Event>()
    private val participantsList = ArrayList<String>()
    private val topic = "/topics/event"
    private var lastKey: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
        firebaseDatabaseService = FirebaseDatabaseService(this)
        firebaseDatabaseService.getUsersData()
        lastKey = intent.getIntExtra("lastKey", 0)
        setDateTime()
        delete()
        share()

    }

    private fun share() {
        binding.btnShare.setOnClickListener {
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/Enter_topic")
            val sharedPreferences =
                this.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
            val sender = sharedPreferences.getString("username", null) as String
            participantsList.add("$sender ✓")
            val description = binding.textInputEdtDescription.text.toString()
            val location = binding.textInputEdtLocation.text.toString()
            val date = binding.tvDate.text.toString()
            if (description != "" && location != "" && date != "") {
                eventList.add(Event(sender,
                    description,
                    location,
                    date,
                    participantsList
                ))
            }
            if (eventList.isNotEmpty()) {
                if (binding.textInputEdtDescription.text.toString().length <= 70 && binding.textInputEdtLocation.text.toString().length <= 70) {
                    for (i in firebaseDatabaseService.userNamelist) {
                        firebaseDatabaseReference.child(i).child(lastKey.toString())
                            .setValue(eventList)
                            .addOnCompleteListener {
                                if (!it.isSuccessful) {
                                    Toast.makeText(this,
                                        "Event could not be created",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    Toast.makeText(this, "Event created", Toast.LENGTH_SHORT)
                        .show()
                    val data = NotificationData(sender,
                        binding.textInputEdtDescription.text.toString())
                    val notification = PushNotification(data, topic)
                    sendNotification(notification)
                    onBackPressed()
                } else {
                    Snackbar.make(binding.btnShare,
                        "You cross the character limit.",
                        Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(binding.btnShare,
                    "You can't create an empty event",
                    Snackbar.LENGTH_SHORT).show()
            }
            eventList.clear()
        }
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitObject.api.pushNotification(notification)
                if (response.isSuccessful) {
                    Log.e("response", response.toString())
                } else {
                    Log.e("error response", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Snackbar.make(binding.btnShare, "Error: $e", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

    private fun delete() {
        binding.ibtnDelete.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setDateTime() {
        binding.ibtnDate.setOnClickListener {
            val dateTime = SetDateTime(this, binding.   tvDate)
            dateTime.setDateTime()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("location", "event")
        startActivity(intent)
    }
}
