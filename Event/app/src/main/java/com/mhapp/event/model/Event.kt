package com.mhapp.event.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
class Event : Serializable {
    lateinit var participants: ArrayList<String>
    lateinit var date: String
    lateinit var description: String
    lateinit var location: String
    lateinit var sender: String

    constructor()
    constructor(
        sender: String?,
        description: String?,
        location: String?,
        date: String?,
        participants: ArrayList<String>?,
    ) : this() {
        this.sender = sender.toString()
        this.description = description.toString()
        this.date = date.toString()
        this.location = location.toString()
        if (participants != null) {
            this.participants = participants
        }
    }
}