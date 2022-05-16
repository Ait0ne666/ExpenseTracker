package com.ait0ne.expensetracker.syncadapter

import android.accounts.Account
import android.content.*
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.ait0ne.expensetracker.api.RetrofitInstance
import com.ait0ne.expensetracker.db.DbUtils
import com.ait0ne.expensetracker.models.*
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.dto.SyncDTO
import com.ait0ne.expensetracker.providers.AppContentProvider
import com.ait0ne.expensetracker.utils.DateUtils
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

        val api = RetrofitInstance(prefs).api


        val cursor =
            mContentResolver.query(AppContentProvider.URI, arrayOf("true"), "dirty = ?", null, null)
        val categoriesCursor =
            mContentResolver.query(
                AppContentProvider.CATEGORIES_URI,
                arrayOf("true"),
                "dirty = ?",
                null,
                null
            )

        var categories = mutableListOf<Category>()


        categoriesCursor?.let {
            categories = DbUtils.parseCategoriesCursor(categoriesCursor)
        }

        var categoriesMap = mutableMapOf<String, Category>()


        categories.forEach {
            categoriesMap[it.title.lowercase()] = it;
        }

        var expenses = mutableListOf<ExpenseFromApi>()

        cursor?.let {
            expenses = DbUtils.parseCursor(cursor)


            Log.i("Sync_Adapter", expenses.size.toString())
        }

        runBlocking {
            var lastSync = prefs.getString("lastSync", null)


            var lastSyncDate: Date? = null

            lastSync?.let {

                val formatter = DateUtils.isoFormatter
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                lastSyncDate = formatter.parse(lastSync)
                lastSync = formatter.format(lastSyncDate)
            }


            Log.i("Sync_Adapter", "last_sync" + lastSync ?: "")

            val response = api.sync(
                SyncDTO(
                    last_sync = lastSync,
                    expenses = expenses
                )
            )

            var shouldUpdateSync = true

            try {
                if (response.isSuccessful) {
                    response.body()?.let { syncResponse ->
                        val expensesToUpdate = syncResponse.expenses
                        val ratesToUpdate = syncResponse.rates

                        Log.i("Sync_Adapter", "expenses_to_update" + expensesToUpdate.size ?: "")
                        expensesToUpdate.forEach { expense ->
                            if (expense.id == null) {

                                val created = createExpense(expense, categoriesMap)


                                if (created == null) {
                                    shouldUpdateSync = false
                                }


                            } else {
                                val updated = updateExpense(expense, categoriesMap)

                                if (updated == null) {
                                    shouldUpdateSync = false
                                }
                            }

                        }

                        if (shouldUpdateSync) {
                            putLastSync(Date())
                        }
                        putRates(ratesToUpdate)
                        Log.i("Sync_Adapter", "success")
                    }
                } else {
                    Log.e("Sync_Adapter", response.raw().toString())
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Sync_Adapter", e.message ?: "")
            }


        }


    }

    fun putLastSync(lastSync: Date) {

        val sharedPreferences =
            context.getSharedPreferences("com.ait0ne.expensetracker", Context.MODE_PRIVATE)
        val dateString = com.ait0ne.expensetracker.utils.DateUtils.isoFormatter.format(lastSync)

        sharedPreferences.edit().putString("lastSync", dateString).apply()
    }


    fun putRates(rates: CurrencyRates) {
        val sharedPreferences =
            context.getSharedPreferences("com.ait0ne.expensetracker", Context.MODE_PRIVATE)
        val builder = GsonBuilder()
        val gson = builder.create()

        val json = gson.toJson(rates)


        sharedPreferences.edit().putString("rates", json).apply()
    }

    fun createExpense(expense: ExpenseFromApi, categories: Map<String, Category>): Expense? {

        expense.cloud_id?.let { cloud_id ->
            val existingExpense = getExpenseByCloudId(cloud_id)


            existingExpense?.let {
                return updateExpense(expense.copy(id = existingExpense.id), categories)
            }
        }


        val categoryid = if (categories.containsKey(expense.category_name.lowercase())) {
            categories[expense.category_name.lowercase()]!!.id
        } else {
            createCategory(expense.category_name, expense.category_id)
        }


        categoryid?.let {
            val expense = Expense(
                id = null,
                title = expense.title,
                created_at = DateUtils.isoFormatter.parse(expense.created_at),
                updated_at = DateUtils.isoFormatter.parse(expense.updated_at),
                dirty = false,
                deleted_at = if (expense.deleted_at != null) {
                    DateUtils.isoFormatter.parse(expense.deleted_at)
                } else null,
                cloud_id = expense.cloud_id,
                currency = expense.currency,
                date = DateUtils.isoFormatter.parse(expense.date),
                amount = expense.amount,
                category_id = categoryid
            )


            val URI =
                mContentResolver.insert(AppContentProvider.URI, Expense.toContentValues(expense))

            val id = URI?.lastPathSegment?.toInt()

            return expense.copy(id = id)
        }


        return null


    }


    fun getExpenseByCloudId(id: String): ExpenseFromApi? {
        val uri = ContentUris.withAppendedId(AppContentProvider.URI, 1)
        val cursor = mContentResolver.query(uri, null, id, null, null)

        cursor?.let {
            val expenses = DbUtils.parseCursor(cursor)

            if (expenses.size > 0) {
                return expenses[0]
            }


        }



        return null

    }


    fun updateExpense(expense: ExpenseFromApi, categories: Map<String, Category>): Expense? {

        val categoryid = if (categories.containsKey(expense.category_name)) {
            categories[expense.category_name]!!.id
        } else {
            createCategory(expense.category_name, expense.category_id)
        }


        categoryid?.let {
            val expense = Expense(
                id = expense.id,
                title = expense.title,
                created_at = DateUtils.isoFormatter.parse(expense.created_at),
                updated_at = DateUtils.isoFormatter.parse(expense.updated_at),
                dirty = false,
                deleted_at = if (expense.deleted_at != null) {
                    DateUtils.isoFormatter.parse(expense.deleted_at)
                } else null,
                cloud_id = expense.cloud_id,
                currency = expense.currency,
                date = DateUtils.isoFormatter.parse(expense.date),
                amount = expense.amount,
                category_id = categoryid
            )


            val URI = mContentResolver.update(
                AppContentProvider.URI,
                Expense.toContentValues(expense),
                null,
                null
            )


            return expense
        }


        return null


    }


    fun createCategory(title: String, cloud_id: String?): Int? {
        val categoryTOCreate = Category(title = title, cloud_id = cloud_id, id = null)

        val URI = mContentResolver.insert(
            AppContentProvider.CATEGORIES_URI,
            Category.toContentValues(categoryTOCreate)
        )

        return URI?.lastPathSegment?.toInt()
    }
}