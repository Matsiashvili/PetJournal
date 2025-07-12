package com.example.petjournal

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.petjournal.databinding.FragmentJournalTabsBinding
import com.google.android.material.tabs.TabLayoutMediator

class JournalTabsFragment : Fragment(R.layout.fragment_journal_tabs) {

    private var _binding: FragmentJournalTabsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentJournalTabsBinding.bind(view)

        val pagerAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2
            override fun createFragment(position: Int) = when (position) {
                0 -> AllEntriesFragment()
                1 -> FavoritesFragment()
                else -> throw IllegalStateException("Invalid position $position")
            }
        }
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "All Entries" else "Favorites"
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}