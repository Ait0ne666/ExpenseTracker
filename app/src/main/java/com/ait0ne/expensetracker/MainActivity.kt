package com.ait0ne.expensetracker

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ait0ne.expensetracker.databinding.ActivityMainBinding
import com.ait0ne.expensetracker.db.ExpenseDB
import com.ait0ne.expensetracker.repositories.CategoriesRepository
import com.ait0ne.expensetracker.repositories.ExpensesRepository
import com.ait0ne.expensetracker.ui.viewmodels.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var addExpenseViewModel: AddExpenseViewModel
    lateinit var expensesListViewModel: ExpensesListViewModel
    lateinit var expenseViewModel: ExpenseViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)


        //viewmodels setup
        val expensesRepository = ExpensesRepository(ExpenseDB(this))
        val categoriesRepository = CategoriesRepository()
        val viewModelProviderFactory = AddExpenseVieModelFactory(expensesRepository, categoriesRepository)
        addExpenseViewModel = ViewModelProvider(this, viewModelProviderFactory).get(AddExpenseViewModel::class.java)



        val expensesViewModelProviderFactory = ExpensesListViewModelFactory(expensesRepository, categoriesRepository)
        expensesListViewModel = ViewModelProvider(this, expensesViewModelProviderFactory).get(ExpensesListViewModel::class.java)

        val expenseViewModelProviderFactory = ExpenseViewModelFactory(expensesRepository, categoriesRepository)
        expenseViewModel = ViewModelProvider(this, expenseViewModelProviderFactory).get(ExpenseViewModel::class.java)




        val binding : ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this


        //visual
        supportActionBar?.hide()
        bottomNavigationView.setupWithNavController(mainNavHostFragment.findNavController())
        window.statusBarColor = getColor(R.color.background)
    }










}