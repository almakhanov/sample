package kz.dodix.sample

import android.content.Context
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kz.dodix.sample.remote.Api
import kz.dodix.sample.remote.CoroutineProvider
import kz.dodix.sample.remote.TokenHolder
import kz.dodix.sample.remote.interseptors.HeaderInterceptor
import kz.dodix.sample.remote.interseptors.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val appModule = module {
    factory {
        createOkHttpClient(androidContext())
    }
    factory {
        createWebService<Api>(get(), "url")
    }
    factory {
        CoroutineProvider()
    }
    module("repository") {
//        single {
//            ProductRepository(get(), get())
//        }
//        factory {
//            PersonalRepository(get(), get())
//        }
//        factory {
//            MenuRepository(get(), get())
//        }
//
//        viewModel {
//            PartialWithDrawViewModel(get())
//        }

    }
}

fun createOkHttpClient(context: Context): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    val okHttpBuilder = OkHttpClient.Builder()
            .connectTimeout(300L, TimeUnit.SECONDS)
            .readTimeout(300L, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(HeaderInterceptor())
    if (TokenHolder.token.isNotEmpty()) {
        okHttpBuilder.addInterceptor(TokenInterceptor())
    }
    okHttpBuilder.hostnameVerifier { _, _ -> true }
    return okHttpBuilder.build()
}

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