package com.owesome.data.api

import android.content.Context
import com.owesome.data.auth.AuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.Route
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException

class RetroFitClient(authManager: AuthManager) {

    private val retrofit by lazy {

        val refreshOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor(10))
            .addInterceptor(RefreshTokenInterceptor(authManager))
            .build()

        val refreshRetrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.108:3001")
            .client(refreshOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val refreshService = refreshRetrofit.create(RefreshApiService::class.java)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor(10))
            .addInterceptor(AccessTokenInterceptor(authManager))
            .authenticator(TokenAuthenticator(authManager, refreshService))
            .build()

        Retrofit.Builder()
            .baseUrl("http://192.168.0.108:3001")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val groupApi: GroupApiService by lazy {
        retrofit.create(GroupApiService::class.java)
    }
}

class RefreshTokenInterceptor(private val authManager: AuthManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = authManager.getRefreshToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

class AccessTokenInterceptor(private val authManager: AuthManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = authManager.getAccessToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

class TokenAuthenticator(
    private val authManager: AuthManager,
    private val refreshService: RefreshApiService
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshResponse = refreshService.refreshToken().execute()
        return if (refreshResponse.isSuccessful) {
            println("SUCCESS")
            val newTokens  = refreshResponse.body() ?: return null
            authManager.saveAccessTokens(newTokens.accessToken, newTokens.refreshToken)
            response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokens.accessToken}")
                .build()
        } else {
            authManager.notifyLoginRequired()
            return null
        }
    }
}

class RetryInterceptor(private val maxRetries: Int = 3): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0

        while (attempt < maxRetries) {
            try {
                return chain.proceed(chain.request())
            } catch (e: HttpException) {
                println(e)
            } catch (e: Exception) {
                attempt++
                Thread.sleep(500L * attempt)
            }
        }
        throw ConnectException("Could not connect, do you have a network connection?")
    }
}

data class RefreshResponse(val accessToken: String, val refreshToken: String)