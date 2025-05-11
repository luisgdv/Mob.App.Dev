package com.example.marvel_heroes

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.example.marvel_heroes.viewmodel.MarvelViewModel

/**
 * Dialog fragment that allows users to filter Marvel heroes by different criteria.
 * Provides options to sort heroes by name, intelligence, and strength in ascending or descending order.
 */
class FilterDialog(private val viewModel: MarvelViewModel) : DialogFragment() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set dialog style to Material Light theme
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the filter dialog layout
        return inflater.inflate(R.layout.dialog_filter, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set dialog title
        dialog?.setTitle("Filter Heroes")
        
        // Get references to UI components
        val nameRadioGroup = view.findViewById<RadioGroup>(R.id.nameRadioGroup)
        val intelligenceRadioGroup = view.findViewById<RadioGroup>(R.id.intelligenceRadioGroup)
        val strengthRadioGroup = view.findViewById<RadioGroup>(R.id.strengthRadioGroup)
        // Find the apply filter button
        val applyFilterButton = view.findViewById<Button>(R.id.applyFilterButton)
        applyFilterButton.setOnClickListener {
            applyFilters()
            dismiss()
        }
        
        // Find the cancel button
        val cancelFilterButton = view.findViewById<Button>(R.id.cancelFilterButton)
        cancelFilterButton.setOnClickListener {
            dismiss()
        }
    }
    
    /**
     * Applies the selected filters to the ViewModel
     */
    private fun applyFilters() {
        // Get the selected radio buttons
        val selectedNameOption = when {
            view?.findViewById<RadioButton>(R.id.nameAscending)?.isChecked == true -> SortOption.NAME_ASC
            view?.findViewById<RadioButton>(R.id.nameDescending)?.isChecked == true -> SortOption.NAME_DESC
            else -> null
        }
        
        val selectedIntelligenceOption = when {
            view?.findViewById<RadioButton>(R.id.intelligenceAscending)?.isChecked == true -> SortOption.INTELLIGENCE_ASC
            view?.findViewById<RadioButton>(R.id.intelligenceDescending)?.isChecked == true -> SortOption.INTELLIGENCE_DESC
            else -> null
        }
        
        val selectedStrengthOption = when {
            view?.findViewById<RadioButton>(R.id.strengthAscending)?.isChecked == true -> SortOption.STRENGTH_ASC
            view?.findViewById<RadioButton>(R.id.strengthDescending)?.isChecked == true -> SortOption.STRENGTH_DESC
            else -> null
        }
        
        // Apply the first non-null sort option
        val sortOption = selectedNameOption ?: selectedIntelligenceOption ?: selectedStrengthOption
        
        // Apply the sort option to the ViewModel
        sortOption?.let { viewModel.applySortOption(it) }
    }
    
    companion object {
        /**
         * Factory method to create a new instance of FilterDialog.
         * @param viewModel The ViewModel that will handle the filtering operations
         * @return A new instance of FilterDialog
         */
        fun newInstance(viewModel: MarvelViewModel): FilterDialog {
            return FilterDialog(viewModel)
        }
    }
}

/**
 * Enum representing different sort options for heroes
 */
enum class SortOption {
    NAME_ASC,
    NAME_DESC,
    INTELLIGENCE_ASC,
    INTELLIGENCE_DESC,
    STRENGTH_ASC,
    STRENGTH_DESC
}