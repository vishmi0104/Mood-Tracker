package com.example.habitmate.ui.auth

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.habitmate.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputLayout

class RegisterFragment : Fragment() {
    
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var googleRegisterButton: Button
    private lateinit var appleRegisterButton: Button
    private lateinit var errorCard: MaterialCardView
    private lateinit var errorMessage: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        nameEditText = view.findViewById(R.id.et_name)
        emailEditText = view.findViewById(R.id.et_email)
        passwordEditText = view.findViewById(R.id.et_password)
        confirmPasswordEditText = view.findViewById(R.id.et_confirm_password)
        registerButton = view.findViewById(R.id.btn_register)
        googleRegisterButton = view.findViewById(R.id.btn_google_register)
        appleRegisterButton = view.findViewById(R.id.btn_apple_register)
        errorCard = view.findViewById(R.id.error_card)
        errorMessage = view.findViewById(R.id.tv_error_message)
        loadingIndicator = view.findViewById(R.id.loading_indicator)
        nameInputLayout = view.findViewById(R.id.til_name)
        emailInputLayout = view.findViewById(R.id.til_email)
        passwordInputLayout = view.findViewById(R.id.til_password)
        confirmPasswordInputLayout = view.findViewById(R.id.til_confirm_password)
        
        // Set up click listeners
        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            
            // Clear previous errors
            clearErrors()
            
            // Validate input
            if (validateInput(name, email, password, confirmPassword)) {
                showLoading(true)
                (activity as? AuthActivity)?.register(name, email, password, confirmPassword)
            }
        }
        
        googleRegisterButton.setOnClickListener {
            // Handle Google registration
            showError("Google registration not implemented yet")
        }
        
        appleRegisterButton.setOnClickListener {
            // Handle Apple registration
            showError("Apple registration not implemented yet")
        }
    }
    
    private fun validateInput(name: String, email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true
        
        if (TextUtils.isEmpty(name)) {
            nameInputLayout.error = "Name is required"
            isValid = false
        }
        
        if (TextUtils.isEmpty(email)) {
            emailInputLayout.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Please enter a valid email"
            isValid = false
        }
        
        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordInputLayout.error = "Password must be at least 6 characters"
            isValid = false
        }
        
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInputLayout.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordInputLayout.error = "Passwords do not match"
            isValid = false
        }
        
        return isValid
    }
    
    private fun clearErrors() {
        nameInputLayout.error = null
        emailInputLayout.error = null
        passwordInputLayout.error = null
        confirmPasswordInputLayout.error = null
        errorCard.visibility = View.GONE
    }
    
    private fun showError(message: String) {
        errorMessage.text = message
        errorCard.visibility = View.VISIBLE
        showLoading(false)
    }
    
    private fun showLoading(show: Boolean) {
        loadingIndicator.visibility = if (show) View.VISIBLE else View.GONE
        registerButton.isEnabled = !show
        googleRegisterButton.isEnabled = !show
        appleRegisterButton.isEnabled = !show
    }
    
    fun onRegistrationFailed(message: String) {
        showError(message)
    }
    
    fun onRegistrationSuccess() {
        showLoading(false)
    }
}