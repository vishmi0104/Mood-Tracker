package com.example.habitmate.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habitmate.databinding.FragmentOnboardingHabitsBinding
import com.example.habitmate.databinding.FragmentOnboardingHydrationBinding
import com.example.habitmate.databinding.FragmentOnboardingMoodBinding

class OnboardingAdapter(
    private val pages: List<OnboardingPage>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> HabitsViewHolder(FragmentOnboardingHabitsBinding.inflate(inflater, parent, false))
            1 -> MoodViewHolder(FragmentOnboardingMoodBinding.inflate(inflater, parent, false))
            2 -> HydrationViewHolder(FragmentOnboardingHydrationBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    override fun getItemViewType(position: Int): Int = position

    abstract class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(page: OnboardingPage)
    }

    class HabitsViewHolder(
        private val binding: FragmentOnboardingHabitsBinding
    ) : OnboardingViewHolder(binding.root) {
        override fun bind(page: OnboardingPage) {
            binding.title.text = page.title
            binding.description.text = page.description
        }
    }

    class MoodViewHolder(
        private val binding: FragmentOnboardingMoodBinding
    ) : OnboardingViewHolder(binding.root) {
        override fun bind(page: OnboardingPage) {
            binding.title.text = page.title
            binding.description.text = page.description
        }
    }

    class HydrationViewHolder(
        private val binding: FragmentOnboardingHydrationBinding
    ) : OnboardingViewHolder(binding.root) {
        override fun bind(page: OnboardingPage) {
            binding.title.text = page.title
            binding.description.text = page.description
        }
    }
}

