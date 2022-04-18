package com.ait0ne.expensetracker.repositories

import com.ait0ne.expensetracker.api.RetrofitInstance
import com.ait0ne.expensetracker.models.dto.LoginDTO
import retrofit2.Response

class AuthRepository (val RetrofitInstance: RetrofitInstance) {

    suspend fun login(password: String, login: String) : Response<String> {
        val dto = LoginDTO(password, login)
        return RetrofitInstance.api.login(dto)

    }

}