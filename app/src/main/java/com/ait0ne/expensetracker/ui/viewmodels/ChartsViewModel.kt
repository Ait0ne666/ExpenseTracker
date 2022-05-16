package com.ait0ne.expensetracker.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.dto.MonthCategory
import com.ait0ne.expensetracker.models.dto.MonthGraphDTO
import com.ait0ne.expensetracker.repositories.ExpensesRepository
import com.ait0ne.expensetracker.repositories.LocalRepository
import com.ait0ne.expensetracker.utils.CurrencyUtils
import com.ait0ne.expensetracker.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*

class ChartsViewModel(val expensesRepository: ExpensesRepository,val  localRepository: LocalRepository):ViewModel() {

    val currentMonth: MutableLiveData<Date> = MutableLiveData(Date())
    val chartInfo: MutableLiveData<Resource<MonthGraphDTO>> = MutableLiveData()

    var currency: MutableLiveData<Currency> = MutableLiveData(localRepository.getCurrency())
    var job = Job()
        get() {
            if (field.isCancelled) field = Job()
            return field
        }


    init {
        getChartInfo(currentMonth.value!!)
    }

    private fun getChartInfo(date:Date) {
        if (job.isActive) {
            job.cancel()
        }

        getChartData(date)
    }


    private fun getChartData(date: Date) = viewModelScope.launch {
        if (chartInfo.value == null) {
            chartInfo.postValue(Resource.Loading())

        }

        val rates = localRepository.getRates()


        val expenses = expensesRepository.getExpensesForMonth(null, date)


        expenses.collect { localExpenses ->

            val categories = mutableMapOf<Int, MonthCategory>()
            var month_total = 0F

            localExpenses.forEach { expense ->

                val amount = CurrencyUtils.convertCurrency(expense.expense.amount, expense.expense.currency, currency.value!!, rates)
                month_total += amount

                if (categories.containsKey(expense.category.id!!)) {
                    val category = categories[expense.category.id!!]!!
                    categories[expense.category.id!!] = category.copy(amount = category.amount + amount)
                } else {
                    categories[expense.category.id!!] = MonthCategory(category_id = expense.category.id!!, category_title = expense.category.title.capitalize(), amount)
                }

            }

            val graphData = MonthGraphDTO(
                month_total = month_total,
                categories = categories.values.toList()
            )

            chartInfo.postValue(Resource.Success(graphData))


        }



    }





    fun changeDate(date:Date) {
        currentMonth.postValue(date)
        getChartInfo(date)
    }



    fun refetch() {
        getChartInfo(currentMonth.value!!)
    }





}