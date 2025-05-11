package com.example.marvel_heroes

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.marvel_heroes.api.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Dialog fragment for displaying detailed information about a hero.
 * Shows hero image, description, biography, and provides options to favorite or share.
 */
class HeroDetailsDialog : DialogFragment() {
    
    private lateinit var hero: Hero
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set dialog style
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert)
        
        // Get hero from arguments
        arguments?.let {
            hero = it.getParcelable(ARG_HERO, Hero::class.java) 
                ?: throw IllegalArgumentException("Hero must not be null")
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the hero details dialog layout
        return inflater.inflate(R.layout.dialog_hero_details, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get references to UI components
        val nameTextView = view.findViewById<TextView>(R.id.heroNameTextView)
        val descriptionTextView = view.findViewById<TextView>(R.id.heroDescriptionTextView)
        val imageView = view.findViewById<ImageView>(R.id.heroImageView)
        val biographyTextView = view.findViewById<TextView>(R.id.heroBiographyTextView)
        val favoriteButton = view.findViewById<Button>(R.id.favoriteButton)
        val shareButton = view.findViewById<Button>(R.id.shareButton)
        val closeButton = view.findViewById<Button>(R.id.closeButton)
        
        // Set hero name and description
        nameTextView.text = hero.name
        descriptionTextView.text = hero.description
        
        // Load hero image with Glide
        Glide.with(requireContext())
            .load(hero.imageUrl)
            .into(imageView)
        
        // Load additional biography data from API
        loadBiography(biographyTextView)
        
        // Set up favorite button with current state
        setupFavoriteButton(favoriteButton)
        
        // Set up share button
        setupShareButton(shareButton)
        
        // Set up close button
        closeButton.setOnClickListener {
            dismiss()
        }
    }
    
    /**
     * Loads hero biography data from the API
     * @param biographyTextView TextView to display the biography
     */
    private fun loadBiography(biographyTextView: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch biography data from API
                val biography = RetrofitInstance.superheroService.getHeroBiography(hero.id)
                
                // Format biography text
                val biographyText = """
                    Full Name: ${biography.fullName}
                    Alter Egos: ${biography.alterEgos}
                    Aliases: ${biography.aliases.joinToString(", ")}
                    Place of Birth: ${biography.placeOfBirth}
                    First Appearance: ${biography.firstAppearance}
                    Publisher: ${biography.publisher}
                    Alignment: ${biography.alignment}
                """.trimIndent()
                
                // Update UI on main thread
                withContext(Dispatchers.Main) {
                    biographyTextView.text = biographyText
                }
            } catch (e: Exception) {
                // Show error message if biography loading fails
                withContext(Dispatchers.Main) {
                    biographyTextView.text = "Error loading biography: ${e.message}"
                }
            }
        }
    }
    
    /**
     * Sets up the favorite button with appropriate text and click handler
     * @param favoriteButton Button for toggling favorite status
     */
    private fun setupFavoriteButton(favoriteButton: Button) {
        // Set button text based on current favorite status
        favoriteButton.text = if (hero.isFavorite) "Remove from Favorites" else "Add to Favorites"
        
        // Set click listener to toggle favorite status
        favoriteButton.setOnClickListener {
            // Toggle favorite status
            hero.isFavorite = !hero.isFavorite
            favoriteButton.text = if (hero.isFavorite) "Remove from Favorites" else "Add to Favorites"
            
            // Show confirmation toast
            val message = if (hero.isFavorite) "${hero.name} added to favorites" else "${hero.name} removed from favorites"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            
            // Save to database
            saveHeroToDatabase()
        }
    }
    
    /**
     * Saves the hero's updated favorite status to the database
     */
    private fun saveHeroToDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = (requireActivity() as MainActivity).getDatabase()
                Log.d("HeroDetailsDialog", "Saving hero ${hero.name} (ID: ${hero.id}) with favorite status: ${hero.isFavorite}")
                db.heroDao().upsertHero(hero)
                
                // Verify the save by retrieving from database
                val savedHero = db.heroDao().getHeroById(hero.id)
                Log.d("HeroDetailsDialog", "Verified from DB: Hero ${savedHero?.name} (ID: ${savedHero?.id}) with favorite status: ${savedHero?.isFavorite}")
                
                // Check all favorites
                val allFavorites = db.heroDao().getAllFavorites()
                Log.d("HeroDetailsDialog", "Total favorites in database: ${allFavorites.size}")
                allFavorites.forEach {
                    Log.d("HeroDetailsDialog", "Favorite in DB: ${it.name} (ID: ${it.id})")
                }
            } catch (e: Exception) {
                Log.e("HeroDetailsDialog", "Error saving hero", e)
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Sets up the share button with appropriate click handler
     * @param shareButton Button for sharing hero information
     */
    private fun setupShareButton(shareButton: Button) {
        shareButton.setOnClickListener {
            // Create share intent
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this hero: ${hero.name}")
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this hero: ${hero.name}\n${hero.description}"
            )
            startActivity(Intent.createChooser(shareIntent, "Share Hero"))
        }
    }
    
    companion object {
        private const val ARG_HERO = "hero"
        
        /**
         * Creates a new instance of HeroDetailsDialog with the specified hero
         * @param hero The hero to display details for
         * @return A new instance of HeroDetailsDialog
         */
        fun newInstance(hero: Hero): HeroDetailsDialog {
            val fragment = HeroDetailsDialog()
            val args = Bundle()
            args.putParcelable(ARG_HERO, hero)
            fragment.arguments = args
            return fragment
        }
    }
}