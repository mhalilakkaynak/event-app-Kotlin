package com.mhapp.event.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mhapp.event.R
import com.mhapp.event.model.Event
import com.mhapp.event.ui.JoinEventActivity

class HomeAdapter(
    private val context: Context,
    private val eventList: ArrayList<Event>,
    private val eventKeyList: ArrayList<String>,
) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSender: TextView = view.findViewById(R.id.tvSender)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val constrainlayoutCardview_home: ConstraintLayout =
            view.findViewById(R.id.constrainlayoutCardview_home)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.cardview_home, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reversePosition = eventList.size - 1 - position
        holder.tvSender.text = eventList[reversePosition].sender + " :"
        holder.tvDateTime.text = eventList[reversePosition].date
        holder.tvDescription.text = eventList[reversePosition].description
        holder.constrainlayoutCardview_home.setOnClickListener {
            val intent = (Intent(context, JoinEventActivity::class.java))
            intent.putExtra("key", eventKeyList[reversePosition])
            intent.putExtra("event", eventList[reversePosition])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }
}


