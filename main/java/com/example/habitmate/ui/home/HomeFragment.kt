package com.example.habitmate.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habitmate.R
import com.example.habitmate.data.*
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class HomeFragment : Fragment() {
    
    private lateinit var habitsRecyclerView: RecyclerView
    private lateinit var progressTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var pieChart: PieChart
    private lateinit var addFab: FloatingActionButton
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var recentHabitsTitle: TextView
    private lateinit var totalHabitsTextView: TextView
    private lateinit var currentStreakTextView: TextView
    private lateinit var bestStreakTextView: TextView
    private lateinit var oceanInsightsButton: Button
    private lateinit var oceanRelaxButton: Button
    private lateinit var gradientOceanPinkButton: Button
    
    private lateinit var dataManager: DataManager
    private lateinit var habitsAdapter: HabitsAdapter
    private var habits = mutableListOf<Habit>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        
        habitsRecyclerView = view.findViewById(R.id.habits_recycler_view)
        progressTextView = view.findViewById(R.id.progress_text)
        progressBar = view.findViewById(R.id.progress_bar)
        pieChart = view.findViewById(R.id.pie_chart)
        addFab = view.findViewById(R.id.fab_add_habit)
        emptyStateLayout = view.findViewById(R.id.empty_state_layout)
        recentHabitsTitle = view.findViewById(R.id.recent_habits_title)
        totalHabitsTextView = view.findViewById(R.id.tv_total_habits)
        currentStreakTextView = view.findViewById(R.id.tv_current_streak)
        bestStreakTextView = view.findViewById(R.id.tv_best_streak)
        oceanInsightsButton = view.findViewById(R.id.btn_ocean_insights)
        oceanRelaxButton = view.findViewById(R.id.btn_ocean_relax)
        gradientOceanPinkButton = view.findViewById(R.id.btn_gradient_ocean_pink)
        
        setupRecyclerView()
        setupPieChart()
        setupFab()
        setupOceanButtons()
        setupGradientButton()
        loadHabits()
        updateProgress()
        updateStatistics()
    }
    
    private fun setupOceanButtons() {
        oceanInsightsButton.setOnClickListener {
            showAppInsights()
        }
        
        oceanRelaxButton.setOnClickListener {
            showRelaxationTips()
        }
    }
    
    private fun setupGradientButton() {
        gradientOceanPinkButton.setOnClickListener {
            // Show energy boosting tips for habits
            val energyTips = listOf(
                "Start with just 5 minutes of your habit today.",
                "Celebrate small wins - they build momentum!",
                "Link your new habit to an existing one.",
                "Focus on consistency over perfection.",
                "Visualize how you'll feel after completing your habit.",
                "Break big habits into tiny, manageable steps.",
                "Track your progress to see your growth.",
                "Reward yourself for showing up.",
                "Remember: progress, not perfection.",
                "Your future self will thank you for starting today."
            )
            
            val randomTip = energyTips.random()
            
            AlertDialog.Builder(requireContext())
                .setTitle("🌊 Ocean Energy Boost 🌸")
                .setMessage("${randomTip}\n\nYou have the energy and determination to make positive changes in your life. Take a deep breath and trust in your ability to build the habits that will lead to your success.")
                .setPositiveButton("Let's Do This!") { _, _ ->
                    Toast.makeText(context, "Energy boost activated! ⚡", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }
    
    private fun showAppInsights() {
        val habits = dataManager.getHabits()
        val habitEntries = dataManager.getHabitEntries()
        val moodEntries = dataManager.getMoodEntries()
        val hydrationData = dataManager.getHydrationData()
        
        val totalHabits = habits.size
        val activeHabits = habits.count { it.isActive }
        val completedToday = habitEntries.count { it.date == dataManager.getCurrentDate() && it.isCompleted }
        val totalCompletions = habitEntries.count { it.isCompleted }
        val moodCount = moodEntries.size
        val waterIntakeToday = hydrationData.glassesToday * 250 // Assuming 250ml per glass
        
        // Prepare stats for modern dialog
        val stats = mapOf(
            "Total Habits" to "$totalHabits",
            "Active Habits" to "$activeHabits",
            "Completed Today" to "$completedToday",
            "Total Completions" to "$totalCompletions",
            "Mood Entries" to "$moodCount",
            "Water Intake Today" to "${waterIntakeToday}ml"
        )
        
        val message = "Get a comprehensive overview of your wellness journey and track your progress across all areas of your life."
        
        com.example.habitmate.utils.ModernInsightDialog.Builder(requireContext())
            .setTitle("🌊 Ocean Insights")
            .setMessage(message)
            .setIcon(R.drawable.ic_home)
            .setStats(stats)
            .setPositiveButton("OK")
            .show()
    }
    
    private fun showRelaxationTips() {
        val tips = listOf(
            "Take 5 deep breaths: Inhale for 4 counts, hold for 4, exhale for 6.",
            "Practice gratitude by listing 3 things you're thankful for today.",
            "Take a 5-minute walk outside to refresh your mind.",
            "Listen to calming ocean sounds or nature music for 10 minutes.",
            "Do a quick body scan meditation, focusing on releasing tension.",
            "Write down your thoughts in a journal for 5 minutes.",
            "Try progressive muscle relaxation: tense and release each muscle group."
        )
        
        val randomTip = tips.random()
        
        AlertDialog.Builder(requireContext())
            .setTitle("🌊 Ocean Relaxation Tips")
            .setMessage(randomTip)
            .setPositiveButton("Done") { _, _ ->
                Toast.makeText(context, "Relaxation tip completed!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Skip", null)
            .show()
    }
    
    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter(habits) { habit ->
            showEditHabitDialog(habit)
        }
        habitsRecyclerView.layoutManager = LinearLayoutManager(context)
        habitsRecyclerView.adapter = habitsAdapter
    }
    
    private fun setupPieChart() {
        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(true)
        pieChart.setDrawEntryLabels(false)
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(android.graphics.Color.WHITE)
        pieChart.setTransparentCircleColor(android.graphics.Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.setHoleRadius(58f)
        pieChart.setTransparentCircleRadius(61f)
        pieChart.setDrawCenterText(true)
        pieChart.setCenterTextSize(16f)
        pieChart.setCenterTextColor(resources.getColor(R.color.text_primary, null))
        pieChart.legend.isEnabled = false
        
        // Set initial chart to white when no data
        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(1f, "No Data"))
        
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(android.graphics.Color.WHITE)
        dataSet.setDrawValues(false)
        
        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.centerText = "No Habits\nToday"
        pieChart.invalidate()
    }
    
    private fun setupFab() {
        addFab.setOnClickListener {
            showAddHabitDialog()
        }
    }
    
    private fun loadHabits() {
        habits.clear()
        habits.addAll(dataManager.getHabits().filter { it.isActive }.take(5)) // Show only recent 5 habits
        habitsAdapter.notifyDataSetChanged()
        
        emptyStateLayout.visibility = if (habits.isEmpty()) View.VISIBLE else View.GONE
        habitsRecyclerView.visibility = if (habits.isEmpty()) View.GONE else View.VISIBLE
        recentHabitsTitle.visibility = if (habits.isEmpty()) View.GONE else View.VISIBLE
        
        // Update statistics when habits change
        updateStatistics()
    }
    
    private fun updateProgress() {
        val today = dataManager.getCurrentDate()
        val allHabits = dataManager.getHabits().filter { it.isActive }
        val todayEntries = dataManager.getHabitEntriesForDate(today)
        val completedCount = todayEntries.count { it.isCompleted }
        val totalHabits = allHabits.size
        
        val progressPercentage = if (totalHabits > 0) {
            (completedCount.toFloat() / totalHabits * 100).toInt()
        } else {
            0
        }
        
        progressTextView.text = "$completedCount/$totalHabits completed ($progressPercentage%)"
        progressBar.max = if (totalHabits > 0) totalHabits else 1
        progressBar.progress = completedCount
        
        // Update pie chart
        updatePieChart(completedCount, totalHabits - completedCount)
        
        // Update statistics
        updateStatistics()
    }
    
    private fun updateStatistics() {
        val allHabits = dataManager.getHabits().filter { it.isActive }
        val totalHabits = allHabits.size
        var currentStreak = 0
        var bestStreak = 0
        
        // Calculate streaks
        if (totalHabits > 0) {
            currentStreak = calculateCurrentStreak(allHabits)
            bestStreak = allHabits.maxOfOrNull { it.bestStreak } ?: 0
        }
        
        totalHabitsTextView.text = totalHabits.toString()
        currentStreakTextView.text = currentStreak.toString()
        bestStreakTextView.text = bestStreak.toString()
    }
    
    private fun calculateCurrentStreak(habits: List<Habit>): Int {
        // For simplicity, we'll calculate based on the habit with the longest current streak
        // In a real app, you might want to calculate this differently
        return habits.maxOfOrNull { it.streak } ?: 0
    }
    
    private fun updatePieChart(completed: Int, pending: Int) {
        val entries = mutableListOf<PieEntry>()
        
        if (completed > 0) {
            entries.add(PieEntry(completed.toFloat(), "Completed"))
        }
        if (pending > 0) {
            entries.add(PieEntry(pending.toFloat(), "Pending"))
        }
        
        // Always ensure we have at least one entry
        if (entries.isEmpty()) {
            entries.add(PieEntry(1f, "No Habits"))
        }
        
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = if (completed == 0 && pending == 0) {
            // If no habits, use light gray color
            listOf(android.graphics.Color.parseColor("#E0E0E0"))
        } else {
            // Use vibrant colors for better visual appeal
            val colors = mutableListOf<Int>()
            if (completed > 0) {
                // Vibrant green for completed habits
                colors.add(android.graphics.Color.parseColor("#4CAF50"))
            }
            if (pending > 0) {
                // Attractive blue-gray for pending habits
                colors.add(android.graphics.Color.parseColor("#90A4AE"))
            }
            colors
        }
        dataSet.setDrawValues(completed > 0 || pending > 0)
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = resources.getColor(R.color.text_primary, null)
        
        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(pieChart))
        pieChart.data = pieData
        
        val total = completed + pending
        pieChart.centerText = if (total > 0) {
            "${(completed.toFloat() / total * 100).toInt()}%\nCompleted"
        } else {
            "No Habits\nToday"
        }
        
        pieChart.invalidate()
    }
    
    private fun showAddHabitDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_habit, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.et_habit_name)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.et_habit_description)
        val frequencySpinner = dialogView.findViewById<Spinner>(R.id.spinner_frequency)
        
        val frequencies = HabitFrequency.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frequencySpinner.adapter = adapter
        
        AlertDialog.Builder(requireContext())
            .setTitle("Add Habit")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()
                val frequency = HabitFrequency.valueOf(frequencies[frequencySpinner.selectedItemPosition])
                
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(context, "Habit name is required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                val habit = Habit(
                    id = dataManager.generateId(),
                    name = name,
                    description = description,
                    frequency = frequency
                )
                
                val updatedHabits = dataManager.getHabits().toMutableList()
                updatedHabits.add(habit)
                dataManager.saveHabits(updatedHabits)
                
                loadHabits()
                updateProgress()
                Toast.makeText(context, "Habit added successfully!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showEditHabitDialog(habit: Habit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_habit, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.et_habit_name)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.et_habit_description)
        val frequencySpinner = dialogView.findViewById<Spinner>(R.id.spinner_frequency)
        
        nameEditText.setText(habit.name)
        descriptionEditText.setText(habit.description)
        
        val frequencies = HabitFrequency.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frequencySpinner.adapter = adapter
        frequencySpinner.setSelection(frequencies.indexOf(habit.frequency.name))
        
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Habit")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()
                val frequency = HabitFrequency.valueOf(frequencies[frequencySpinner.selectedItemPosition])
                
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(context, "Habit name is required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                val updatedHabit = habit.copy(
                    name = name,
                    description = description,
                    frequency = frequency
                )
                
                val updatedHabits = dataManager.getHabits().toMutableList()
                val index = updatedHabits.indexOfFirst { it.id == habit.id }
                if (index != -1) {
                    updatedHabits[index] = updatedHabit
                    dataManager.saveHabits(updatedHabits)
                    
                    loadHabits()
                    updateProgress()
                    Toast.makeText(context, "Habit updated successfully!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Delete") { _, _ ->
                showDeleteConfirmationDialog(habit)
            }
            .show()
    }
    
    private fun showDeleteConfirmationDialog(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete \"${habit.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                val updatedHabits = dataManager.getHabits().toMutableList()
                updatedHabits.removeAll { it.id == habit.id }
                dataManager.saveHabits(updatedHabits)
                
                loadHabits()
                updateProgress()
                Toast.makeText(context, "Habit deleted successfully!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private inner class HabitsAdapter(
        private val habits: List<Habit>,
        private val onHabitClick: (Habit) -> Unit
    ) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
            return HabitViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
            holder.bind(habits[position])
        }
        
        override fun getItemCount(): Int = habits.size
        
        inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nameTextView = itemView.findViewById<TextView>(R.id.tv_habit_name)
            private val descriptionTextView = itemView.findViewById<TextView>(R.id.tv_habit_description)
            private val frequencyTextView = itemView.findViewById<TextView>(R.id.tv_habit_frequency)
            private val checkBox = itemView.findViewById<CheckBox>(R.id.checkbox_completed)
            
            fun bind(habit: Habit) {
                nameTextView.text = habit.name
                descriptionTextView.text = habit.description
                frequencyTextView.text = habit.frequency.name.lowercase().replaceFirstChar { it.uppercase() }
                
                val today = dataManager.getCurrentDate()
                val todayEntry = dataManager.getHabitEntriesForDate(today).find { it.habitId == habit.id }
                val isCompleted = todayEntry?.isCompleted ?: false
                
                checkBox.isChecked = isCompleted
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked != isCompleted) {
                        toggleHabitCompletion(habit, isChecked)
                    }
                }
                
                itemView.setOnClickListener {
                    onHabitClick(habit)
                }
            }
            
            private fun toggleHabitCompletion(habit: Habit, isCompleted: Boolean) {
                val today = dataManager.getCurrentDate()
                val entries = dataManager.getHabitEntries().toMutableList()
                
                val existingEntry = entries.find { it.habitId == habit.id && it.date == today }
                if (existingEntry != null) {
                    val index = entries.indexOf(existingEntry)
                    entries[index] = existingEntry.copy(
                        isCompleted = isCompleted,
                        completedAt = if (isCompleted) System.currentTimeMillis() else null
                    )
                } else {
                    entries.add(
                        HabitEntry(
                            id = dataManager.generateId(),
                            habitId = habit.id,
                            date = today,
                            isCompleted = isCompleted,
                            completedAt = if (isCompleted) System.currentTimeMillis() else null
                        )
                    )
                }
                
                dataManager.saveHabitEntries(entries)
                updateProgress()
                loadHabits() // Refresh the RecyclerView to reflect the changes
            }
        }
    }
    
    // Add this method to refresh data when the fragment becomes visible
    override fun onResume() {
        super.onResume()
        loadHabits()
        updateProgress()
        updateStatistics()
    }
}