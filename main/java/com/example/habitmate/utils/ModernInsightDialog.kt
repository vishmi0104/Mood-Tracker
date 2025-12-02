package com.example.habitmate.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.habitmate.R
import com.google.android.material.card.MaterialCardView
import java.util.*

class ModernInsightDialog private constructor(
    private val context: Context,
    private val title: String,
    private val message: String,
    private val iconRes: Int
) {
    
    private var positiveButtonText: String = "OK"
    private var negativeButtonText: String = "Close"
    private var positiveButtonListener: (() -> Unit)? = null
    private var negativeButtonListener: (() -> Unit)? = null
    private var stats: Map<String, String> = emptyMap()
    private var distribution: Map<String, Pair<String, Int>> = emptyMap() // emoji, count
    
    class Builder(private val context: Context) {
        private var title: String = ""
        private var message: String = ""
        private var iconRes: Int = R.drawable.ic_mood
        private var positiveButtonText: String = "OK"
        private var negativeButtonText: String = "Close"
        private var positiveButtonListener: (() -> Unit)? = null
        private var negativeButtonListener: (() -> Unit)? = null
        private var stats: Map<String, String> = emptyMap()
        private var distribution: Map<String, Pair<String, Int>> = emptyMap()
        
        fun setTitle(title: String) = apply { this.title = title }
        fun setMessage(message: String) = apply { this.message = message }
        fun setIcon(iconRes: Int) = apply { this.iconRes = iconRes }
        fun setPositiveButton(text: String, listener: (() -> Unit)? = null) = apply { 
            this.positiveButtonText = text
            this.positiveButtonListener = listener
        }
        fun setNegativeButton(text: String, listener: (() -> Unit)? = null) = apply { 
            this.negativeButtonText = text
            this.negativeButtonListener = listener
        }
        fun setStats(stats: Map<String, String>) = apply { this.stats = stats }
        fun setDistribution(distribution: Map<String, Pair<String, Int>>) = apply { this.distribution = distribution }
        
        fun build(): ModernInsightDialog {
            return ModernInsightDialog(context, title, message, iconRes).apply {
                this.positiveButtonText = this@Builder.positiveButtonText
                this.negativeButtonText = this@Builder.negativeButtonText
                this.positiveButtonListener = this@Builder.positiveButtonListener
                this.negativeButtonListener = this@Builder.negativeButtonListener
                this.stats = this@Builder.stats
                this.distribution = this@Builder.distribution
            }
        }
        
        fun show() {
            build().show()
        }
    }
    
    fun show() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_insights_modern, null)
        
        val titleTextView = dialogView.findViewById<TextView>(R.id.tv_insight_title)
        val iconImageView = dialogView.findViewById<ImageView>(R.id.iv_insight_icon)
        val messageTextView = dialogView.findViewById<TextView>(R.id.tv_insight_message)
        val statsContainer = dialogView.findViewById<LinearLayout>(R.id.stats_container)
        val statsGrid = dialogView.findViewById<GridLayout>(R.id.stats_grid)
        val distributionContainer = dialogView.findViewById<LinearLayout>(R.id.distribution_container)
        val positiveButton = dialogView.findViewById<Button>(R.id.btn_positive)
        val negativeButton = dialogView.findViewById<Button>(R.id.btn_negative)
        
        // Set basic content
        titleTextView.text = title
        iconImageView.setImageResource(iconRes)
        messageTextView.text = message
        
        // Set up stats if provided
        if (stats.isNotEmpty()) {
            statsContainer.visibility = View.VISIBLE
            statsGrid.removeAllViews()
            
            // Add stats to grid
            var index = 0
            stats.forEach { (label, value) ->
                val statView = LayoutInflater.from(context).inflate(R.layout.item_stat, statsGrid, false) as MaterialCardView
                val labelTextView = statView.findViewById<TextView>(R.id.tv_stat_label)
                val valueTextView = statView.findViewById<TextView>(R.id.tv_stat_value)
                
                labelTextView.text = label
                valueTextView.text = value
                
                val layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(index % 2, 1f)
                    setMargins(4, 4, 4, 4)
                }
                
                statsGrid.addView(statView, layoutParams)
                index++
            }
        }
        
        // Set up distribution if provided
        if (distribution.isNotEmpty()) {
            distributionContainer.visibility = View.VISIBLE
            
            // Clear existing views except the title
            distributionContainer.removeViews(1, distributionContainer.childCount - 1)
            
            // Add distribution items
            distribution.forEach { (label, pair) ->
                val (emoji, count) = pair
                val distributionView = LayoutInflater.from(context).inflate(R.layout.item_distribution, distributionContainer, false)
                val emojiTextView = distributionView.findViewById<TextView>(R.id.tv_distribution_emoji)
                val labelTextView = distributionView.findViewById<TextView>(R.id.tv_distribution_label)
                val countTextView = distributionView.findViewById<TextView>(R.id.tv_distribution_value)
                
                emojiTextView.text = emoji
                labelTextView.text = label
                countTextView.text = count.toString()
                
                distributionContainer.addView(distributionView)
            }
        }
        
        // Create and show dialog
        val dialog = AlertDialog.Builder(context, R.style.ModernDialogStyle)
            .setView(dialogView)
            .create()
        
        // Set up buttons
        positiveButton.text = positiveButtonText
        negativeButton.text = negativeButtonText
        
        positiveButton.setOnClickListener {
            positiveButtonListener?.invoke()
            // Dismiss the dialog
            dialog.dismiss()
        }
        
        negativeButton.setOnClickListener {
            negativeButtonListener?.invoke()
            // Dismiss the dialog
            dialog.dismiss()
        }
        
        // Make dialog width match parent with some margins
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        dialog.show()
    }
}