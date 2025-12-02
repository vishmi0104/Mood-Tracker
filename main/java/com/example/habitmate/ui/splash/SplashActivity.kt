package com.example.habitmate.ui.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.habitmate.data.DataManager
import com.example.habitmate.databinding.ActivitySplashBinding
import com.example.habitmate.ui.auth.AuthActivity
import com.example.habitmate.ui.main.MainActivity
import com.example.habitmate.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private lateinit var dataManager: DataManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        dataManager = DataManager(this)
        
        setupAnimations()
        startSplashSequence()
    }
    
    private fun setupAnimations() {
        // Initial state - all elements invisible
        binding.logoContainer.alpha = 0f
        binding.logoContainer.scaleX = 0.5f
        binding.logoContainer.scaleY = 0.5f
        binding.tagline.alpha = 0f
        binding.tagline.translationY = 30f
        binding.versionInfo.alpha = 0f
        binding.loading.alpha = 0f
        binding.loadingText.alpha = 0f
        
        // Decorative elements
        binding.decorative1.alpha = 0f
        binding.decorative1.scaleX = 0.8f
        binding.decorative1.scaleY = 0.8f
        binding.decorative2.alpha = 0f
        binding.decorative2.scaleX = 0.8f
        binding.decorative2.scaleY = 0.8f
        binding.decorative3.alpha = 0f
        binding.decorative3.scaleX = 0.8f
        binding.decorative3.scaleY = 0.8f
    }
    
    private fun startSplashSequence() {
        Handler(Looper.getMainLooper()).postDelayed({
            animateLogo()
        }, 300)
        
        Handler(Looper.getMainLooper()).postDelayed({
            animateTagline()
        }, 800)
        
        Handler(Looper.getMainLooper()).postDelayed({
            animateVersionInfo()
        }, 1200)
        
        Handler(Looper.getMainLooper()).postDelayed({
            animateDecorativeElements()
        }, 1500)
        
        Handler(Looper.getMainLooper()).postDelayed({
            animateLoading()
        }, 1800)
        
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 3000)
    }
    
    private fun animateLogo() {
        val scaleX = ObjectAnimator.ofFloat(binding.logoContainer, "scaleX", 0.5f, 1.1f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.logoContainer, "scaleY", 0.5f, 1.1f, 1f)
        val alpha = ObjectAnimator.ofFloat(binding.logoContainer, "alpha", 0f, 1f)
        
        val logoAnimator = AnimatorSet()
        logoAnimator.playTogether(scaleX, scaleY, alpha)
        logoAnimator.duration = 800
        logoAnimator.interpolator = OvershootInterpolator(1.2f)
        logoAnimator.start()
        
        // Logo rotation animation
        val rotation = ObjectAnimator.ofFloat(binding.logo, "rotation", 0f, 360f)
        rotation.duration = 1000
        rotation.interpolator = AccelerateDecelerateInterpolator()
        rotation.start()
    }
    
    private fun animateTagline() {
        val alpha = ObjectAnimator.ofFloat(binding.tagline, "alpha", 0f, 1f)
        val translationY = ObjectAnimator.ofFloat(binding.tagline, "translationY", 30f, 0f)
        
        val taglineAnimator = AnimatorSet()
        taglineAnimator.playTogether(alpha, translationY)
        taglineAnimator.duration = 500
        taglineAnimator.interpolator = DecelerateInterpolator()
        taglineAnimator.start()
    }
    
    private fun animateVersionInfo() {
        val alpha = ObjectAnimator.ofFloat(binding.versionInfo, "alpha", 0f, 1f)
        alpha.duration = 400
        alpha.interpolator = DecelerateInterpolator()
        alpha.start()
    }
    
    private fun animateDecorativeElements() {
        val elements = listOf(binding.decorative1, binding.decorative2, binding.decorative3)
        
        elements.forEachIndexed { index, element ->
            Handler(Looper.getMainLooper()).postDelayed({
                val alpha = ObjectAnimator.ofFloat(element, "alpha", 0f, 0.3f)
                val scaleX = ObjectAnimator.ofFloat(element, "scaleX", 0.8f, 1f)
                val scaleY = ObjectAnimator.ofFloat(element, "scaleY", 0.8f, 1f)
                
                val elementAnimator = AnimatorSet()
                elementAnimator.playTogether(alpha, scaleX, scaleY)
                elementAnimator.duration = 400
                elementAnimator.interpolator = OvershootInterpolator(1.1f)
                elementAnimator.start()
            }, (index * 200).toLong())
        }
    }
    
    private fun animateLoading() {
        val alpha = ObjectAnimator.ofFloat(binding.loading, "alpha", 0f, 1f)
        val loadingTextAlpha = ObjectAnimator.ofFloat(binding.loadingText, "alpha", 0f, 1f)
        
        val loadingAnimator = AnimatorSet()
        loadingAnimator.playTogether(alpha, loadingTextAlpha)
        loadingAnimator.duration = 500
        loadingAnimator.interpolator = DecelerateInterpolator()
        loadingAnimator.start()
        
        // Pulse animation for loading text
        val pulse = ObjectAnimator.ofFloat(binding.loadingText, "alpha", 0.8f, 1f, 0.8f)
        pulse.duration = 1000
        pulse.repeatCount = ValueAnimator.INFINITE
        pulse.start()
    }
    
    private fun navigateToNextScreen() {
        val intent = when {
            dataManager.isLoggedIn() -> {
                Intent(this, MainActivity::class.java)
            }
            shouldShowOnboarding() -> {
                Intent(this, OnboardingActivity::class.java)
            }
            else -> {
                Intent(this, AuthActivity::class.java)
            }
        }
        
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
    
    private fun shouldShowOnboarding(): Boolean {
        // Show onboarding if user hasn't completed it yet
        val prefs = dataManager.getUserPreferences()
        return !prefs.onboardingCompleted
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clear any pending handlers to prevent memory leaks
        Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null)
    }
}