package com.example.habitmate.ui.hydration

import android.app.AlertDialog
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habitmate.R
import com.example.habitmate.data.DataManager
import com.example.habitmate.data.HydrationData
import com.example.habitmate.ui.hydration.HydrationReminderWorker
import com.example.habitmate.ui.main.MainActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class HydrationFragment : Fragment() {
    
    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001
    }
    
    private lateinit var dailyGoalTextView: TextView
    private lateinit var dailyProgress: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var customIntakeEditText: EditText
    private lateinit var addIntakeButton: Button
    private lateinit var goalSeekBar: SeekBar
    private lateinit var goalTextView: TextView
    private lateinit var reminderIntervalSeekBar: SeekBar
    private lateinit var reminderIntervalTextView: TextView
    private lateinit var reminderTimeButton: Button
    private lateinit var intakeHistoryRecyclerView: RecyclerView
    private lateinit var emptyHistoryTextView: TextView
    private lateinit var resetButton: Button
    private lateinit var addGlassFab: FloatingActionButton
    private lateinit var hydrationChart: BarChart
    private lateinit var chartCard: MaterialCardView
    private lateinit var oceanHydrationInsightsButton: Button
    private lateinit var oceanHydrationTipsButton: Button
    private lateinit var gradientOceanPinkButton: Button
    
    private lateinit var dataManager: DataManager
    private lateinit var intakeHistoryAdapter: IntakeHistoryAdapter
    private val intakeHistory = mutableListOf<WaterIntake>()
    private val predefinedIntakes = listOf(100, 150, 250, 330, 500)
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hydration, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        
        dailyGoalTextView = view.findViewById(R.id.tv_daily_goal)
        dailyProgress = view.findViewById(R.id.progress_daily)
        progressText = view.findViewById(R.id.tv_progress_text)
        customIntakeEditText = view.findViewById(R.id.et_custom_intake)
        addIntakeButton = view.findViewById(R.id.btn_add_intake)
        goalSeekBar = view.findViewById(R.id.seekbar_goal)
        goalTextView = view.findViewById(R.id.tv_goal)
        reminderIntervalSeekBar = view.findViewById(R.id.seekbar_reminder_interval)
        reminderIntervalTextView = view.findViewById(R.id.tv_reminder_interval)
        reminderTimeButton = view.findViewById(R.id.btn_reminder_time)
        intakeHistoryRecyclerView = view.findViewById(R.id.rv_intake_history)
        emptyHistoryTextView = view.findViewById(R.id.tv_empty_history)
        resetButton = view.findViewById(R.id.btn_reset)
        addGlassFab = view.findViewById(R.id.fab_add_glass)
        hydrationChart = view.findViewById(R.id.hydration_chart)
        chartCard = view.findViewById(R.id.chart_card)
        oceanHydrationInsightsButton = view.findViewById(R.id.btn_ocean_hydration_insights)
        oceanHydrationTipsButton = view.findViewById(R.id.btn_ocean_hydration_tips)
        gradientOceanPinkButton = view.findViewById(R.id.btn_gradient_ocean_pink)
        
        setupRecyclerView()
        setupChart()
        setupSeekBars()
        setupButtons()
        setupOceanButtons()
        setupGradientButton()
        loadHydrationData()
        updateChart()
    }
    
    private fun setupOceanButtons() {
        oceanHydrationInsightsButton.setOnClickListener {
            showHydrationInsights()
        }
        
        oceanHydrationTipsButton.setOnClickListener {
            showHydrationTips()
        }
    }
    
    private fun setupGradientButton() {
        gradientOceanPinkButton.setOnClickListener {
            // Show hydration motivation tips
            val motivationTips = listOf(
                "Your body is 60% water - keep it hydrated!",
                "Proper hydration boosts energy and brain function.",
                "Clear urine indicates good hydration.",
                "Drink water before you feel thirsty.",
                "Water helps transport nutrients throughout your body.",
                "Even mild dehydration can affect mood and concentration.",
                "Drink a glass of water with each meal.",
                "Carry a water bottle to sip throughout the day.",
                "Add fruit for natural flavor if plain water is boring.",
                "Hydration is key to healthy skin and kidney function."
            )
            
            val randomTip = motivationTips.random()
            
            AlertDialog.Builder(requireContext())
                .setTitle("🌊 Ocean Hydration Boost 🌸")
                .setMessage("${randomTip}\n\nStaying hydrated is one of the simplest yet most powerful things you can do for your health. Every sip counts toward your wellness goals!")
                .setPositiveButton("Hydrate Now!") { _, _ ->
                    // Add a glass of water
                    addSpecificIntake(250) // Add 250ml
                    Toast.makeText(context, "Hydration boost activated! 💧", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Later", null)
                .show()
        }
    }
    
    private fun showHydrationInsights() {
        val hydrationData = dataManager.getHydrationData()
        val weeklyData = getWeeklyHydrationData()
        
        val avgDailyIntake = weeklyData.values.average()
        val bestDay = weeklyData.maxByOrNull { it.value }?.key ?: "N/A"
        val consistency = weeklyData.filter { it.value > 0 }.size
        
        // Prepare stats for modern dialog
        val stats = mapOf(
            "Daily Goal" to "${hydrationData.goal} glasses",
            "Current Intake" to "${hydrationData.glassesToday} glasses",
            "Average Daily Intake" to "${String.format("%.1f", avgDailyIntake)} glasses",
            "Best Hydration Day" to "$bestDay",
            "Consistency (7 days)" to "$consistency/7 days"
        )
        
        val message = "Stay hydrated and track your water intake to maintain optimal health and energy levels throughout the day."
        
        com.example.habitmate.utils.ModernInsightDialog.Builder(requireContext())
            .setTitle("💧 Hydration Insights")
            .setMessage(message)
            .setIcon(R.drawable.ic_water)
            .setStats(stats)
            .setPositiveButton("OK")
            .show()
    }
    
    private fun showHydrationTips() {
        val tips = listOf(
            "Start your day with a glass of water to kickstart hydration.",
            "Carry a reusable water bottle to track your intake throughout the day.",
            "Flavor your water with lemon, cucumber, or mint for better taste.",
            "Set hourly reminders to drink water during work hours.",
            "Drink water before meals to aid digestion and stay hydrated.",
            "Eat water-rich foods like watermelon, cucumber, and oranges.",
            "Replace one sugary drink per day with water for better health."
        )
        
        val randomTip = tips.random()
        
        AlertDialog.Builder(requireContext())
            .setTitle("💧 Ocean Hydration Tips")
            .setMessage(randomTip)
            .setPositiveButton("Done") { _, _ ->
                Toast.makeText(context, "Hydration tip completed!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Skip", null)
            .show()
    }
    
    private fun setupRecyclerView() {
        intakeHistoryAdapter = IntakeHistoryAdapter(intakeHistory)
        intakeHistoryRecyclerView.layoutManager = LinearLayoutManager(context)
        intakeHistoryRecyclerView.adapter = intakeHistoryAdapter
    }
    
    private fun setupChart() {
        hydrationChart.description.isEnabled = false
        hydrationChart.setTouchEnabled(true)
        hydrationChart.isDragEnabled = true
        hydrationChart.setScaleEnabled(true)
        hydrationChart.setPinchZoom(true)
        
        val xAxis = hydrationChart.xAxis
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        
        val leftAxis = hydrationChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.axisMinimum = 0f
        
        hydrationChart.axisRight.isEnabled = false
        hydrationChart.legend.isEnabled = false
    }
    
    private fun setupSeekBars() {
        goalSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val goal = progress + 1
                    updateGoalText(goal)
                    dailyGoalTextView.text = "$goal glasses"
                    updateProgress()
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress ?: 0
                val goal = progress + 1
                val hydrationData = dataManager.getHydrationData()
                updateHydrationData { it.copy(goal = goal) }
                updateChart()
            }
        })
        
        reminderIntervalSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val interval = (progress + 1) * 30 // 30, 60, 90, 120 minutes
                    val hours = interval / 60
                    val minutes = interval % 60
                    val intervalText = if (hours > 0) {
                        if (minutes > 0) "$hours h $minutes m" else "$hours h"
                    } else {
                        "$minutes m"
                    }
                    reminderIntervalTextView.text = "Reminder: Every $intervalText"
                    updateHydrationData { it.copy(reminderInterval = interval) }
                    scheduleReminder(interval)
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    
    private fun setupButtons() {
        addIntakeButton.setOnClickListener {
            addCustomIntake()
        }
        
        addGlassFab.setOnClickListener {
            showIntakeOptions()
        }
        
        reminderTimeButton.setOnClickListener {
            showTimePickerDialog()
        }
        
        resetButton.setOnClickListener {
            resetHydrationData()
        }
    }
    
    private fun showIntakeOptions() {
        val options = mutableListOf<String>()
        options.add("Custom Intake (${customIntakeEditText.text} ml)")
        predefinedIntakes.forEach { intake ->
            options.add("$intake ml")
        }
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, options)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Add Water Intake")
            .setAdapter(adapter) { _, which ->
                when (which) {
                    0 -> {
                        // Custom intake
                        val intakeText = customIntakeEditText.text.toString()
                        if (intakeText.isNotEmpty()) {
                            try {
                                val customIntake = intakeText.toInt()
                                if (customIntake > 0) {
                                    addSpecificIntake(customIntake)
                                } else {
                                    Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: NumberFormatException) {
                                Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> {
                        // Predefined intake
                        val intake = predefinedIntakes[which - 1]
                        addSpecificIntake(intake)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun addSpecificIntake(amountMl: Int) {
        val hydrationData = dataManager.getHydrationData()
        val glassesToAdd = Math.ceil(amountMl.toDouble() / hydrationData.customIntakeMl).toInt()
        val newGlasses = hydrationData.glassesToday + glassesToAdd
        val totalMl = newGlasses * hydrationData.customIntakeMl
        val goalMl = hydrationData.goal * hydrationData.customIntakeMl
        
        updateHydrationData { it.copy(glassesToday = newGlasses, lastGlassTime = System.currentTimeMillis()) }
        
        // Add to intake history
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        intakeHistory.add(0, WaterIntake(amountMl, currentTime))
        intakeHistoryAdapter.notifyDataSetChanged()
        updateEmptyState()
        
        updateProgress()
        
        if (totalMl >= goalMl) {
            showGoalAchievedNotification(goalMl)
        }
        
        updateChart()
        Toast.makeText(context, "Added $amountMl ml!", Toast.LENGTH_SHORT).show()
    }
    
    private fun addCustomIntake() {
        val intakeText = customIntakeEditText.text.toString()
        if (intakeText.isNotEmpty()) {
            try {
                val customIntake = intakeText.toInt()
                if (customIntake > 0) {
                    val hydrationData = dataManager.getHydrationData()
                    val glassesToAdd = Math.ceil(customIntake.toDouble() / hydrationData.customIntakeMl).toInt()
                    val newGlasses = hydrationData.glassesToday + glassesToAdd
                    val totalMl = newGlasses * hydrationData.customIntakeMl
                    val goalMl = hydrationData.goal * hydrationData.customIntakeMl
                    
                    updateHydrationData { it.copy(glassesToday = newGlasses, lastGlassTime = System.currentTimeMillis()) }
                    
                    // Add to intake history
                    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    intakeHistory.add(0, WaterIntake(customIntake, currentTime))
                    intakeHistoryAdapter.notifyDataSetChanged()
                    updateEmptyState()
                    
                    updateProgress()
                    
                    if (totalMl >= goalMl) {
                        showGoalAchievedNotification(goalMl)
                    }
                    
                    updateChart()
                    Toast.makeText(context, "Added $customIntake ml!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadHydrationData() {
        val hydrationData = dataManager.getHydrationData()
        
        // Update daily goal display
        dailyGoalTextView.text = "${hydrationData.goal} glasses"
        
        // Update progress
        updateProgress()
        
        // Set custom intake value
        customIntakeEditText.setText(hydrationData.customIntakeMl.toString())
        
        goalSeekBar.progress = hydrationData.goal - 1
        updateGoalText(hydrationData.goal)
        
        val intervalProgress = (hydrationData.reminderInterval / 30) - 1
        reminderIntervalSeekBar.progress = intervalProgress.coerceAtLeast(0)
        
        val hours = hydrationData.reminderInterval / 60
        val minutes = hydrationData.reminderInterval % 60
        val intervalText = if (hours > 0) {
            if (minutes > 0) "$hours h $minutes m" else "$hours h"
        } else {
            "$minutes m"
        }
        reminderIntervalTextView.text = "Reminder: Every $intervalText"
        
        // Load intake history
        loadIntakeHistory()
    }
    
    private fun updateGoalText(goal: Int) {
        goalTextView.text = "Goal: $goal glasses"
    }
    
    private fun updateProgress() {
        val hydrationData = dataManager.getHydrationData()
        val totalMl = hydrationData.glassesToday * hydrationData.customIntakeMl
        val goalMl = hydrationData.goal * hydrationData.customIntakeMl
        val progressPercentage = if (goalMl > 0) (totalMl.toFloat() / goalMl * 100).toInt() else 0
        
        dailyProgress.progress = progressPercentage
        progressText.text = "$totalMl ml / $goalMl ml ($progressPercentage%)"
    }
    
    private fun showGoalAchievedNotification(goalMl: Int) {
        // Show toast message
        Toast.makeText(context, "🎉 Congratulations! You've reached your daily goal of $goalMl ml!", Toast.LENGTH_LONG).show()
        
        // Show proper notification
        showGoalNotification(goalMl)
    }
    
    private fun showGoalNotification(goalMl: Int) {
        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "hydration_goal_achieved",
                "Hydration Goal Achieved",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when you've reached your daily hydration goal"
                enableLights(true)
                lightColor = resources.getColor(R.color.pink_primary, null)
                enableVibration(true)
            }
            
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        
        // Create an intent to open the app when the notification is tapped
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(requireContext(), "hydration_goal_achieved")
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("🏆 Goal Achieved!")
            .setContentText("Congratulations! You've reached your daily goal of $goalMl ml")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1002, notification) // Use different ID than reminder notification
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
    }
    
    private fun updateHydrationData(update: (HydrationData) -> HydrationData) {
        val currentData = dataManager.getHydrationData()
        val updatedData = update(currentData)
        dataManager.saveHydrationData(updatedData)
    }
    
    private fun scheduleReminder(intervalMinutes: Int) {
        HydrationReminderWorker.scheduleReminder(requireContext(), intervalMinutes)
    }
    
    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        
        TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
                reminderTimeButton.text = "Reminder Time: $timeString"
                
                // Schedule reminder for specific time
                scheduleExactTimeReminder(selectedHour, selectedMinute)
            },
            hour,
            minute,
            true
        ).show()
    }
    
    private fun scheduleExactTimeReminder(hour: Int, minute: Int) {
        // Check if we have permission to schedule exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Request permission to schedule exact alarms
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intent.data = Uri.parse("package:${requireContext().packageName}")
                    startActivity(intent)
                }
                Toast.makeText(context, "Please grant permission to schedule exact alarms", Toast.LENGTH_LONG).show()
                return
            }
        }
        
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        
        // If the time is in the past, set it for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        // Cancel any existing alarm
        alarmManager.cancel(pendingIntent)
        
        // Set the exact alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
        
        Toast.makeText(context, "Exact time reminder set for $hour:$minute", Toast.LENGTH_SHORT).show()
    }
    
    private fun calculateDelayMinutes(hour: Int, minute: Int): Int {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        
        val currentTotalMinutes = currentHour * 60 + currentMinute
        val targetTotalMinutes = hour * 60 + minute
        
        return if (targetTotalMinutes > currentTotalMinutes) {
            targetTotalMinutes - currentTotalMinutes
        } else {
            (24 * 60) - currentTotalMinutes + targetTotalMinutes
        }
    }
    
    private fun loadIntakeHistory() {
        // In a real app, you would load this from a database
        // For now, we'll just clear the list
        intakeHistory.clear()
        intakeHistoryAdapter.notifyDataSetChanged()
        updateEmptyState()
    }
    
    private fun updateEmptyState() {
        emptyHistoryTextView.visibility = if (intakeHistory.isEmpty()) View.VISIBLE else View.GONE
        intakeHistoryRecyclerView.visibility = if (intakeHistory.isEmpty()) View.GONE else View.VISIBLE
    }
    
    private fun updateChart() {
        val hydrationData = dataManager.getHydrationData()
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        
        // Show last 7 days of hydration data
        val last7Days = getLast7Days()
        last7Days.forEachIndexed { index, date ->
            // For demo purposes, we'll use random data
            // In a real app, you'd store daily hydration data
            val glasses = if (date == dataManager.getCurrentDate()) {
                hydrationData.glassesToday
            } else {
                (1..hydrationData.goal).random() // Random data for previous days
            }
            
            // Convert to ml for the chart
            val ml = glasses * hydrationData.customIntakeMl
            entries.add(BarEntry(index.toFloat(), ml.toFloat()))
            labels.add(SimpleDateFormat("MMM dd", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)!!))
        }
        
        val dataSet = BarDataSet(entries, "Water Intake (ml)")
        dataSet.color = resources.getColor(R.color.pink_primary, null)
        dataSet.setDrawValues(true)
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = resources.getColor(R.color.text_primary, null)
        
        val barData = BarData(dataSet)
        barData.barWidth = 0.8f
        hydrationChart.data = barData
        
        hydrationChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        hydrationChart.axisLeft.axisMinimum = 0f
        hydrationChart.axisLeft.granularity = hydrationData.customIntakeMl.toFloat() // Show increments based on custom intake
        hydrationChart.invalidate()
    }
    
    private fun getLast7Days(): List<String> {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            dates.add(dateFormat.format(calendar.time))
        }
        
        return dates
    }
    
    private fun resetHydrationData() {
        AlertDialog.Builder(requireContext())
            .setTitle("Reset Hydration Data")
            .setMessage("Are you sure you want to reset your today's hydration progress?")
            .setPositiveButton("Reset") { _, _ ->
                updateHydrationData { it.copy(glassesToday = 0, lastGlassTime = 0) }
                
                // Clear intake history
                intakeHistory.clear()
                intakeHistoryAdapter.notifyDataSetChanged()
                updateEmptyState()
                
                updateProgress()
                updateChart()
                
                Toast.makeText(context, "Hydration data reset!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun getWeeklyHydrationData(): Map<String, Int> {
        val weeklyData = mutableMapOf<String, Int>()
        val last7Days = getLast7Days()
        
        last7Days.forEach { date ->
            val glasses = if (date == dataManager.getCurrentDate()) {
                dataManager.getHydrationData().glassesToday
            } else {
                (1..dataManager.getHydrationData().goal).random() // Random data for previous days
            }
            weeklyData[date] = glasses
        }
        
        return weeklyData
    }
    
    data class WaterIntake(
        val amountMl: Int,
        val time: String
    )
    
    inner class IntakeHistoryAdapter(
        private val intakes: List<WaterIntake>
    ) : RecyclerView.Adapter<IntakeHistoryAdapter.IntakeViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntakeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_intake_entry, parent, false)
            return IntakeViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: IntakeViewHolder, position: Int) {
            holder.bind(intakes[position])
        }
        
        override fun getItemCount(): Int = intakes.size
        
        inner class IntakeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val amountTextView = itemView.findViewById<TextView>(R.id.tv_intake_amount)
            private val timeTextView = itemView.findViewById<TextView>(R.id.tv_intake_time)
            
            fun bind(intake: WaterIntake) {
                amountTextView.text = "${intake.amountMl} ml"
                timeTextView.text = intake.time
            }
        }
    }
}
