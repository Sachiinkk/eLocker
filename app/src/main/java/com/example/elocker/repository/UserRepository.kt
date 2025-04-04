
package com.example.elocker.repository

import com.example.elocker.model.User
import javax.inject.Inject

class UserRepository @Inject constructor() {
    fun registerUser(user: User) {
        // mock implementation, replace with network/db logic
        println("User Registered: $user")
    }
}