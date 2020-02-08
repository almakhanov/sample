package kz.dodix.sample

import android.content.Context
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kz.dodix.sample.data.remote.Api
import kz.dodix.sample.data.remote.CoroutineProvider
import kz.dodix.sample.data.remote.TokenHolder
import kz.dodix.sample.data.interseptors.HeaderInterceptor
import kz.dodix.sample.data.interseptors.TokenInterceptor
import kz.dodix.sample.data.remote.OpenApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier


val appModule = module {

    /**
     * Api services
     */
    factory { createWebService<OpenApi>(get("openApi"), BuildConfig.API_URL) }
    factory { createWebService<Api>(get("default"), BuildConfig.API_URL) }

    /**
     * [OkHttpClient] instances
     */
    factory("default") { createOkHttpClient(androidContext(), get()) }
    factory("openApi") { createOkHttpOpenApi(androidContext()) }


    factory {
        CoroutineProvider()
    }

    module("repository") {

    }
}

const val timeout = 120L

val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
    else HttpLoggingInterceptor.Level.NONE
}

val hostnameVerifier = HostnameVerifier { _, _ -> true }

fun createOkHttpClient(context: Context, tokenInterceptor: TokenInterceptor) =
    OkHttpClient.Builder()
        .connectTimeout(timeout, TimeUnit.SECONDS)
        .readTimeout(timeout, TimeUnit.SECONDS)
        .hostnameVerifier(hostnameVerifier)
        .addInterceptor(HeaderInterceptor())
        .addInterceptor(tokenInterceptor)
        .addInterceptor(httpLoggingInterceptor)
        //.sslSocketFactory(TLSSocketFactory.getSSLSocketFactory(context))
        .build()

fun createOkHttpOpenApi(context: Context) = OkHttpClient.Builder()
    .connectTimeout(timeout, TimeUnit.SECONDS)
    .readTimeout(timeout, TimeUnit.SECONDS)
    .hostnameVerifier(hostnameVerifier)
    .addInterceptor(HeaderInterceptor())
    .addInterceptor(httpLoggingInterceptor)
    //.sslSocketFactory(TLSSocketFactory.getSSLSocketFactory(context))
    .build()

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {
    val gson = GsonBuilder()
        .setLenient()
        .create()

    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create(gson)).build()
    return retrofit.create(T::class.java)
}