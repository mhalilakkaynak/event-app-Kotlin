package com.mhapp.event.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.mhapp.event.model.Event

class FirebaseDatabaseService(private val context: Context) {
    val allEventList = ArrayList<Event>()
    val userNamelist = ArrayList<String>()
    val eventKeyList = ArrayList<String>()
    var lastKey = 0
    private val sharedPreferences =
        context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
    private val userName = sharedPreferences.getString("username", null) as String
    private val firebase = FirebaseDatabase.getInstance()
    fun getAllEventData() {
        val firebaseDatabaseReference = firebase.getReference("event")
        firebaseDatabaseReference.child(userName)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    for (i in snapshot.children) {
                        val event = i.getValue(Event::class.java)
                        eventKeyList.add(snapshot.key.toString())
                        lastKey = snapshot.key.toString().toInt()
                        allEventList.add(Event(event!!.sender,
                            event.description,
                            event.location,
                            event.date,
                            event.participants))
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error: ${error.toException()}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    fun getUsersData() {
        val firebaseReference = firebase.getReference("userInfo")
        firebaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                userNamelist.add(snapshot.value.toString())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: ${error.toException()}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun deleteEventData(lastKey: Int) {
        val firebaseReference = firebase.getReference("event")
        for (i in 0 until lastKey)
            firebaseReference.child(userName).child(i.toString()).removeValue()
    }
}