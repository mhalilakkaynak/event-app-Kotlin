package com.mhapp.event.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mhapp.event.service.FirebaseDatabaseService
import com.mhapp.event.R
import com.mhapp.event.databinding.ActivityMainBinding
import com.mhapp.event.model.Event
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var firebaseDatabaseService: FirebaseDatabaseService
    private var keyList: ArrayList<String>? = null
    private var allEventList: ArrayList<Event>? = null
    private lateinit var location: String
    private val homeFragment = HomeFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        location = intent.getStringExtra("location") as String
        if (location == "login") {
            allEventList =
                intent.getSerializableExtra("allEventList") as ArrayList<Event>
            keyList = intent.getStringArrayListExtra("eventKeyList") as ArrayList<String>
            homeFragment.sendEvent(allEventList!!, keyList!!)
        }
        setFragment(homeFragment)
        navigationView()
    }

    private fun navigationView() {
        binding.bottomNavigationviewMain.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.itemHome -> {

                    setFragment(HomeFragment())
                    true
                }
                R.id.itemEvent -> {
                    val intent = Intent(this, EventActivity::class.java)
                    if (location == "login") {
                        if (keyList!!.size != 0) {
                            intent.putExtra("lastKey", (keyList!![keyList!!.size - 1]).toInt() + 1)
                        } else {
                            intent.putExtra("lastKey", 1)
                        }
                        startActivity(intent)
                    } else if (location == "event") {
                        firebaseDatabaseService = FirebaseDatabaseService(this)
                        firebaseDatabaseService.getAllEventData()
                        GlobalScope.launch {
                            delay(1000)
                            intent.putExtra("lastKey",
                                firebaseDatabaseService.lastKey + 1)
                            startActivity(intent)
                        }
                    }
                    true
                }
                R.id.itemUser -> {
                    setFragment(UserFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutMain, fragment)
            .commit()
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


}