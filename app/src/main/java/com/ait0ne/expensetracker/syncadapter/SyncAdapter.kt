package com.ait0ne.expensetracker.syncadapter

import android.accounts.Account
import android.content.*
import android.os.Bundle
import android.util.Log
import com.ait0ne.expensetracker.api.RetrofitInstance
import com.ait0ne.expensetracker.models.Category
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.Expense
import com.ait0ne.expensetracker.models.ExpenseDTO
import com.ait0ne.expensetracker.models.dto.SyncDTO
import com.ait0ne.expensetracker.providers.AppContentProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant.now
import java.util.*

class SyncAdapter @JvmOverloads constructor(
    context: Context,
    autoInitialize: Boolean,
    /**
     * Using a default argument along with @JvmOverloads
     * generates constructor for both method signatures to maintain compatibility
     * with Android 3.0 and later platform versions
     */
    allowParallelSyncs: Boolean = false,
    /*
     * If your app uses a content resolver, get an instance of it
     * from the incoming Context
     */
    val mContentResolver: ContentResolver = context.contentResolver
) : AbstractThreadedSyncAdapter(context, autoInitialize, allowParallelSyncs) {
    override fun onPerformSync(
        p0: Account?,
        p1: Bundle?,
        p2: String?,
        provider: ContentProviderClient?,
        p4: SyncResult?
    ) {
        Log.i("Sync_Adapter", "SYNC")
        val prefs = context.getSharedPreferences("com.ait0ne.expensetracker", Context.MODE_PRIVATE)

        val api =  RetrofitInstance(prefs).api

        val expense = ExpenseDTO(
            id = "adc",
            amount = 500f,
            date = Date(),
            currency = Currency.RUB,
            category_id = "Фигня",
            title = null,
            category = Category(id = "игня", title = "Фигня")
        )

        GlobalScope.launch {
             val response = api.sync(SyncDTO(expenses = listOf(expense)))

            if (response.isSuccessful) {
                response.body()?.let {
                    val expense = it.expenses[0]

                    val exp = Expense(
                        title = expense.title,
                        category_id = expense.category_id,
                        id = null,
                        cloud_id = expense.id,
                        currency = expense.currency,
                        date = expense.date,
                        amount = expense.amount
                    )

                    val id = provider?.insert(AppContentProvider.URI, Expense.toContentValues(exp))
                    Log.i("Sync_Adapter", id.toString())
                }
            }


        }






    }

}