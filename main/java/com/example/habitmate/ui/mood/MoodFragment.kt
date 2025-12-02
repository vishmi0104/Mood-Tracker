package com.example.habitmate.ui.mood

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.habitmate.R
import com.example.habitmate.data.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class MoodFragment : Fragment() {
    
    private lateinit var moodRecyclerView: RecyclerView
    private lateinit var moodChart: LineChart
    private lateinit var addMoodButton: com.google.android.material.floatingactionbutton.FloatingActionButton
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var chartCard: com.google.android.material.card.MaterialCardView
    private lateinit var calendarCard: com.google.android.material.card.MaterialCardView
    private lateinit var calendarGrid: GridLayout
    private lateinit var monthYearText: TextView
    private lateinit var previousMonthButton: ImageButton
    private lateinit var nextMonthButton: ImageButton
    private lateinit var totalEntriesText: TextView
    private lateinit var weeklyAvgText: TextView
    private lateinit var journalCountText: TextView
    private lateinit var oceanInsightsButton: Button
    private lateinit var oceanRelaxButton: Button
    private lateinit var gradientOceanPinkButton: Button
    
    private lateinit var dataManager: DataManager
    private lateinit var moodAdapter: MoodAdapter
    private var moodEntries = mutableListOf<MoodEntry>()
    
    // Calendar variables
    private var calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var isCalendarVisible = true // Track calendar visibility
    
    // For mood selection dialog
    private var selectedMood: MoodType? = null
    private var selectedDate: String = ""
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_enhanced, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        
        moodRecyclerView = view.findViewById(R.id.mood_recycler_view)
        moodChart = view.findViewById(R.id.mood_chart)
        addMoodButton = view.findViewById(R.id.fab_add_mood)
        emptyStateLayout = view.findViewById(R.id.empty_state_layout)
        chartCard = view.findViewById(R.id.chart_card)
        calendarCard = view.findViewById(R.id.calendar_card)
        calendarGrid = view.findViewById(R.id.calendar_grid)
        monthYearText = view.findViewById(R.id.tv_month_year)
        previousMonthButton = view.findViewById(R.id.btn_previous_month)
        nextMonthButton = view.findViewById(R.id.btn_next_month)
        totalEntriesText = view.findViewById(R.id.tv_total_entries)
        weeklyAvgText = view.findViewById(R.id.tv_weekly_avg)
        journalCountText = view.findViewById(R.id.tv_journal_count)
        oceanInsightsButton = view.findViewById(R.id.btn_ocean_insights)
        oceanRelaxButton = view.findViewById(R.id.btn_ocean_relax)
        gradientOceanPinkButton = view.findViewById(R.id.btn_gradient_ocean_pink)
        
        setupRecyclerView()
        setupChart()
        setupCalendar()
        setupAddButton()
        setupOceanButtons()
        setupGradientButton()
        loadMoodEntries()
    }
    
    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter { moodEntry ->
            showMoodOptionsDialog(moodEntry)
        }
        moodRecyclerView.layoutManager = LinearLayoutManager(context)
        moodRecyclerView.adapter = moodAdapter
        moodRecyclerView.setHasFixedSize(true)
        moodRecyclerView.isNestedScrollingEnabled = true
        
        // Add some spacing between items
        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_small)
        moodRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = spacing
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = spacing
                }
            }
        })
    }
    
    private fun setupChart() {
        moodChart.description.isEnabled = false
        moodChart.setTouchEnabled(true)
        moodChart.isDragEnabled = true
        moodChart.setScaleEnabled(true)
        moodChart.setPinchZoom(true)
        
        val xAxis = moodChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        
        val leftAxis = moodChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 5f
        
        moodChart.axisRight.isEnabled = false
        moodChart.legend.isEnabled = false
    }
    
    private fun setupCalendar() {
        updateCalendarHeader()
        
        previousMonthButton.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }
        
        nextMonthButton.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }
        
        updateCalendar()
    }
    
    private fun updateCalendarHeader() {
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearText.text = monthYearFormat.format(calendar.time)
    }
    
    private fun updateCalendar() {
        updateCalendarHeader()
        calendarGrid.removeAllViews()
        
        // Add day headers (Sun, Mon, Tue, etc.)
        val daysOfWeek = arrayOf("S", "M", "T", "W", "T", "F", "S")
        for (day in daysOfWeek) {
            val dayTextView = TextView(context)
            dayTextView.text = day
            dayTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            dayTextView.setTextColor(resources.getColor(R.color.text_secondary, null))
            dayTextView.textSize = 14f
            dayTextView.setPadding(8, 8, 8, 8)
            
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            calendarGrid.addView(dayTextView, params)
        }
        
        // Get first day of month and day of week
        val calendarCopy = calendar.clone() as Calendar
        calendarCopy.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendarCopy.get(Calendar.DAY_OF_WEEK) - 1 // Convert to 0-6
        val daysInMonth = calendarCopy.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // Add empty cells for days before the first day of the month
        for (i in 0 until firstDayOfWeek) {
            val emptyView = View(context)
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            calendarGrid.addView(emptyView, params)
        }
        
        // Add cells for each day of the month
        val currentMonthEntries = moodEntries.filter { 
            val entryDate = dateFormat.parse(it.date)
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = entryDate!!
            
            entryCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
            entryCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
        }
        
        for (day in 1..daysInMonth) {
            val dayTextView = TextView(context)
            dayTextView.text = day.toString()
            dayTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            dayTextView.textSize = 16f
            dayTextView.setPadding(8, 16, 8, 16)
            
            // Check if there's a mood entry for this day
            val dateString = String.format("%04d-%02d-%02d", 
                calendar.get(Calendar.YEAR), 
                calendar.get(Calendar.MONTH) + 1, 
                day)
            
            val moodEntry = currentMonthEntries.find { it.date == dateString }
            
            if (moodEntry != null) {
                // Set background color based on mood
                when (moodEntry.mood) {
                    MoodType.HAPPY -> dayTextView.setBackgroundColor(resources.getColor(R.color.mood_happy, null))
                    MoodType.EXCITED -> dayTextView.setBackgroundColor(resources.getColor(R.color.mood_excited, null))
                    MoodType.NEUTRAL -> dayTextView.setBackgroundColor(resources.getColor(R.color.mood_neutral, null))
                    MoodType.SAD -> dayTextView.setBackgroundColor(resources.getColor(R.color.mood_sad, null))
                    MoodType.ANGRY -> dayTextView.setBackgroundColor(resources.getColor(R.color.mood_angry, null))
                    MoodType.CALM -> dayTextView.setBackgroundColor(resources.getColor(R.color.mood_happy, null))
                    MoodType.STRESSED -> dayTextView.setBackgroundColor(resources.getColor(R.color.mood_sad, null))
                    MoodType.GRATEFUL -> dayTextView.setBackgroundColor(resources.getColor(R.color.mood_happy, null))
                    MoodType.LONELY -> dayTextView.setBackgroundColor(resources.getColor(R.color.mood_sad, null))
                    MoodType.CONFIDENT -> dayTextView.setBackgroundColor(resources.getColor(R.color.mood_happy, null))
                }
                dayTextView.setTextColor(resources.getColor(R.color.white, null))
            } else {
                dayTextView.setTextColor(resources.getColor(R.color.text_primary, null))
            }
            
            // Add click listener to add mood for this day
            dayTextView.setOnClickListener {
                showMoodSelectorDialogForDate(dateString)
            }
            
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            calendarGrid.addView(dayTextView, params)
        }
    }
    
    private fun setupAddButton() {
        addMoodButton.setOnClickListener {
            showMoodSelectorDialog()
        }
    }
    
    private fun setupOceanButtons() {
        oceanInsightsButton.setOnClickListener {
            if (moodEntries.isEmpty()) {
                Toast.makeText(context, "No mood data available for insights", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Calculate mood statistics
            val totalEntries = moodEntries.size
            val avgIntensity = moodEntries.map { it.intensity }.average()
            val moodDistribution = moodEntries.groupingBy { it.mood }.eachCount()
            val mostCommonMood = moodDistribution.maxByOrNull { it.value }?.key ?: MoodType.NEUTRAL
            
            // Prepare stats for modern dialog
            val stats = mapOf(
                "Total Entries" to "$totalEntries",
                "Average Mood" to "${String.format("%.1f", avgIntensity)}/10",
                "Most Common Mood" to "${mostCommonMood.emoji} ${mostCommonMood.description}"
            )
            
            // Prepare distribution for modern dialog
            val distribution = moodDistribution.map { (mood, count) ->
                mood.description to (mood.emoji to count)
            }.toMap()
            
            val message = "Track your emotional journey and discover patterns in your mood over time."
            
            com.example.habitmate.utils.ModernInsightDialog.Builder(requireContext())
                .setTitle("Mood Insights")
                .setMessage(message)
                .setIcon(R.drawable.ic_mood)
                .setStats(stats)
                .setDistribution(distribution)
                .setPositiveButton("OK")
                .show()
        }
        
        oceanRelaxButton.setOnClickListener {
            val exercises = listOf(
                "Deep Breathing: Inhale for 4 counts, hold for 4, exhale for 6. Repeat 5 times.",
                "Progressive Muscle Relaxation: Tense and release each muscle group from toes to head.",
                "Mindful Meditation: Focus on your breath for 5 minutes. Notice thoughts without judgment.",
                "Body Scan: Pay attention to each part of your body, releasing tension as you go.",
                "Guided Imagery: Visualize a peaceful place, engaging all your senses in the experience."
            )
            
            val randomExercise = exercises.random()
            
            AlertDialog.Builder(requireContext())
                .setTitle("Ocean Relaxation 🌊")
                .setMessage(randomExercise)
                .setPositiveButton("Done") { _, _ ->
                    // Optionally log this relaxation session
                    Toast.makeText(context, "Relaxation session completed!", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Skip", null)
                .show()
        }
    }
    
    private fun setupGradientButton() {
        gradientOceanPinkButton.setOnClickListener {
            // Show a mood boosting message with positive affirmations
            val affirmations = listOf(
                "You are capable of amazing things!",
                "Your feelings are valid and important.",
                "Each day is a new opportunity for happiness.",
                "You are stronger than you think.",
                "Your mood can change, and that's okay.",
                "Take a deep breath and trust yourself.",
                "You deserve love and kindness, especially from yourself.",
                "Small steps lead to big changes.",
                "You are making progress, even when it doesn't feel like it.",
                "Your well-being matters."
            )
            
            val randomAffirmation = affirmations.random()
            
            AlertDialog.Builder(requireContext())
                .setTitle("🌊 Ocean Mood Boost 🌸")
                .setMessage("${randomAffirmation}\n\nTake a moment to breathe deeply and remember that your feelings are temporary. You have the power to influence your mood through positive actions and thoughts.")
                .setPositiveButton("I Feel Better!") { _, _ ->
                    Toast.makeText(context, "Positive energy flowing! 🌟", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }
    
    private fun loadMoodEntries() {
        val allEntries = dataManager.getMoodEntries()
        moodEntries.clear()
        moodEntries.addAll(allEntries.sortedByDescending { it.timestamp })
        
        // Update existing adapter instead of creating a new one
        if (::moodAdapter.isInitialized) {
            moodAdapter.notifyDataSetChanged()
        } else {
            moodAdapter = MoodAdapter { moodEntry -> 
                showMoodOptionsDialog(moodEntry)
            }
            moodRecyclerView.adapter = moodAdapter
        }
        
        // Update stats
        totalEntriesText.text = moodEntries.size.toString()
        
        // Calculate weekly average mood
        val last7Days = getLast7Days()
        val recentEntries = moodEntries.filter { it.date in last7Days }
        if (recentEntries.isNotEmpty()) {
            val avgIntensity = recentEntries.map { it.intensity }.average()
            val avgMood = when {
                avgIntensity >= 9 -> MoodType.HAPPY
                avgIntensity >= 7 -> MoodType.EXCITED
                avgIntensity >= 5 -> MoodType.NEUTRAL
                avgIntensity >= 3 -> MoodType.SAD
                else -> MoodType.ANGRY
            }
            weeklyAvgText.text = avgMood.emoji
        } else {
            weeklyAvgText.text = "😊"
        }
        
        // Update journal count
        journalCountText.text = "${moodEntries.size} ${if (moodEntries.size == 1) "entry" else "entries"}"
        
        // Always show the RecyclerView, but toggle visibility of empty state
        moodRecyclerView.visibility = View.VISIBLE
        emptyStateLayout.visibility = if (moodEntries.isEmpty()) View.VISIBLE else View.GONE
        chartCard.visibility = if (moodEntries.isEmpty()) View.GONE else View.VISIBLE
        calendarCard.visibility = if (moodEntries.isEmpty()) View.GONE else View.VISIBLE
        
        updateChart()
        updateCalendar()
    }
    
    private fun updateChart() {
        // Get last 7 days
        val last7Days = getLast7Days()
        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()
        
        // Process each day
        last7Days.forEachIndexed { index, date ->
            // Find mood entry for this date
            val moodEntry = moodEntries.find { it.date == date }
            
            // Convert mood to numeric value
            val moodValue = when (moodEntry?.mood) {
                MoodType.HAPPY -> 5f
                MoodType.EXCITED -> 4f
                MoodType.NEUTRAL -> 3f
                MoodType.SAD -> 2f
                MoodType.ANGRY -> 1f
                MoodType.CALM -> 4.5f
                MoodType.STRESSED -> 1.5f
                MoodType.GRATEFUL -> 4f
                MoodType.LONELY -> 2f
                MoodType.CONFIDENT -> 4.5f
                null -> 0f // Still add entry with 0 for consistent x-axis
            }
            
            // Always add entries to maintain x-axis consistency
            entries.add(Entry(index.toFloat(), moodValue))
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val displayFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                val parsedDate = dateFormat.parse(date)
                labels.add(displayFormat.format(parsedDate!!))
            } catch (e: Exception) {
                labels.add(date)
            }
        }
        
        // Create dataset
        val dataSet = LineDataSet(entries, "Mood Trend")
        dataSet.color = resources.getColor(R.color.pink_primary, null)
        dataSet.setCircleColor(resources.getColor(R.color.pink_primary_dark, null))
        dataSet.lineWidth = 3f
        dataSet.circleRadius = 5f
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawValues(false)
        dataSet.setDrawFilled(true)
        dataSet.fillColor = resources.getColor(R.color.pink_primary_light, null)
        dataSet.fillAlpha = 100
        
        // Create chart data
        val lineData = LineData(dataSet)
        moodChart.data = lineData
        
        // Configure axes
        moodChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        moodChart.xAxis.granularity = 1f
        moodChart.axisLeft.axisMinimum = 0f
        moodChart.axisLeft.axisMaximum = 5f
        
        // Refresh chart
        moodChart.invalidate()
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
    
    private fun showMoodSelectorDialog() {
        showMoodSelectorDialogForDate(dataManager.getCurrentDate())
    }
    
    private fun showMoodSelectorDialogForDate(date: String) {
        selectedDate = date
        selectedMood = null
        
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_mood_selector, null)
        val moodRecyclerView = dialogView.findViewById<RecyclerView>(R.id.mood_selector_recycler)
        val noteEditText = dialogView.findViewById<EditText>(R.id.et_mood_note)
        val saveButton = dialogView.findViewById<Button>(R.id.btn_save_mood)
        
        val moods = MoodType.values().toList()
        val moodSelectorAdapter = MoodSelectorAdapter(moods) { selectedMoodType ->
            selectedMood = selectedMoodType
        }
        
        moodRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        moodRecyclerView.adapter = moodSelectorAdapter
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("How are you feeling?")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .show()
        
        saveButton.setOnClickListener {
            if (selectedMood != null) {
                val note = noteEditText.text.toString().trim()
                saveMoodEntry(selectedMood!!, note, selectedDate)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please select a mood", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun saveMoodEntry(mood: MoodType, note: String, date: String = dataManager.getCurrentDate()) {
        // Get all existing entries
        val existingEntries = dataManager.getMoodEntries().toMutableList()
        
        // Convert mood to intensity value (1-10 scale)
        val intensity = when (mood) {
            MoodType.ANGRY -> 1
            MoodType.STRESSED -> 2
            MoodType.SAD -> 3
            MoodType.LONELY -> 4
            MoodType.NEUTRAL -> 5
            MoodType.CALM -> 6
            MoodType.CONFIDENT -> 7
            MoodType.GRATEFUL -> 8
            MoodType.EXCITED -> 9
            MoodType.HAPPY -> 10
        }
        
        // Check if there's already an entry for this date
        val existingEntryIndex = existingEntries.indexOfFirst { it.date == date }
        
        if (existingEntryIndex != -1) {
            // Update existing entry
            val existingEntry = existingEntries[existingEntryIndex]
            val updatedEntry = existingEntry.copy(
                mood = mood,
                note = note,
                timestamp = System.currentTimeMillis(),
                intensity = intensity
            )
            existingEntries[existingEntryIndex] = updatedEntry
        } else {
            // Create new entry
            val newEntry = MoodEntry(
                id = dataManager.generateId(),
                mood = mood,
                note = note,
                date = date,
                timestamp = System.currentTimeMillis(),
                intensity = intensity
            )
            existingEntries.add(newEntry)
        }
        
        // Save all entries
        dataManager.saveMoodEntries(existingEntries)
        
        // Reload entries to update UI
        loadMoodEntries()
        Toast.makeText(context, "Mood logged successfully!", Toast.LENGTH_SHORT).show()
    }
    
    private fun showMoodOptionsDialog(moodEntry: MoodEntry) {
        val options = arrayOf("View Details", "Share", "Edit", "Delete")
        
        AlertDialog.Builder(requireContext())
            .setTitle("Mood Entry")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showMoodDetailsDialog(moodEntry)
                    1 -> shareMoodEntry(moodEntry)
                    2 -> showEditMoodDialog(moodEntry)
                    3 -> showDeleteConfirmationDialog(moodEntry)
                }
            }
            .show()
    }
    
    private fun showEditMoodDialog(moodEntry: MoodEntry) {
        selectedDate = moodEntry.date
        selectedMood = moodEntry.mood
        
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_mood_selector, null)
        val moodRecyclerView = dialogView.findViewById<RecyclerView>(R.id.mood_selector_recycler)
        val noteEditText = dialogView.findViewById<EditText>(R.id.et_mood_note)
        val saveButton = dialogView.findViewById<Button>(R.id.btn_save_mood)
        
        noteEditText.setText(moodEntry.note)
        
        val moods = MoodType.values().toList()
        val moodSelectorAdapter = MoodSelectorAdapter(moods) { selectedMoodType ->
            selectedMood = selectedMoodType
        }
        
        // Highlight the current mood in the selector
        moodRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        moodRecyclerView.adapter = moodSelectorAdapter
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Mood")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .show()
        
        saveButton.setOnClickListener {
            if (selectedMood != null) {
                val note = noteEditText.text.toString().trim()
                updateMoodEntry(moodEntry, selectedMood!!, note)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please select a mood", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateMoodEntry(moodEntry: MoodEntry, mood: MoodType, note: String) {
        val updatedEntries = dataManager.getMoodEntries().toMutableList()
        updatedEntries.removeAll { it.id == moodEntry.id }
        
        // Convert mood to intensity value (1-10 scale)
        val intensity = when (mood) {
            MoodType.ANGRY -> 1
            MoodType.STRESSED -> 2
            MoodType.SAD -> 3
            MoodType.LONELY -> 4
            MoodType.NEUTRAL -> 5
            MoodType.CALM -> 6
            MoodType.CONFIDENT -> 7
            MoodType.GRATEFUL -> 8
            MoodType.EXCITED -> 9
            MoodType.HAPPY -> 10
        }
        
        val updatedEntry = moodEntry.copy(
            mood = mood,
            note = note,
            timestamp = System.currentTimeMillis(),
            intensity = intensity
        )
        
        updatedEntries.add(updatedEntry)
        dataManager.saveMoodEntries(updatedEntries)
        
        loadMoodEntries()
        Toast.makeText(context, "Mood updated successfully!", Toast.LENGTH_SHORT).show()
    }
    
    private fun showMoodDetailsDialog(moodEntry: MoodEntry) {
        val message = if (moodEntry.note.isNotEmpty()) {
            "Mood: ${moodEntry.mood.emoji}\nDate: ${formatDate(moodEntry.date)}\nNote: ${moodEntry.note}"
        } else {
            "Mood: ${moodEntry.mood.emoji}\nDate: ${formatDate(moodEntry.date)}"
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("Mood Details")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun shareMoodEntry(moodEntry: MoodEntry) {
        val shareText = if (moodEntry.note.isNotEmpty()) {
            "I'm feeling ${moodEntry.mood.emoji} today! ${moodEntry.note} #HabitMate"
        } else {
            "I'm feeling ${moodEntry.mood.emoji} today! #HabitMate"
        }
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share your mood"))
    }
    
    private fun showDeleteConfirmationDialog(moodEntry: MoodEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Mood Entry")
            .setMessage("Are you sure you want to delete this mood entry?")
            .setPositiveButton("Delete") { _, _ ->
                val updatedEntries = dataManager.getMoodEntries().toMutableList()
                updatedEntries.removeAll { it.id == moodEntry.id }
                dataManager.saveMoodEntries(updatedEntries)
                
                loadMoodEntries()
                Toast.makeText(context, "Mood entry deleted!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }
    
    private inner class MoodAdapter(
        private val onMoodClick: (MoodEntry) -> Unit
    ) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mood_enhanced, parent, false)
            return MoodViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
            holder.bind(moodEntries[position])
        }
        
        override fun getItemCount(): Int = moodEntries.size
        
        inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val emojiTextView = itemView.findViewById<TextView>(R.id.tv_mood_emoji)
            private val dateTextView = itemView.findViewById<TextView>(R.id.tv_mood_date)
            private val noteTextView = itemView.findViewById<TextView>(R.id.tv_mood_note)
            private val timeTextView = itemView.findViewById<TextView>(R.id.tv_mood_time)
            
            fun bind(moodEntry: MoodEntry) {
                // Ensure emoji is displayed properly
                emojiTextView.text = moodEntry.mood.emoji
                emojiTextView.invalidate()
                
                dateTextView.text = formatDate(moodEntry.date)
                noteTextView.text = moodEntry.note
                noteTextView.visibility = if (moodEntry.note.isNotEmpty()) View.VISIBLE else View.GONE
                
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                timeTextView.text = timeFormat.format(Date(moodEntry.timestamp))
                
                itemView.setOnClickListener {
                    onMoodClick(moodEntry)
                }
            }
            
            private fun formatDate(dateString: String): String {
                return try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                    val date = inputFormat.parse(dateString)
                    outputFormat.format(date!!)
                } catch (e: Exception) {
                    dateString
                }
            }
        }
    }
    
    private inner class MoodSelectorAdapter(
        private val moods: List<MoodType>,
        private val onMoodSelected: (MoodType) -> Unit
    ) : RecyclerView.Adapter<MoodSelectorAdapter.MoodSelectorViewHolder>() {
        
        private var selectedPosition = -1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodSelectorViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mood_selector, parent, false)
            return MoodSelectorViewHolder(view)
        }

        override fun onBindViewHolder(holder: MoodSelectorViewHolder, position: Int) {
            holder.bind(moods[position], position == selectedPosition)
        }

        override fun getItemCount(): Int = moods.size

        inner class MoodSelectorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val emojiTextView = itemView.findViewById<TextView>(R.id.tv_mood_emoji)
            private val nameTextView = itemView.findViewById<TextView>(R.id.tv_mood_name)

            fun bind(mood: MoodType, isSelected: Boolean) {
                emojiTextView.text = mood.emoji
                nameTextView.text = mood.description
                
                // Highlight selected item
                if (isSelected) {
                    itemView.setBackgroundColor(itemView.context.resources.getColor(R.color.pink_primary_light, null))
                } else {
                    itemView.setBackgroundColor(0) // Transparent
                }
                
                itemView.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = bindingAdapterPosition
                    
                    // Notify changes to update UI
                    if (previousPosition >= 0) {
                        notifyItemChanged(previousPosition)
                    }
                    notifyItemChanged(selectedPosition)
                    
                    onMoodSelected(mood)
                }
            }
        }
    }
}