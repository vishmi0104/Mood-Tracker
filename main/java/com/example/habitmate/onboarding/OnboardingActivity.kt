package com.example.habitmate.onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.habitmate.R
import com.example.habitmate.data.DataManager
import com.example.habitmate.databinding.ActivityOnboardingBinding
import com.example.habitmate.ui.auth.AuthActivity

class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var dataManager: DataManager
    private lateinit var onboardingAdapter: OnboardingAdapter
    
    private val onboardingPages = listOf(
        OnboardingPage(
            title = "Track Better Habits",
            description = "Build healthy routines with our intuitive habit tracker. Set daily goals, track progress, and celebrate your achievements.",
            layoutRes = R.layout.fragment_onboarding_habits
        ),
        OnboardingPage(
            title = "Understand Your Mood",
            description = "Track your emotional well-being with our mood journal. Log daily feelings, identify patterns, and gain insights into your mental health.",
            layoutRes = R.layout.fragment_onboarding_mood
        ),
        OnboardingPage(
            title = "Stay Hydrated",
            description = "Maintain optimal hydration with smart reminders and progress tracking. Set daily goals and build healthy drinking habits.",
            layoutRes = R.layout.fragment_onboarding_hydration
        )
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        dataManager = DataManager(this)
        
        setupViewPager()
        setupPageIndicators()
        setupNavigationButtons()
        setupAnimations()
    }
    
    private fun setupViewPager() {
        onboardingAdapter = OnboardingAdapter(onboardingPages)
        binding.viewPager.adapter = onboardingAdapter
        
        // Disable user swiping for better control
        binding.viewPager.isUserInputEnabled = false
        
        // Page change callback
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updatePageIndicators(position)
                updateNavigationButtons(position)
                updateProgressIndicator(position)
            }
        })
    }
    
    private fun setupPageIndicators() {
        // Create page indicators
        for (i in onboardingPages.indices) {
            val indicator = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.indicator_size),
                    resources.getDimensionPixelSize(R.dimen.indicator_size)
                ).apply {
                    marginEnd = if (i < onboardingPages.size - 1) {
                        resources.getDimensionPixelSize(R.dimen.indicator_margin)
                    } else 0
                }
                setBackgroundResource(R.drawable.indicator_unselected)
            }
            binding.pageIndicators.addView(indicator)
        }
        
        // Set first indicator as selected
        updatePageIndicators(0)
    }
    
    private fun updatePageIndicators(currentPosition: Int) {
        for (i in 0 until binding.pageIndicators.childCount) {
            val indicator = binding.pageIndicators.getChildAt(i)
            indicator.setBackgroundResource(
                if (i == currentPosition) R.drawable.indicator_selected else R.drawable.indicator_unselected
            )
        }
    }
    
    private fun setupNavigationButtons() {
        binding.btnSkip.setOnClickListener {
            navigateToAuth()
        }
        
        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < onboardingPages.size - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                navigateToAuth()
            }
        }
        
        updateNavigationButtons(0)
    }
    
    private fun updateNavigationButtons(position: Int) {
        val isLastPage = position == onboardingPages.size - 1
        
        // Update skip button visibility
        binding.btnSkip.visibility = if (isLastPage) View.GONE else View.VISIBLE
        
        // Update next button text and icon
        binding.btnNext.text = if (isLastPage) "Get Started" else "Next"
        binding.btnNext.setIconResource(
            if (isLastPage) R.drawable.ic_add else R.drawable.ic_add
        )
    }
    
    private fun updateProgressIndicator(position: Int) {
        val progress = ((position + 1) * 100) / onboardingPages.size
        binding.progressIndicator.progress = progress
    }
    
    private fun setupAnimations() {
        // Initial state
        binding.pageIndicators.alpha = 0f
        binding.progressIndicator.alpha = 0f
        binding.bottomContainer.alpha = 0f
        binding.bottomContainer.translationY = 100f
        
        // Animate in
        val indicatorsAlpha = ObjectAnimator.ofFloat(binding.pageIndicators, "alpha", 0f, 1f)
        val progressAlpha = ObjectAnimator.ofFloat(binding.progressIndicator, "alpha", 0f, 1f)
        val containerAlpha = ObjectAnimator.ofFloat(binding.bottomContainer, "alpha", 0f, 1f)
        val containerTranslation = ObjectAnimator.ofFloat(binding.bottomContainer, "translationY", 100f, 0f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(indicatorsAlpha, progressAlpha, containerAlpha, containerTranslation)
        animatorSet.duration = 800
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.startDelay = 500
        animatorSet.start()
    }
    
    internal fun navigateToAuth() {
        // Mark onboarding as completed by updating user preferences
        val preferences = dataManager.getUserPreferences()
        dataManager.saveUserPreferences(
            preferences.copy(
                onboardingCompleted = true
            )
        )
        
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
    
    override fun onBackPressed() {
        val currentItem = binding.viewPager.currentItem
        if (currentItem > 0) {
            binding.viewPager.currentItem = currentItem - 1
        } else {
            super.onBackPressed()
        }
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val layoutRes: Int
)