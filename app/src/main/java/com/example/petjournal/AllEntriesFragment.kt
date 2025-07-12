package com.example.petjournal

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petjournal.databinding.FragmentJournalListBinding

class AllEntriesFragment : Fragment() {

    private var _binding: FragmentJournalListBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: PetJournalAdapter

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        _binding = FragmentJournalListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadEntries()
    }

    private fun loadEntries() {
        val entries = dbHelper.getAllEntries()
        adapter = PetJournalAdapter(
            entries,
            onFavoriteToggle = { entry, isFav ->
                dbHelper.updateFavoriteStatus(entry.id ?: "", isFav)
                loadEntries()
            }
        )
        binding.recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadEntries()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}