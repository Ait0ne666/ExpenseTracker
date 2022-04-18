package com.ait0ne.expensetracker.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ait0ne.expensetracker.ui.bottomsheetpicker.BottomSheetPicker
import com.ait0ne.expensetracker.ui.bottomsheetpicker.SelectOption
import kotlinx.android.synthetic.main.fragment_main.*
import android.graphics.drawable.Drawable

import android.graphics.drawable.shapes.RectShape

import android.graphics.drawable.PaintDrawable

import android.graphics.Shader

import android.graphics.LinearGradient

import android.graphics.drawable.ShapeDrawable.ShaderFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ait0ne.expensetracker.MainActivity
import com.ait0ne.expensetracker.R
import com.ait0ne.expensetracker.ui.viewmodels.AddExpenseViewModel
import android.content.ClipData.Item

import android.content.ClipData
import android.opengl.Visibility
import android.text.Editable
import android.text.TextWatcher
import android.widget.AutoCompleteTextView

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.ait0ne.expensetracker.databinding.FragmentMainBinding
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.widget.AppCompatButton
import com.ait0ne.expensetracker.ui.viewmodels.FormFields
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import com.ait0ne.expensetracker.models.Currency
import java.util.*


class MainFragment : Fragment(com.ait0ne.expensetracker.R.layout.fragment_main) {

    private lateinit var viewmodel: AddExpenseViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentMainBinding =
            DataBindingUtil.inflate(
                inflater,
                com.ait0ne.expensetracker.R.layout.fragment_main,
                container,
                false
            )
        viewmodel = (activity as MainActivity).addExpenseViewModel
        binding.viewModel = viewmodel
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setupView()
        viewmodel.updateTotal()

        viewmodel.options.observe(viewLifecycleOwner) { categories ->
            val categoryPicker = view.findViewById<AutoCompleteTextView>(R.id.atvCategory)
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categories
            )

            categoryPicker.setAdapter(adapter)
        }

        viewmodel.loading.observe(viewLifecycleOwner) { loading ->
            toggleExpenseLoader(loading)
        }

        viewmodel.categoryError.observe(viewLifecycleOwner) { error ->

            val container = view.findViewById<TextInputLayout>(R.id.atvCategoryContainer)



            if (error == null) {
                container.isErrorEnabled = false
            } else {
                container.error = getString(error)
            }
        }

        viewmodel.amountError.observe(viewLifecycleOwner) { error ->

            val container = view.findViewById<TextInputLayout>(R.id.etAmountContainer)



            if (error == null) {
                container.isErrorEnabled = false
            } else {
                container.error = getString(error)
            }
        }

    }


    private fun setupView() {
        val categoryPicker = view?.findViewById<AutoCompleteTextView>(R.id.atvCategory)

        categoryPicker?.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                categoryPicker.showDropDown()
            }
        }


        categoryPicker?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            hideKeyboardFrom(requireContext(), requireView())
        }


        categoryPicker?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewmodel.clearFieldError(FormFields.CATEGORY)
            }
        });

        val amountContainer = view?.findViewById<TextInputEditText>(R.id.etAmount)

        amountContainer?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewmodel.clearFieldError(FormFields.AMOUNT)
            }
        });


        val datepicker = view?.findViewById<TextInputEditText>(R.id.etDate)

        datepicker?.setOnClickListener {
            showDatePicker()
        }


        viewmodel.onError { error ->

            Snackbar.make(requireView(), getString(error), Snackbar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.error))
                .setTextColor(requireContext().getColor(R.color.white))
                .show()
        }

        viewmodel.onSuccess { success ->

            Snackbar.make(requireView(), getString(success), Snackbar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.success))
                .setTextColor(requireContext().getColor(R.color.white))
                .show()
        }

        viewmodel.onButtonClick {
            hideKeyboardFrom(requireContext(), requireView())
        }


        tvCurrency.setOnClickListener {
            showCurrencyPicker()
        }


        etAmount.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawableForCurrency(viewmodel.currency.value!!), null, null, null)


        viewmodel.currency.observe(viewLifecycleOwner) {
            etAmount.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawableForCurrency(it), null, null, null)
        }
    }


    private fun toggleExpenseLoader(value: Boolean) {
        val loader = view?.findViewById<ProgressBar>(R.id.expensesLoader)

        loader?.let {
            it.apply {
                visibility = if (value) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            }
        }
    }


    fun hideKeyboardFrom(context: Context, view: View) {

        val focus = view.findFocus()
        focus.clearFocus()
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showDatePicker() {
        val date = viewmodel.addExpenseFormState.value!!.date
        val year = date.year
        val month = date.month
        val day = date.date

        val datepicker = DatePickerDialog(
            requireContext(),
            { view, year, month, dayOfMonth ->
                val new_date = Date()
                new_date.date = dayOfMonth
                new_date.year = year - 1900
                new_date.month = month
                viewmodel.changeDate(new_date)

            },
            year +1900,
            month,
            day
        )



        datepicker.datePicker.maxDate = Calendar.getInstance().timeInMillis


        datepicker.show()

    }



    private fun showCurrencyPicker() {
        val options:MutableList<SelectOption<Currency>> = mutableListOf()

        options.add(SelectOption(Currency.RUB, Currency.RUB.text.uppercase()))
        options.add(SelectOption(Currency.USD, Currency.USD.text.uppercase()))
        options.add(SelectOption(Currency.EUR, Currency.EUR.text.uppercase()))
        options.add(SelectOption(Currency.THB, Currency.THB.text.uppercase()))

        val picker = BottomSheetPicker<Currency>(options) {
            viewmodel.changeCurrency(it)
        }


        picker.show(parentFragmentManager, "")

    }


    private fun getDrawableForCurrency(currency: Currency): Drawable? {
        when(currency) {
            Currency.THB -> {
                return requireContext().getDrawable(R.drawable.ic_tbh)
            }
            Currency.EUR -> {
                return requireContext().getDrawable(R.drawable.ic_euro)
            }
            Currency.RUB -> {
                return requireContext().getDrawable(R.drawable.ic_ruble)
            }
            Currency.USD -> {
                return requireContext().getDrawable(R.drawable.ic_dollar)
            }
        }
    }
}