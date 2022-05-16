package com.ait0ne.expensetracker.repositories

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.core.database.getFloatOrNull
import androidx.core.database.getStringOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ait0ne.expensetracker.api.RetrofitInstance
import com.ait0ne.expensetracker.db.DbUtils
import com.ait0ne.expensetracker.db.ExpenseDB
import com.ait0ne.expensetracker.models.*
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.dto.*
import com.ait0ne.expensetracker.providers.AppContentProvider
import com.ait0ne.expensetracker.utils.DateUtils
import com.ait0ne.expensetracker.utils.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class ExpensesRepository(
    private val db: ExpenseDB,
    private val RetrofitInstance: RetrofitInstance,
    private val localRepository: LocalRepository,
    private val contentResolver: ContentResolver,
    private val sync: () -> Unit
) {

    var timer: Timer? = null

    init {
        scheduleSync()
    }



    fun scheduleSync() {
        timer = Timer()
        val delay = 2 * 60 * 1000
        timer?.schedule(
            object : TimerTask() {
                override fun run() {
                    sync()
                }
            },
            delay.toLong(), delay.toLong(),
        )
    }

    fun getExpensesForMonth(category: Int?, month: Date): Flow<MutableList<ExpenseWithCategory>> {
        val year = month.year
        val m = month.month
        val start = Date(year, m, 1, 0, 0, 0)
        val calendar = GregorianCalendar()
        calendar.time = start
        calendar.add(Calendar.MONTH, 1)
        val end = calendar.time


        if (category == null) {
            return db.getExpenseDao().getAllExpensesForPeriod(start.time, end.time)
        } else {
            return db.getExpenseDao().getAllExpensesForPeriod(start.time, end.time, category)
        }
    }


    fun getExpensesForDay(day: Date): Flow<MutableList<ExpenseWithCategory>> {
        val year = day.year
        val month = day.month
        val d = day.date
        val start = Date(year, month, d, 0, 0, 0)
        val calendar = GregorianCalendar()
        calendar.time = start
        calendar.add(Calendar.DATE, 1)
        val end = calendar.time



        return db.getExpenseDao().getAllExpensesForPeriod(start.time, end.time)

    }


    suspend fun deleteByID(id: Int): Int {

        val deletedId =  db.getExpenseDao().deleteExpenseById(id.toLong(), Date())
        sync()
        return deletedId
    }

    suspend fun createExpenseFromDAO(expense: CreateExpenseRequest) {

        val dao = db.getExpenseDao()
        val existing = dao.getCategoryByTitle(expense.category_name.lowercase())

        val category_id = if (existing.isNotEmpty()) {

            existing[0].id
        } else {
            val created_category = dao.upsertCategory(
                Category(
                    cloud_id = null,
                    title = expense.category_name.lowercase(),
                )
            )

            created_category.toInt()
        }


        val expenseToCreate = Expense(
            category_id = category_id!!,
            title = expense.title,
            cloud_id = expense.cloud_id,
            currency = expense.currency,
            id = expense.id,
            date = expense.date,
            amount = expense.amount,
            created_at = expense.created_at ?: Date(),
            deleted_at = null,
            updated_at = Date(),
            dirty = true

        )


        dao.upsert(expenseToCreate)
        sync()
    }




    suspend fun initialSync(): Resource<Boolean> {
        try {
            val response = RetrofitInstance.api.sync(
                SyncDTO(expenses = listOf(), null)
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    val expenses = it.expenses
                    val dao = db.getExpenseDao()
                    val categoryIds = mutableMapOf<String, Int?>()

                    localRepository.putRates(it.rates)

                    for (expense in expenses) {
                        var category_id: Int? = null
                        val category_name = expense.category_name

                        if (categoryIds.containsKey(category_name.lowercase())) {
                            category_id = categoryIds[category_name.lowercase()]
                        } else {

                            val existing = dao.getCategoryByTitle(category_name.lowercase())

                            category_id = if (existing.isNotEmpty()) {
                                categoryIds[category_name.lowercase()] = existing[0].id
                                existing[0].id
                            } else {
                                val created_category = dao.upsertCategory(
                                    Category(
                                        cloud_id = expense.category_id,
                                        title = category_name.lowercase(),
                                    )
                                )
                                categoryIds[category_name.lowercase()] = created_category.toInt()
                                created_category.toInt()
                            }
                        }


                        val expenseToCreate = Expense(
                            category_id = category_id!!,
                            title = expense.title,
                            cloud_id = expense.cloud_id,
                            currency = expense.currency,
                            id = null,
                            date = DateUtils.isoFormatter.parse(expense.date),
                            amount = expense.amount,
                            created_at = DateUtils.isoFormatter.parse(expense.date),
                            deleted_at = if (expense.deleted_at != null) {
                                DateUtils.isoFormatter.parse(expense.deleted_at)
                                                                         } else null,
                            updated_at = DateUtils.isoFormatter.parse(expense.updated_at),
                            dirty = false

                        )


                        dao.upsert(expenseToCreate)


                    }


                    return Resource.Success(true)
                }
            }





            return Resource.Error(message = "Ошибка синхронизации")

        } catch (e: Exception) {
            Log.e("Sync", e.message ?: "")
            return Resource.Error(message = "Ошибка синхронизации")
        }


    }




}