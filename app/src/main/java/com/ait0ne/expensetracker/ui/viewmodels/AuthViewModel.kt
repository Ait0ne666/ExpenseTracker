package com.ait0ne.expensetracker.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ait0ne.expensetracker.R
import com.ait0ne.expensetracker.repositories.AuthRepository
import com.ait0ne.expensetracker.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel(val authRepository: AuthRepository) : ViewModel() {


    val login: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()
    val loginError: MutableLiveData<Int> = MutableLiveData()
    val passwordError: MutableLiveData<Int> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    var errorCallback: ((error: Int) -> Unit)? = null
    var successCallback: ((success: String) -> Unit)? = null
    var buttonCallback: (() -> Unit)? = null


    fun login() {
        if (loading.value == true) {
            return
        }
        if (validate()) {
            loading.postValue(true)
            handleLogin()

        }
    }


    fun handleLogin() = viewModelScope.launch {
        buttonCallback?.let {
            it()
        }
        var response = authRepository.login(password.value!!, login.value!!)

        var result = handleLoginResponse(response)

        when(result) {
            is Resource.Success -> {
                loading.postValue(false)
                successCallback?.let {
                    it(result.data!!)
                }
            }

            is Resource.Error -> {

                errorCallback?.let {
                    it(R.string.loginError)
                }
                loading.postValue(false)
            }
        }

    }


    fun handleLoginResponse(response: Response<String>): Resource<String> {

        if (response.isSuccessful) {
            response.body().let {
                if (!it.isNullOrEmpty()) {
                    return Resource.Success(it)

                }
            }


        }

        return Resource.Error(R.string.loginError.toString())
    }


    fun validate(): Boolean {
        var validated = true
        if (login.value.isNullOrEmpty()) {
            loginError.postValue(R.string.required)
            validated = false
        } else {
            loginError.postValue(null)
        }


        if (password.value.isNullOrEmpty()) {
            passwordError.postValue(R.string.required)
            validated = false
        } else {
            passwordError.postValue(null)
        }

        return validated
    }


    fun clearErrors(field: String) {
        if (field == "pass") {
            passwordError.postValue(null)
        } else {
            loginError.postValue(null)
        }
    }


    fun onError(callback: (error: Int) -> Unit) {
        errorCallback = callback
    }

    fun onSuccess(callback: (success: String) -> Unit) {
        successCallback = callback
    }


    fun onButtonClick(callback: () -> Unit) {
        buttonCallback = callback
    }
}