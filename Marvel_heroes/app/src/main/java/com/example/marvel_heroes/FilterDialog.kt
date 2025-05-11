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
        val applyFilterButton = view.findViewById<Button>(R.id.applyFilterButton)
        
        // Set click listener for the apply button
        applyFilterButton.setOnClickListener {
            // Apply name filter based on selected radio button
            when (nameRadioGroup.checkedRadioButtonId) {
                R.id.nameAscending -> viewModel.filterHeroesByNameAscending()
                R.id.nameDescending -> viewModel.filterHeroesByNameDescending()
            }
            
            // Apply intelligence filter based on selected radio button
            when (intelligenceRadioGroup.checkedRadioButtonId) {
                R.id.intelligenceAscending -> viewModel.filterHeroesByIntelligenceAscending()
                R.id.intelligenceDescending -> viewModel.filterHeroesByIntelligenceDescending()
            }
            
            // Apply strength filter based on selected radio button
            when (strengthRadioGroup.checkedRadioButtonId) {
                R.id.strengthAscending -> viewModel.filterHeroesByStrengthAscending()
                R.id.strengthDescending -> viewModel.filterHeroesByStrengthDescending()
            }
            
            // Close the dialog after applying filters
            dismiss()
        }
        
        // Add cancel button functionality if it exists in the layout
        view.findViewById<Button>(R.id.cancelFilterButton)?.setOnClickListener {
            dismiss()
        }
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