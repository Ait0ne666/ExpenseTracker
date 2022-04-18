package com.ait0ne.expensetracker.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.repositories.LocalRepository

class SharedViewModel(val localRepository: LocalRepository): ViewModel() {

    private var _currency: MutableLiveData<Currency> = MutableLiveData(localRepository.getCurrency())
    val currency: LiveData<Currency> = _currency





}