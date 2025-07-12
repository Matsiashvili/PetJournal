package com.example.petjournal

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class PetJournalAdapter(
    private val entries: List<PetJournalEntry>,
    private val onItemClick: (PetJournalEntry) -> Unit = {},
    private val onFavoriteToggle: (PetJournalEntry, Boolean) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<PetJournalAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val petName: TextView = view.findViewById(R.id.tvPetName)
        val species: TextView = view.findViewById(R.id.tvSpecies)
        val notes: TextView = view.findViewById(R.id.tvNotes)
        val date: TextView = view.findViewById(R.id.tvDate)
        val petImage: ImageView = view.findViewById(R.id.ivPetImage)
        val favoriteBtn: ImageButton = view.findViewById(R.id.btnFavorite)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(entries[position])
                }
            }
            favoriteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val entry = entries[position]
                    onFavoriteToggle(entry, !entry.isFavorite)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pet_journal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.petName.text = entry.petName
        holder.species.text = entry.species
        holder.notes.text = entry.notes

        val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            .format(Date(entry.date))
        holder.date.text = formattedDate

        if (!entry.imageUrl.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(entry.imageUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.petImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.petImage.setImageResource(R.drawable.ic_placeholder)
            }
        } else {
            holder.petImage.setImageResource(R.drawable.ic_placeholder)
        }

        holder.favoriteBtn.setImageResource(
            if (entry.isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )
    }

    override fun getItemCount(): Int = entries.size
}