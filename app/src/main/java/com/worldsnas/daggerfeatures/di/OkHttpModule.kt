package com.worldsnas.daggerfeatures.di

import android.app.Application
import android.util.Log
import com.squareup.moshi.Moshi
import com.worldsnas.daggerfeatures.BuildConfig.DEBUG
import com.worldsnas.daggerfeatures.network.UserAPI
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
class OkHttpModule {

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("network", message)
            }
        })

        httpLoggingInterceptor.level =
            if (DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        return httpLoggingInterceptor
    }

    @Provides
    fun provideOkhttpCache(app: Application): Cache =
        Cache(app.cacheDir, 50_000_000)

    @Provides
    @Singleton
    fun provideClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor,
        cache: Cache
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @OkHttpQualifier
    @Singleton
    fun secondOkHttp(client : OkHttpClient): OkHttpClient =
        client.newBuilder()
                //new logic
            .build()

    @Provides
    @Singleton
    fun provideMoshi() = Moshi.Builder().build()


    @Provides
    fun provideMoshiConverter(moshi: Moshi) = MoshiConverterFactory.create(moshi)

    @Provides
    @Singleton
    fun provideRetrofit(
        moshi: MoshiConverterFactory,
        client : OkHttpClient
    ) = Retrofit.Builder()
        .client(client)
        .baseUrl("https://sample.com")
        .addConverterFactory(moshi)
        .build()

    @Provides
    fun provideUserAPI(
        retrofit : Retrofit
    ): UserAPI = retrofit.create()
}