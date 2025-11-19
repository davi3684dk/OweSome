package com.owesome.data.api

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.gson.annotations.SerializedName
import com.owesome.BuildConfig
import com.owesome.data.auth.AuthManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.compose.koinInject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.util.concurrent.TimeUnit

class RetroFitClient(authManager: AuthManager, context: Context) {

    private val retrofit by lazy {
        val baseUrl = BuildConfig.BACKEND_URL

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)



        val refreshOkHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .addInterceptor(logging)
            .addInterceptor(RefreshTokenInterceptor(authManager))
            .addInterceptor(RetryInterceptor(4, context))
            .build()

        val refreshRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(refreshOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val refreshService = refreshRetrofit.create(AuthApiService::class.java)

        val okHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .addInterceptor(logging)
            .addInterceptor(AccessTokenInterceptor(authManager))
            .authenticator(TokenAuthenticator(authManager, refreshService))
            .addInterceptor(RetryInterceptor(4, context))
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

    val expenseAPI: ExpenseApiService by lazy {
        retrofit.create(ExpenseApiService::class.java)
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

class RetryInterceptor(private val maxRetries: Int = 3, private val context: Context): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        val request = chain.request()

        var response: Response? = null
        var responseOk = false

        while (!responseOk && attempt < maxRetries) {
            try {
                response = chain.proceed(request)
                responseOk = true
            } catch (e: Exception) {
                Thread.sleep(500L * attempt)
                attempt++

                (context as Activity).runOnUiThread {
                    Toast.makeText(
                        context,
                        "Could not connect to server, retrying...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        if (response != null) {
            return response
        } else {
            return Response.Builder()
                .code(502)
                .message("Bad Gateway")
                .body("".toResponseBody(null))
                .protocol(Protocol.HTTP_2)
                .request(chain.request())
                .build()
        }
    }
}

data class RefreshResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)