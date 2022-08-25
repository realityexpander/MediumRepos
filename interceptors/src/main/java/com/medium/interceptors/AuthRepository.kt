package com.medium.interceptors

interface AuthRepository {

    fun refreshAccessToken(refreshToken: String): String

    fun logout()
}
