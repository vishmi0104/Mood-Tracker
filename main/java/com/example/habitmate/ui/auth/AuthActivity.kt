package com.example.habitmate.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.habitmate.R
import com.example.habitmate.data.DataManager
import com.example.habitmate.data.User
import com.example.habitmate.ui.main.MainActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AuthActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var dataManager: DataManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        
        dataManager = DataManager(this)
        
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        
        setupViewPager()
    }
    
    private fun setupViewPager() {
        val adapter = AuthPagerAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.login)
                1 -> getString(R.string.register)
                else -> ""
            }
        }.attach()
    }
    
    fun login(email: String, password: String) {
        if (validateLogin(email, password)) {
            // Simple validation - in real app, you'd check against server
            val user = User(
                id = dataManager.generateId(),
                name = email.substringBefore("@"),
                email = email
            )
            dataManager.saveUser(user)
            navigateToMain()
        }
    }
    
    fun register(name: String, email: String, password: String, confirmPassword: String) {
        if (validateRegister(name, email, password, confirmPassword)) {
            val user = User(
                id = dataManager.generateId(),
                name = name,
                email = email
            )
            dataManager.saveUser(user)
            navigateToMain()
        }
    }
    
    private fun validateLogin(email: String, password: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.error_email_required), Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.error_email_invalid), Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.error_password_required), Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, getString(R.string.error_password_short), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    
    private fun validateRegister(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.error_name_required), Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.error_email_required), Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.error_email_invalid), Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.error_password_required), Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, getString(R.string.error_password_short), Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmPassword) {
            Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    
    private inner class AuthPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        
        override fun getItemCount(): Int = 2
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> LoginFragment()
                1 -> RegisterFragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}