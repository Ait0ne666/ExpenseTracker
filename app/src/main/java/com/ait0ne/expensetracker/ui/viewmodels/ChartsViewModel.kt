package com.ait0ne.expensetracker.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.dto.MonthGraphDTO
import com.ait0ne.expensetracker.repositories.ExpensesRepository
import com.ait0ne.expensetracker.repositories.LocalRepository
import com.ait0ne.expensetracker.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*

class ChartsViewModel(val expensesRepository: ExpensesRepository, localRepository: LocalRepository):ViewModel() {

    val currentMonth: MutableLiveData<Date> = MutableLiveData(Date())
    val chartInfo: MutableLiveData<Resource<MonthGraphDTO>> = MutableLiveData()

    var currency: MutableLiveData<Currency> = MutableLiveData(localRepository.getCurrency())




    private fun getChartInfo(date: Date) = viewModelScope.launch {
        if (chartInfo.value == null) {
            chartInfo.postValue(Resource.Loading())

        }
        val response = expensesRepository.monthGraph(date, currency.value!!)

        val result = handleChartResponse(response)

        chartInfo.postValue(result)

    }



    private fun handleChartResponse(response: Response<MonthGraphDTO>):Resource<MonthGraphDTO> {

        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }


        return Resource.Error("Could not fetch chart data")

    }

    fun changeDate(date:Date) {
        currentMonth.postValue(date)
        getChartInfo(date)
    }



    fun update() {
        getChartInfo(currentMonth.value!!)
    }

}