package com.mhapp.event.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mhapp.event.service.FirebaseDatabaseService
import com.mhapp.event.adapter.HomeAdapter
import com.mhapp.event.R
import com.mhapp.event.databinding.FragmentHomeBinding
import com.mhapp.event.model.Event
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment() : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HomeAdapter
    private lateinit var firebaseDatabaseService: FirebaseDatabaseService
    private var allEventList: ArrayList<Event>? = null
    private var eventKeyList: ArrayList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        binding = FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        firebaseDatabaseService = FirebaseDatabaseService(context!!)
        binding.swipeRefresh.setOnRefreshListener {
            refresh()
        }
        setAdapter()
        toolbar()
    }

    private fun toolbar() {
        binding.toolbarHome.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.itemdelete -> {
                    Snackbar.make(binding.toolbarHome,
                        "Do you want to delete all events",
                        Snackbar.LENGTH_SHORT).setAction("Yes") {
                        try {
                            firebaseDatabaseService.deleteEventData(eventKeyList!![eventKeyList!!.size - 1].toInt())
                            refresh()
                        } catch (e: Exception) {
                            Snackbar.make(binding.toolbarHome, "Error: $e", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }.show()
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun setAdapter() {
        binding.recyclerviewHome.setHasFixedSize(true)
        binding.recyclerviewHome.layoutManager = LinearLayoutManager(context)
        try {
            if (allEventList == null && eventKeyList == null) {
                refresh()
            } else {
                adapter = HomeAdapter(context!!, allEventList!!, eventKeyList!!)
                binding.recyclerviewHome.adapter = adapter
            }
        } catch (e: Exception) {
            Snackbar.make(binding.recyclerviewHome, "Error: $e", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun refresh() {

        firebaseDatabaseService = FirebaseDatabaseService(context!!)
        firebaseDatabaseService.getAllEventData()
        binding.recyclerviewHome.postDelayed({
            try {
                adapter =
                    HomeAdapter(context !!,
                        firebaseDatabaseService.allEventList,
                        firebaseDatabaseService.eventKeyList)
                binding.recyclerviewHome.adapter = adapter
                binding.swipeRefresh.isRefreshing = false
            } catch (e: Exception) {
                Snackbar.make(binding.swipeRefresh, "Error: $e", Snackbar.LENGTH_SHORT).show()
            }
        }, 700)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun sendEvent(allEventList: ArrayList<Event>, eventKeyList: ArrayList<String>) {
        this.allEventList = allEventList
        this.eventKeyList = eventKeyList
    }
}