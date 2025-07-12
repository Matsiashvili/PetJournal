package com.example.petjournal

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.petjournal.databinding.FragmentEntryDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class EntryDetailFragment : Fragment(R.layout.fragment_entry_detail) {

    private val args: EntryDetailFragmentArgs by navArgs()
    private var _binding: FragmentEntryDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEntryDetailBinding.bind(view)
        dbHelper = DatabaseHelper(requireContext())

        val entryId = args.entryId
        val entry = dbHelper.getAllEntries().find { it.id == entryId }
        entry?.let {
            binding.tvPetName.text = it.petName
            binding.tvSpecies.text = it.species
            binding.tvBreed.text = it.breed
            binding.tvNotes.text = it.notes
            binding.tvDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it.date))
            if (!it.imageUrl.isNullOrEmpty()) {
                val imageBytes = android.util.Base64.decode(it.imageUrl, android.util.Base64.DEFAULT)
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                binding.ivPetImage.setImageBitmap(bitmap)
            } else {
                binding.ivPetImage.setImageResource(R.drawable.ic_placeholder)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}