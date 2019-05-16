package kz.dodix.sample.data.remote

import kotlinx.coroutines.Deferred
import retrofit2.Response

interface CoroutineCaller {

    suspend fun <T> coroutineApiCall(deferred: Deferred<Response<T>>): AsyncResult<T>

    suspend fun <T> coroutineApiCallRaw(deferred: Deferred<T>): AsyncResult<T>

    suspend fun <T> multiCall(vararg requests: Deferred<Response<T>>): List<AsyncResult<T>>

    suspend fun <T1, T2, R> zip(
        request1: Deferred<Response<T1>>,
        request2: Deferred<Response<T2>>,
        zipper: (AsyncResult<T1>, AsyncResult<T2>) -> R
    ): R

    suspend fun <T1, T2, T3, R> zip(
        request1: Deferred<Response<T1>>,
        request2: Deferred<Response<T2>>,
        request3: Deferred<Response<T3>>,
        zipper: (AsyncResult<T1>, AsyncResult<T2>, AsyncResult<T3>) -> R
    ): R

    suspend fun <T1, T2, T3, T4, R> zip(
        request1: Deferred<Response<T1>>,
        request2: Deferred<Response<T2>>,
        request3: Deferred<Response<T3>>,
        request4: Deferred<Response<T4>>,
        zipper: (AsyncResult<T1>, AsyncResult<T2>, AsyncResult<T3>, AsyncResult<T4>) -> R
    ): R

    suspend fun <T, R> zipArray(
        vararg requests: Deferred<Response<T>>,
        zipper: (List<AsyncResult<T>>) -> R
    ): R
}