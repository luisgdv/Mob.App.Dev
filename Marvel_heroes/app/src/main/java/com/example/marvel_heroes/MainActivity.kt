package com.example.marvel_heroes

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.marvel_heroes.api.MarvelService
import com.example.marvel_heroes.viewmodel.MarvelViewModel
import com.example.marvel_heroes.viewmodel.MarvelViewModelFactory
import com.example.marvel_heroes.data.HeroDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main activity for the Marvel Heroes application.
 * Handles the display of heroes, search functionality, and navigation between heroes and favorites.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MarvelViewModel
    private lateinit var adapter: HeroAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        
        // Initialize ViewModel with database
        viewModel = ViewModelProvider(this, MarvelViewModelFactory(getDatabase()))[MarvelViewModel::class.java]
        
        setupRecyclerView()
        setupSwipeRefresh()
        setupSearchView()
        setupObservers()
        setupBottomNavigation()
        setupFilterButton()
        
        // Load heroes on startup
        loadMarvelCharacters()
    }
    
    /**
     * Sets up the RecyclerView and its adapter
     */
    private fun setupRecyclerView() {
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)
        adapter = HeroAdapter(emptyList()) { hero ->
            // Show hero details when a hero is clicked
            HeroDetailsDialog.newInstance(hero).show(supportFragmentManager, "heroDetails")
        }
        
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }
    
    /**
     * Sets up the SwipeRefreshLayout for pull-to-refresh functionality
     */
    private fun setupSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        swipeRefreshLayout.setOnRefreshListener {
            loadMarvelCharacters()
        }
    }
    
    /**
     * Sets up the search functionality
     */
    private fun setupSearchView() {
        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchHeroes(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { 
                    if (it.length >= 3) {
                        searchHeroes(it)
                    } else if (it.isEmpty()) {
                        // If search is cleared, show all heroes
                        viewModel.heroes.value?.let { heroes -> adapter.submitList(heroes) }
                    }
                }
                return true
            }
        })
    }
    
    /**
     * Sets up observers for LiveData
     */
    private fun setupObservers() {
        viewModel.heroes.observe(this) { heroes ->
            adapter.submitList(heroes)
            findViewById<View>(R.id.progressBar).visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
    }
    
    /**
     * Sets up the bottom navigation bar
     */
    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_heroes -> {
                    showHeroesView()
                    true
                }
                R.id.navigation_favorites -> {
                    showFavoritesView()
                    true
                }
                else -> false
            }
        }
        
        // Set the default selected item
        bottomNavigation.selectedItemId = R.id.navigation_heroes
    }
    
    /**
     * Shows the heroes view and hides the favorites view
     */
    private fun showHeroesView() {
        findViewById<View>(R.id.mainContent).visibility = View.VISIBLE
        supportFragmentManager.findFragmentById(R.id.favoritesContainer)?.let {
            supportFragmentManager.beginTransaction().hide(it).commit()
        }
    }
    
    /**
     * Shows the favorites view and hides the heroes view
     */
    private fun showFavoritesView() {
        findViewById<View>(R.id.mainContent).visibility = View.GONE
        
        // Check if the favoritesContainer exists
        val favoritesContainer = findViewById<View>(R.id.favoritesContainer)
        if (favoritesContainer == null) {
            Log.e("MainActivity", "favoritesContainer view not found!")
            return
        }
        
        // Debug database contents
        debugDatabaseContents()
        
        // Show or create favorites fragment
        var fragment = supportFragmentManager.findFragmentById(R.id.favoritesContainer)
        if (fragment == null) {
            Log.d("MainActivity", "Creating new FavoritesFragment")
            fragment = FavoritesFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.favoritesContainer, fragment)
                .commit()
        } else {
            Log.d("MainActivity", "Showing existing FavoritesFragment")
            supportFragmentManager.beginTransaction().show(fragment).commit()
            // Force reload of favorites
            (fragment as FavoritesFragment).loadFavorites()
        }
    }
    
    /**
     * Sets up the filter button
     */
    private fun setupFilterButton() {
        val filterButton = findViewById<ImageButton>(R.id.filterButton)
        filterButton.setOnClickListener {
            FilterDialog.newInstance(viewModel).show(supportFragmentManager, "filterDialog")
        }
    }
    
    /**
     * Logs database contents for debugging
     */
    private fun debugDatabaseContents() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allHeroes = getDatabase().heroDao().getAllHeroes()
                val favorites = getDatabase().heroDao().getAllFavorites()
                Log.d("MainActivity", "Total heroes in database: ${allHeroes.size}")
                Log.d("MainActivity", "Total favorites in database: ${favorites.size}")
                
                favorites.forEach {
                    Log.d("MainActivity", "Favorite: ${it.name}, ID: ${it.id}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error querying database", e)
            }
        }
    }
    
    /**
     * Loads Marvel characters from the API
     */
    private fun loadMarvelCharacters() {
        // Show loading indicator
        findViewById<View>(R.id.progressBar).visibility = View.VISIBLE
        
        // Make API request
        viewModel.loadHeroes()
    }
    
    /**
     * Searches heroes by name
     * @param query The search query
     */
    private fun searchHeroes(query: String) {
        Log.d("MainActivity", "Searching for: $query")
        viewModel.heroes.value?.let { allHeroes ->
            val filteredHeroes = allHeroes.filter { 
                it.name.contains(query, ignoreCase = true) 
            }
            Log.d("MainActivity", "Found ${filteredHeroes.size} heroes matching '$query'")
            adapter.submitList(filteredHeroes)
        }
    }
    
    companion object {
        private var databaseInstance: HeroDatabase? = null
    }
    
    /**
     * Gets the database instance, creating it if necessary
     * @return The database instance
     */
    fun getDatabase(): HeroDatabase {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(
                applicationContext,
                HeroDatabase::class.java,
                "hero_database"
            ).build()
            Log.d("MainActivity", "Created new database instance")
        }
        return databaseInstance!!
    }
}
