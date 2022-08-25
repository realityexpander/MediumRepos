package com.medium.interceptors

import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    override fun refreshAccessToken(refreshToken: String): String {
        println("Getting Access token using refreshToken: $refreshToken")
        /* .... */
        return "NEW_ACCESS_TOKEN"
    }

    override fun logout() {
        println("Logout")
        /* .... */
    }
}
