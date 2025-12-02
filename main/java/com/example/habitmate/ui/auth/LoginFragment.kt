package com.example.habitmate.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.habitmate.R
import com.example.habitmate.data.DataManager
import com.example.habitmate.databinding.FragmentLoginBinding
import com.example.habitmate.ui.main.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        
        setupInputValidation()
        setupClickListeners()
        setupAnimations()
        loadRememberedCredentials()
    }
    
    private fun setupInputValidation() {
        // Email validation
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateEmail()
            }
        })
        
        // Password validation
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validatePassword()
            }
        })
    }
    
    private fun validateEmail(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val isValid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        
        binding.tilEmail.error = if (!isValid && email.isNotEmpty()) {
            "Please enter a valid email address"
        } else null
        
        return isValid
    }
    
    private fun validatePassword(): Boolean {
        val password = binding.etPassword.text.toString().trim()
        val isValid = password.length >= 6
        
        binding.tilPassword.error = if (!isValid && password.isNotEmpty()) {
            "Password must be at least 6 characters"
        } else null
        
        return isValid
    }
    
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            performLogin()
        }
        
        binding.tvForgotPassword.setOnClickListener {
            handleForgotPassword()
        }
        
        binding.btnGoogleLogin.setOnClickListener {
            performGoogleLogin()
        }
        
        binding.btnAppleLogin.setOnClickListener {
            performAppleLogin()
        }
    }
    
    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val rememberMe = binding.cbRememberMe.isChecked
        
        // Validate inputs
        if (!validateInputs(email, password)) {
            return
        }
        
        // Show loading state
        showLoading(true)
        hideError()
        
        // Simulate login process
        lifecycleScope.launch {
            try {
                // Simulate network delay
                delay(2000)
                
                // Check credentials (in real app, this would be API call)
                if (isValidCredentials(email, password)) {
                    // Save user data
                    val user = com.example.habitmate.data.User(
                        id = dataManager.generateId(),
                        name = email.substringBefore("@"),
                        email = email,
                        createdAt = System.currentTimeMillis()
                    )
                    dataManager.saveUser(user)
                    
                    // Save remember me preference
                    if (rememberMe) {
                        saveCredentials(email, password)
                    } else {
                        clearSavedCredentials()
                    }
                    
                    // Navigate to main activity
                    navigateToMain()
                } else {
                    showError("Invalid email or password")
                }
            } catch (e: Exception) {
                showError("Login failed. Please try again.")
            } finally {
                showLoading(false)
            }
        }
    }
    
    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true
        
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Please enter a valid email address"
            isValid = false
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        }
        
        return isValid
    }
    
    private fun isValidCredentials(email: String, password: String): Boolean {
        // In a real app, this would validate against a server
        // For demo purposes, accept any email/password combination
        return email.isNotEmpty() && password.isNotEmpty()
    }
    
    private fun handleForgotPassword() {
        val email = binding.etEmail.text.toString().trim()
        
        if (email.isEmpty()) {
            binding.tilEmail.error = "Please enter your email first"
            binding.etEmail.requestFocus()
            return
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Please enter a valid email address"
            binding.etEmail.requestFocus()
            return
        }
        
        // Show success message
        Toast.makeText(requireContext(), "Password reset link sent to $email", Toast.LENGTH_LONG).show()
    }
    
    private fun performGoogleLogin() {
        Toast.makeText(requireContext(), "Google login coming soon!", Toast.LENGTH_SHORT).show()
    }
    
    private fun performAppleLogin() {
        Toast.makeText(requireContext(), "Apple login coming soon!", Toast.LENGTH_SHORT).show()
    }
    
    private fun showLoading(show: Boolean) {
        binding.loadingIndicator.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !show
        binding.btnGoogleLogin.isEnabled = !show
        binding.btnAppleLogin.isEnabled = !show
    }
    
    private fun showError(message: String) {
        binding.tvErrorMessage.text = message
        binding.errorCard.visibility = View.VISIBLE
        
        // Animate error card appearance
        val alpha = ObjectAnimator.ofFloat(binding.errorCard, "alpha", 0f, 1f)
        val scaleX = ObjectAnimator.ofFloat(binding.errorCard, "scaleX", 0.9f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.errorCard, "scaleY", 0.9f, 1f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alpha, scaleX, scaleY)
        animatorSet.duration = 300
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.start()
    }
    
    private fun hideError() {
        binding.errorCard.visibility = View.GONE
    }
    
    private fun navigateToMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
    
    private fun saveCredentials(email: String, password: String) {
        val prefs = dataManager.getUserPreferences()
        dataManager.saveUserPreferences(prefs.copy(biometricEnabled = true))
        
        // In a real app, you would encrypt and save credentials securely
        // For demo purposes, we'll just save the email
        val prefsFile = requireContext().getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
        prefsFile.edit()
            .putString("saved_email", email)
            .putBoolean("remember_me", true)
            .apply()
    }
    
    private fun clearSavedCredentials() {
        val prefsFile = requireContext().getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
        prefsFile.edit()
            .remove("saved_email")
            .putBoolean("remember_me", false)
            .apply()
    }
    
    private fun loadRememberedCredentials() {
        val prefsFile = requireContext().getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
        val savedEmail = prefsFile.getString("saved_email", "")
        val rememberMe = prefsFile.getBoolean("remember_me", false)
        
        if (rememberMe && !savedEmail.isNullOrEmpty()) {
            binding.etEmail.setText(savedEmail)
            binding.cbRememberMe.isChecked = true
        }
    }
    
    private fun setupAnimations() {
        // Initial state
        binding.loginCard.alpha = 0f
        binding.loginCard.translationY = 50f
        
        // Animate card appearance
        val alpha = ObjectAnimator.ofFloat(binding.loginCard, "alpha", 0f, 1f)
        val translationY = ObjectAnimator.ofFloat(binding.loginCard, "translationY", 50f, 0f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alpha, translationY)
        animatorSet.duration = 600
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.startDelay = 200
        animatorSet.start()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

