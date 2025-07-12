package com.example.petjournal

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.petjournal.databinding.FragmentAddEntryBinding
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream

class AddEntryFragment : Fragment() {

    private var _binding: FragmentAddEntryBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private lateinit var dbHelper: DatabaseHelper

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
        binding.ivPetImage.setImageURI(uri)
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        _binding = FragmentAddEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())

        binding.ivPetImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            saveEntry()
        }
    }

    private fun saveEntry() {
        val petName = binding.etPetName.text.toString().trim()
        val species = binding.etSpecies.text.toString().trim()
        val breed = binding.etBreed.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()
        val date = System.currentTimeMillis()

        if (petName.isEmpty()) {
            Toast.makeText(context, "Please enter the pet name", Toast.LENGTH_SHORT).show()
            return
        }

        val base64Image: String? = selectedImageUri?.let { uri ->
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                val imageBytes = baos.toByteArray()
                Base64.encodeToString(imageBytes, Base64.DEFAULT)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        saveEntryToDatabase(petName, species, breed, notes, base64Image, date)
    }

    private fun saveEntryToDatabase(
        petName: String,
        species: String,
        breed: String,
        notes: String,
        base64Image: String?,
        date: Long
    ) {
        val entry = PetJournalEntry(
            petName = petName,
            species = species,
            breed = breed,
            date = date,
            notes = notes,
            imageUrl = base64Image
        )

        val databaseRef = FirebaseDatabase.getInstance().getReference("entries")
        val newEntryRef = databaseRef.push()
        val firebaseId = newEntryRef.key ?: ""

        val firebaseEntry = entry.copy(id = firebaseId)

        newEntryRef.setValue(firebaseEntry)
            .addOnSuccessListener {
                val result = dbHelper.addEntry(firebaseEntry)
                if (result != -1L) {
                    Toast.makeText(context, "Entry saved locally and to cloud!", Toast.LENGTH_SHORT).show()
                    clearFields()
                } else {
                    Toast.makeText(context, "Firebase success, but local DB failed", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error saving to Firebase", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        binding.etPetName.text.clear()
        binding.etSpecies.text.clear()
        binding.etBreed.text.clear()
        binding.etNotes.text.clear()
        binding.ivPetImage.setImageResource(R.drawable.ic_placeholder)
        selectedImageUri = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}