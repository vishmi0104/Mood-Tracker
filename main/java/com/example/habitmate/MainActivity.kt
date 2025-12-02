package com.example.habitmate.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.habitmate.R
import com.example.habitmate.ui.home.HomeFragment
import com.example.habitmate.ui.mood.MoodFragment
import com.example.habitmate.ui.hydration.HydrationFragment
import com.example.habitmate.ui.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    
    private lateinit var bottomNavigation: BottomNavigationView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        bottomNavigation = findViewById(R.id.bottom_navigation)
        
        setupBottomNavigation()
        setupNavigationLabels()
        
        // Check if we need to navigate to a specific fragment
        handleNotificationNavigation(intent)
        
        // Show home fragment by default
        if (savedInstanceState == null && intent.getStringExtra("navigate_to") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_mood -> MoodFragment()
                R.id.nav_hydration -> HydrationFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> HomeFragment()
            }
            
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            
            true
        }
    }
    
    private fun setupNavigationLabels() {
        val menu: Menu = bottomNavigation.menu
        menu.findItem(R.id.nav_home).setTitle("🏠 Home")
        menu.findItem(R.id.nav_mood).setTitle("😊 Mood")
        menu.findItem(R.id.nav_hydration).setTitle("💧 Hydration")
        menu.findItem(R.id.nav_settings).setTitle("⚙️ Settings")
    }
    
    private fun handleNotificationNavigation(intent: android.content.Intent) {
        val navigateTo = intent.getStringExtra("navigate_to")
        if (navigateTo == "mood") {
            // Navigate to mood fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MoodFragment())
                .commit()
            bottomNavigation.selectedItemId = R.id.nav_mood
        }
    }
    
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        // Handle navigation when activity is already running
        handleNotificationNavigation(intent)
    }
}