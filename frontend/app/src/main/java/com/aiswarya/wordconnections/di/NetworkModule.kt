package com.aiswarya.wordconnections.di

import com.aiswarya.wordconnections.data.remote.api.PuzzleApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://word-connections-backend.onrender.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .addInterceptor(RetryInterceptor()) // Add the interceptor
            .build()
    }

    // Custom retry interceptor
    class RetryInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var response: Response? = null
            var retryCount = 0
            val maxRetries = 3
            var lastException: Exception? = null

            while (retryCount < maxRetries && response == null) {
                try {
                    response = chain.proceed(request)
                } catch (e: Exception) {
                    lastException = e
                    retryCount++
                    if (retryCount < maxRetries) {
                        Thread.sleep(1000L * retryCount) // Exponential backoff
                    }
                }
            }

            return response ?: throw lastException
                ?: SocketTimeoutException("Request failed after $maxRetries attempts")
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePuzzleApiService(retrofit: Retrofit): PuzzleApiService {
        return retrofit.create(PuzzleApiService::class.java)
    }
}