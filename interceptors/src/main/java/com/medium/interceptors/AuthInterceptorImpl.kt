package com.medium.interceptors

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.HttpURLConnection
import javax.inject.Inject

class AuthInterceptorImpl @Inject constructor(
    private val pref: AppSharedPreferences,
    private val authRepository: AuthRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var accessToken = pref.getAccessToken()

        if (accessToken.isNullOrBlank()) {
            accessToken = refreshAccessToken()
            if (accessToken.isNullOrBlank()) {
                authRepository.logout()
                return chain.proceed(request)
            }
        }

        val response = chain.proceed(newRequestWithAccessToken(accessToken, request))

        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            val newAccessToken = pref.getAccessToken()

            if (newAccessToken != accessToken) {
                return chain.proceed(newRequestWithAccessToken(accessToken, request))
            } else {
                accessToken = refreshAccessToken()
                if (accessToken.isNullOrBlank()) {
                    authRepository.logout()
                    return response
                }
                return chain.proceed(newRequestWithAccessToken(accessToken, request))
            }
        }

        return response
    }

    private fun newRequestWithAccessToken(accessToken: String, request: Request): Request =
        request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

    private fun refreshAccessToken(): String? {
        synchronized(this) {
            val refreshToken = pref.getRefreshToken()

            refreshToken?.let {
                val accessToken = authRepository.refreshAccessToken(refreshToken)
                pref.setAccessToken(accessToken)
                return accessToken
            } ?: return null
        }
    }
}
