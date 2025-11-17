package com.owesome.data.api

import com.google.gson.annotations.SerializedName
import com.owesome.BuildConfig
import com.owesome.data.auth.AuthManager
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException

class RetroFitClient(authManager: AuthManager) {

    private val retrofit by lazy {
        val baseUrl = BuildConfig.BACKEND_URL

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)



        val refreshOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(RetryInterceptor(4))
            .addInterceptor(RefreshTokenInterceptor(authManager))
            .build()

        val refreshRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(refreshOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val refreshService = refreshRetrofit.create(AuthApiService::class.java)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(RetryInterceptor(4))
            .addInterceptor(AccessTokenInterceptor(authManager))
            .authenticator(TokenAuthenticator(authManager, refreshService))
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val groupApi: GroupApiService by lazy {
        retrofit.create(GroupApiService::class.java)
    }

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val userApi: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}

class RefreshTokenInterceptor(private val authManager: AuthManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = authManager.getRefreshToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Cookie", "refresh_token=$token")
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
                .addHeader("Cookie", "jwt=$token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

class TokenAuthenticator(
    private val authManager: AuthManager,
    private val refreshService: AuthApiService
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshResponse = refreshService.refreshToken().execute()
        return if (refreshResponse.isSuccessful) {
            println("SUCCESS")
            val newTokens  = refreshResponse.body() ?: return null
            authManager.saveAccessTokens(newTokens.accessToken, newTokens.refreshToken)
            response.request.newBuilder()
                .header("Cookie", "jwt=${newTokens.accessToken}")
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

data class RefreshResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)