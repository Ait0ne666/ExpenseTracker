package com.ait0ne.expensetracker

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.ContentObserver
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ait0ne.expensetracker.api.RetrofitInstance
import com.ait0ne.expensetracker.databinding.ActivityMainBinding
import com.ait0ne.expensetracker.db.ExpenseDB
import com.ait0ne.expensetracker.fragments.AuthFragmentDirections
import com.ait0ne.expensetracker.repositories.AuthRepository
import com.ait0ne.expensetracker.repositories.CategoriesRepository
import com.ait0ne.expensetracker.repositories.ExpensesRepository
import com.ait0ne.expensetracker.repositories.LocalRepository
import com.ait0ne.expensetracker.syncadapter.SyncAdapter
import com.ait0ne.expensetracker.syncadapter.SyncService
import com.ait0ne.expensetracker.ui.viewmodels.*
import kotlinx.android.synthetic.main.activity_main.*


const val AUTHORITY = "com.ait0ne.expensetracker.provider"

// An account type, in the form of a domain name
const val ACCOUNT_TYPE = "com.ait0ne.expensetracker.heroku"

// The account name
const val ACCOUNT = "user"


class MainActivity : AppCompatActivity() {
    lateinit var mAccount: Account
    lateinit var addExpenseViewModel: AddExpenseViewModel
    lateinit var expensesListViewModel: ExpensesListViewModel
    lateinit var expenseViewModel: ExpenseViewModel
    lateinit var chartsViewModel: ChartsViewModel
    lateinit var sharedViewModel: SharedViewModel
    lateinit var authViewModel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

//        setupSyncService()


        val prefs = getSharedPreferences(
            "com.ait0ne.expensetracker", Context.MODE_PRIVATE
        )


        val isLoggedIn = prefs.getString("jwt", "") != ""


        val retrofit = RetrofitInstance(prefs)

        var resolver = contentResolver





        //viewmodels setup
        val expensesRepository = ExpensesRepository(ExpenseDB(this), retrofit, resolver)
        val categoriesRepository = CategoriesRepository(retrofit)
        val authRepository = AuthRepository(retrofit)
        val localRepository = LocalRepository(prefs)
        val viewModelProviderFactory =
            AddExpenseVieModelFactory(expensesRepository, categoriesRepository, localRepository)
        addExpenseViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(AddExpenseViewModel::class.java)


        val expensesViewModelProviderFactory = ExpensesListViewModelFactory(
            expensesRepository,
            categoriesRepository,
            localRepository.getCurrency()
        )
        expensesListViewModel = ViewModelProvider(
            this,
            expensesViewModelProviderFactory
        ).get(ExpensesListViewModel::class.java)

        val expenseViewModelProviderFactory =
            ExpenseViewModelFactory(expensesRepository, categoriesRepository, localRepository)
        expenseViewModel = ViewModelProvider(
            this,
            expenseViewModelProviderFactory
        ).get(ExpenseViewModel::class.java)

        val chartsViewModelProviderFactory =
            ChartsVieModelFactory(expensesRepository, localRepository)
        chartsViewModel =
            ViewModelProvider(this, chartsViewModelProviderFactory).get(ChartsViewModel::class.java)


        val authViewModelProviderFactory = AuthVieModelFactory(authRepository)
        authViewModel =
            ViewModelProvider(this, authViewModelProviderFactory).get(AuthViewModel::class.java)

        val sharedViewModelProviderFactory = SharedViewModelFactory(localRepository)
        sharedViewModel =
            ViewModelProvider(this, sharedViewModelProviderFactory).get(SharedViewModel::class.java)


        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this


        localRepository.currency.observe(this) {
            addExpenseViewModel.currency.postValue(it)
            expensesListViewModel.currency.postValue(it)
            expenseViewModel.currency.postValue(it)
            chartsViewModel.currency.postValue(it)
        }

        //visual
        supportActionBar?.hide()

        val navController = mainNavHostFragment.findNavController()
        bottomNavigationView.setupWithNavController(navController)

        if (isLoggedIn) {
            navController.navigate(AuthFragmentDirections.actionAuthFragmentToMainFragment())

        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.authFragment -> {
                    bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
        window.statusBarColor = getColor(R.color.background)
    }


    private fun setupSyncService() {

        mAccount = createSyncAccount()

        val intent = Intent(applicationContext, SyncService::class.java)

        applicationContext.startService(intent)


//            ContentResolver.requestSync(
//                mAccount,
//                AUTHORITY,
//                Bundle.EMPTY
//            )


        ContentResolver.addPeriodicSync(
            mAccount,
            AUTHORITY,
            Bundle.EMPTY,
            3600L
        )


    }


    private fun createSyncAccount(): Account {
        val accountManager = getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        return Account(ACCOUNT, ACCOUNT_TYPE).also { newAccount ->
            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (accountManager.addAccountExplicitly(newAccount, null, null)) {
                /*
                 * If you don't set android:syncable="true" in
                 * in your <provider> element in the manifest,
                 * then call context.setIsSyncable(account, AUTHORITY, 1)
                 * here.
                 */
            } else {
                /*
                 * The account exists or some other error occurred. Log this, report it,
                 * or handle it internally.
                 */
            }


            return newAccount
        }
    }


}