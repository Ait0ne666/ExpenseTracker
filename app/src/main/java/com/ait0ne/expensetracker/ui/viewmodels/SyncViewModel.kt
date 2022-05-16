package com.ait0ne.expensetracker.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ait0ne.expensetracker.repositories.ExpensesRepository
import com.ait0ne.expensetracker.repositories.LocalRepository
import com.ait0ne.expensetracker.utils.Resource
import kotlinx.coroutines.launch
import java.util.*

class SyncViewModel(val expensesRepository: ExpensesRepository,val localRepository: LocalRepository):ViewModel() {

    val syncResult: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    init {

    }


    fun initialSync() = viewModelScope.launch {
        if (syncResult.value !is Resource.Loading) {
            syncResult.postValue(Resource.Loading())
            val lastSync = localRepository.getLastSync()


            if (lastSync == null) {
                val result = expensesRepository.initialSync()


                when(result) {
                    is Resource.Success -> {
                        localRepository.putLastSync(Date())
                    }
                }


                syncResult.postValue(result)

            } else {
                syncResult.postValue(Resource.Success(true))
            }
        }
    }





}