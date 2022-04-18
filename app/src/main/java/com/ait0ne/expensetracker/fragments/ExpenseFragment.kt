package com.ait0ne.expensetracker.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ait0ne.expensetracker.MainActivity
import com.ait0ne.expensetracker.R
import com.ait0ne.expensetracker.databinding.FragmentExpenseBinding
import com.ait0ne.expensetracker.databinding.FragmentListBinding
import com.ait0ne.expensetracker.models.ExpenseDTO
import com.ait0ne.expensetracker.ui.viewmodels.ExpenseViewModel
import android.graphics.Shader.TileMode

import android.graphics.LinearGradient

import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.ui.viewmodels.FormFields
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*


class ExpenseFragment(val expense: ExpenseDTO, val successCallback: () -> Unit): DialogFragment(){

    lateinit var viewmodel: ExpenseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setStyle(STYLE_NORMAL, R.style.DialogTheme)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentExpenseBinding =
            DataBindingUtil.inflate(
                inflater,
                com.ait0ne.expensetracker.R.layout.fragment_expense,
                container,
                false
            )
        viewmodel = (activity as MainActivity).expenseViewModel
        binding.viewModel = viewmodel
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupView()

        viewmodel.setInitialExpense(expense)


        val textShader: Shader = LinearGradient(
            0.0f,
            0.0f,
            0.0f,
            etAmount.lineHeight.toFloat(),
            intArrayOf(requireContext().getColor(R.color.gradientStart), requireContext().getColor(R.color.gradientCenter)),
            floatArrayOf(0f,  1f),
            TileMode.REPEAT
        )
        etAmount.paint.shader = textShader



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

        viewmodel.onSuccess {

            successCallback()
            dismiss()
        }

        viewmodel.onButtonClick {
            hideKeyboardFrom(requireContext(), requireView())
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
        val date = viewmodel.expense.value!!.date
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