package com.ait0ne.expensetracker.fragments

import android.app.ActionBar
import android.app.DatePickerDialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ait0ne.expensetracker.MainActivity
import com.ait0ne.expensetracker.R
import com.ait0ne.expensetracker.databinding.FragmentListBinding
import com.ait0ne.expensetracker.databinding.FragmentMainBinding
import com.ait0ne.expensetracker.ui.adapters.ExpensesAdapter
import com.ait0ne.expensetracker.ui.viewmodels.AddExpenseViewModel
import com.ait0ne.expensetracker.ui.viewmodels.ExpensesListViewModel
import com.ait0ne.expensetracker.utils.Resource
import com.ait0ne.expensetracker.utils.RvItemDivider
import kotlinx.android.synthetic.main.fragment_list.*
import android.widget.AdapterView
import android.widget.LinearLayout

import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.ui.bottomsheetpicker.BottomSheetPicker
import com.ait0ne.expensetracker.ui.bottomsheetpicker.SelectOption
import com.ait0ne.expensetracker.utils.DateUtils
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class ListFragment: Fragment(R.layout.fragment_list) {

    private lateinit var viewmodel: ExpensesListViewModel
    private lateinit var expensesAdapter: ExpensesAdapter
    val TAG = "Expenses list"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentListBinding =
            DataBindingUtil.inflate(
                inflater,
                com.ait0ne.expensetracker.R.layout.fragment_list,
                container,
                false
            )
        viewmodel = (activity as MainActivity).expensesListViewModel
        binding.viewModel = viewmodel
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerview()



        viewmodel.expenses.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { response ->
                        expensesAdapter.differ.submitList(response.expenses.toList())
                        tvMonthTotal.text = response.month_total.toInt().toString() + Currency.symbol(viewmodel.currency.value!!)
                    }

                    rvExpensesList.layoutManager?.scrollToPosition(0)

                }
                is Resource.Error -> {
                    hideProgressBar()

                    response.message?.let { message ->
                        Log.e(TAG, "An error occured $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })




        viewmodel.options.observe(viewLifecycleOwner) {
            sCategory.adapter = ArrayAdapter(requireContext(), R.layout.custom_dropdown_item, it)
            sCategory.setSelection(viewmodel.selectedCategory.value ?: 0, true)


            sCategory.onItemSelectedListener =object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    viewmodel.selectedCategory.postValue(p2)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
        }


        viewmodel.selectedCategory.observe(viewLifecycleOwner) {
            viewmodel.updateExpenses()
        }




        tvCurrentMonth.setOnClickListener {
            showDatePicker()
        }

        expensesAdapter.setOnItemClickListener { expense ->
            val dialog = ExpenseFragment(expense) {
//                viewmodel.updateData()
            }
            dialog.show(parentFragmentManager, "")
        }



    }





    private fun setupRecyclerview() {
        expensesAdapter = ExpensesAdapter()
        rvExpensesList.apply {
            addItemDecoration(RvItemDivider(20, 0))
            adapter = expensesAdapter
            layoutManager = LinearLayoutManager(activity)

        }

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition


                viewmodel.deleteExpense(position)


            }

        }).attachToRecyclerView(rvExpensesList)





    }


    private fun hideProgressBar() {
        pbExpensesLoader.visibility = View.GONE
    }


    private fun showProgressBar() {
        pbExpensesLoader.visibility = View.VISIBLE
    }


    fun showDatePicker() {
        val currentDate = LocalDateTime.now()
        val months = mutableListOf<SelectOption<Date>>()


        for (i  in 0..11) {
            val month = currentDate.minusMonths(i.toLong())
            val date = Date()
            date.month = month.month.value - 1
            date.year = month.year - 1900

            months.add(SelectOption(date, DateUtils.months[date.month] + " " +  (date.year + 1900)))
        }


        val picker = BottomSheetPicker<Date>(months) {
            viewmodel.changeMonth(it)

        }

        picker.show(parentFragmentManager, "")
    }

}