package com.ait0ne.expensetracker.ui.bottomsheetpicker

import com.google.android.material.bottomsheet.BottomSheetDialogFragment


import android.os.Bundle

import android.view.ViewGroup

import android.view.LayoutInflater
import android.view.View
import com.ait0ne.expensetracker.R
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import android.widget.Toast

import android.widget.TextView

import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import java.util.*


class SelectOption<T>(public final val value: T, public final val text: String) {

}


class BottomSheetPicker<T>(private val options: List<SelectOption<T>> = listOf(), private val onChange: (value: T) -> Unit): BottomSheetDialogFragment() {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val optionTexts = options.map {
            it.text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.custom_list_item, optionTexts
        )

        lvBottomSheetOptions.adapter = adapter

        lvBottomSheetOptions.setOnItemClickListener { parent, itemClicked, position, id ->
            val value = options[position]

            onChange(value.value)
            this.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false)
    }

}