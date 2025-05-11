package com.example.marvel_heroes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * Adapter for displaying heroes in a RecyclerView
 * @param heroes Initial list of heroes
 * @param onHeroClick Callback for when a hero is clicked
 */
class HeroAdapter(
    private var heroes: List<Hero>,
    private val onHeroClick: (Hero) -> Unit
) : RecyclerView.Adapter<HeroAdapter.HeroViewHolder>() {

    /**
     * Updates the list of heroes and refreshes the display
     * @param newHeroes New list of heroes to display
     */
    fun submitList(newHeroes: List<Hero>) {
        Log.d("HeroAdapter", "Submitting new list with ${newHeroes.size} heroes")
        heroes = newHeroes
        notifyDataSetChanged() // Refresh the entire list
    }

    /**
     * Creates a new ViewHolder for a hero item
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hero, parent, false)
        return HeroViewHolder(view)
    }

    /**
     * Binds data to a ViewHolder at the specified position
     */
    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        val hero = heroes[position]
        holder.bind(hero)
    }

    /**
     * Returns the total number of heroes in the list
     */
    override fun getItemCount(): Int = heroes.size

    /**
     * ViewHolder for hero items
     */
    inner class HeroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.heroNameTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.heroImageView)

        /**
         * Binds hero data to the ViewHolder
         * @param hero The hero to display
         */
        fun bind(hero: Hero) {
            nameTextView.text = hero.name
            
            // Load image with Glide
            Glide.with(itemView.context)
                .load(hero.imageUrl)
                .into(imageView)
            
            // Set click listener
            itemView.setOnClickListener {
                onHeroClick(hero)
            }
            
            // Log for debugging
            Log.d("HeroAdapter", "Binding hero: ${hero.name}, isFavorite: ${hero.isFavorite}")
        }
    }
}
