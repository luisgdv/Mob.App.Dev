package com.example.marvel_heroes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marvel_heroes.data.HeroDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragment for displaying favorite heroes.
 * Shows a grid of favorite heroes or an empty state if no favorites exist.
 */
class FavoritesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HeroAdapter
    private lateinit var db: HeroDatabase
    private lateinit var emptyView: TextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the favorites fragment layout
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        Log.d("FavoritesFragment", "onViewCreated called")
        
        // Initialize database from MainActivity
        db = (requireActivity() as MainActivity).getDatabase()
        
        // Set up RecyclerView with grid layout
        recyclerView = view.findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        
        // Get reference to empty state view
        emptyView = view.findViewById(R.id.emptyFavoritesView)
        
        // Initialize adapter with empty list and click handler
        adapter = HeroAdapter(emptyList()) { hero ->
            // Show hero details when a hero is clicked
            HeroDetailsDialog.newInstance(hero).show(parentFragmentManager, "heroDetails")
        }
        recyclerView.adapter = adapter
        
        // Load favorite heroes
        loadFavorites()
    }
    
    override fun onResume() {
        super.onResume()
        // Reload favorites when fragment becomes visible
        loadFavorites()
    }
    
    /**
     * Loads favorite heroes from the database and updates the UI
     */
    fun loadFavorites() {
        Log.d("FavoritesFragment", "loadFavorites called")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get all heroes for debugging
                val allHeroes = db.heroDao().getAllHeroes()
                Log.d("FavoritesFragment", "Total heroes in database: ${allHeroes.size}")
                
                // Get favorites
                val favorites = db.heroDao().getAllFavorites()
                Log.d("FavoritesFragment", "Found ${favorites.size} favorites")
                
                // Log each favorite for debugging
                favorites.forEach { 
                    Log.d("FavoritesFragment", "Favorite: ${it.name} (ID: ${it.id}), isFavorite=${it.isFavorite}")
                }
                
                // Update UI on main thread
                withContext(Dispatchers.Main) {
                    if (favorites.isEmpty()) {
                        // Show empty state when no favorites exist
                        Log.d("FavoritesFragment", "No favorites found, showing empty view")
                        emptyView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        // Show favorites in RecyclerView
                        Log.d("FavoritesFragment", "Showing ${favorites.size} favorites in RecyclerView")
                        emptyView.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        adapter.submitList(favorites)
                    }
                }
            } catch (e: Exception) {
                Log.e("FavoritesFragment", "Error loading favorites", e)
                e.printStackTrace()
            }
        }
    }
}