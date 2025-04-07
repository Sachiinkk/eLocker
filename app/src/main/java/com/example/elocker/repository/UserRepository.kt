package com.example.elocker.repository

import com.example.elocker.data.remote.FormData
import com.example.elocker.data.remote.RetrofitInstance
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor() {
    suspend fun submitForm(data: FormData): Response<Void> {
        return RetrofitInstance.api.submitForm(data)
    }
}
