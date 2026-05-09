package com.example.data.data.repository

import com.example.data.data.remote.FacebookAuthDataSource
import com.example.domain.domain.model.User
import com.example.domain.domain.repository.FacebookAuthRepository
import javax.inject.Inject

class FacebookAuthRepositoryImpl @Inject constructor(
    private val dataSource: FacebookAuthDataSource
) : FacebookAuthRepository {
    override suspend fun login(token: String): Result<User> {
        return dataSource.logIn(token)
    }
}