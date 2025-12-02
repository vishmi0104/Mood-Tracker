package com.example.habitmate.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.habitmate.R

class OnboardingFragment : Fragment() {
    
    private var titleRes: Int = 0
    private var descriptionRes: Int = 0
    private var imageRes: Int = 0
    
    companion object {
        fun newInstance(titleRes: Int, descriptionRes: Int, imageRes: Int): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle()
            args.putInt("title", titleRes)
            args.putInt("description", descriptionRes)
            args.putInt("image", imageRes)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            titleRes = it.getInt("title")
            descriptionRes = it.getInt("description")
            imageRes = it.getInt("image")
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val titleText = view.findViewById<TextView>(R.id.title)
        val descriptionText = view.findViewById<TextView>(R.id.description)
        val imageView = view.findViewById<ImageView>(R.id.image)
        val getStartedButton = view.findViewById<Button>(R.id.btn_get_started)
        val skipButton = view.findViewById<Button>(R.id.btn_skip)
        
        titleText.text = getString(titleRes)
        descriptionText.text = getString(descriptionRes)
        imageView.setImageResource(imageRes)
        
        // Show "Get Started" button only on last page
        val isLastPage = titleRes == R.string.onboarding_title_3
        getStartedButton.visibility = if (isLastPage) View.VISIBLE else View.GONE
        skipButton.visibility = if (isLastPage) View.GONE else View.VISIBLE
        
        getStartedButton.setOnClickListener {
            (activity as? OnboardingActivity)?.navigateToAuth()
        }
        
        skipButton.setOnClickListener {
            (activity as? OnboardingActivity)?.navigateToAuth()
        }
    }
}

