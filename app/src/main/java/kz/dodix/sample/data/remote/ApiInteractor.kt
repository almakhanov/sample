package kz.dodix.sample.data.remote

import com.google.gson.JsonParseException
import kotlinx.coroutines.Deferred
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.net.ConnectException
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException

class ApiInteractor : CoroutineCaller {
    val HTTP_CODE_TOKEN_EXPIRED = 420
    val HTTP_CODE_ACCOUNT_BLOCKED = 419

    /**
     * Обработчик запросов на `kotlin coroutines`
     * ждет выполнения запроса [deferred]
     * обрабатывает ошибки сервера
     * обрабатывает ошибки соединения
     * возвращает [AsyncResult.Success] или [AsyncResult.Error]
     */
    override suspend fun <T> coroutineApiCall(deferred: Deferred<Response<T>>): AsyncResult<T> = try {
        handleResult(deferred.await())
    } catch (e: Exception) {
        handleException(e)
    }

    override suspend fun <T> coroutineApiCallRaw(deferred: Deferred<T>): AsyncResult<T> = try {
        AsyncResult.Success(deferred.await())
    } catch (e: Exception) {
        handleException(e)
    }

    /**
     * Обработчик для нескольких запросов на `kotlin coroutines`
     * запускает все [requests] и записывает их в массив [AsyncResult]
     * обрабатывает ошибки сервера при помощи [coroutineApiCall]
     * обрабатывает ошибки соединения при помощи [coroutineApiCall]
     *   пока есть ограничение: можно делать только однородные запросы
     *   то есть [requests] должны возвращать либо один тип данных, либо общий интерфейс
     */
    override suspend fun <T> multiCall(vararg requests: Deferred<Response<T>>): List<AsyncResult<T>> =
        requests.map {
            coroutineApiCall(it)
        }

    /**
     * Обработчик для однородных запросов на `kotlin coroutines`
     * [requests] должны возвращать один тип данных
     * запускает все [requests], записывает их в массив [AsyncResult]
     * и передает в обработчик [zipper]
     */
    override suspend fun <T, R> zipArray(
        vararg requests: Deferred<Response<T>>,
        zipper: (List<AsyncResult<T>>) -> R
    ): R = zipper(requests.map { coroutineApiCall(it) })

    /**
     * Обработчик для двух разнородных запросов на `kotlin coroutines`
     * запускает [request1], [request2] и передает в обработчик [zipper]
     */
    override suspend fun <T1, T2, R> zip(
        request1: Deferred<Response<T1>>,
        request2: Deferred<Response<T2>>,
        zipper: (AsyncResult<T1>, AsyncResult<T2>) -> R
    ): R = zipper(coroutineApiCall(request1), coroutineApiCall(request2))

    /**
     * Обработчик для трех разнородных запросов на `kotlin coroutines`
     * запускает [request1], [request2], [request3] и передает в обработчик [zipper]
     * обрабатывает ошибки сервера при помощи [coroutineApiCall]
     * обрабатывает ошибки соединения при помощи [coroutineApiCall]
     */
    override suspend fun <T1, T2, T3, R> zip(
        request1: Deferred<Response<T1>>,
        request2: Deferred<Response<T2>>,
        request3: Deferred<Response<T3>>,
        zipper: (AsyncResult<T1>, AsyncResult<T2>, AsyncResult<T3>) -> R
    ): R = zipper(coroutineApiCall(request1), coroutineApiCall(request2), coroutineApiCall(request3))

    /**
     * Обработчик для трех разнородных запросов на `kotlin coroutines`
     * запускает [request1], [request2], [request3] и передает в обработчик [zipper]
     * обрабатывает ошибки сервера при помощи [coroutineApiCall]
     * обрабатывает ошибки соединения при помощи [coroutineApiCall]
     */
    override suspend fun <T1, T2, T3, T4, R> zip(
        request1: Deferred<Response<T1>>,
        request2: Deferred<Response<T2>>,
        request3: Deferred<Response<T3>>,
        request4: Deferred<Response<T4>>,
        zipper: (AsyncResult<T1>, AsyncResult<T2>, AsyncResult<T3>, AsyncResult<T4>) -> R
    ): R = zipper(
        coroutineApiCall(request1),
        coroutineApiCall(request2),
        coroutineApiCall(request3),
        coroutineApiCall(request4)
    )

    private fun <T> handleResult(result: Response<T>): AsyncResult<T> {
        return if (result.isSuccessful) {
            AsyncResult.Success(result.body())
        } else {
            throw HttpException(result)
        }
    }

    private fun <T> handleException(e: Exception): AsyncResult<T> {
        return when (e) {
            is HttpException -> {
                when (e.code()) {
                    HTTP_UNAUTHORIZED -> {
                        Timber.i("User not authorized, should exit application")
                        AsyncResult.Error("Срок действия сессии истек", e.code())
                    }
                    HTTP_CODE_TOKEN_EXPIRED -> {
                        Timber.i("User's token has expired, should exit application")
                        AsyncResult.Error("Срок действия сессии истек", e.code())
                    }
                    HTTP_CODE_ACCOUNT_BLOCKED -> {
                        Timber.i("User's account is blocked, should exit application")
                        AsyncResult.Error("Ваш аккаунт заблокирован", e.code())
                    }
                    HTTP_INTERNAL_ERROR -> {
                        Timber.w("Internal server error:\n${e.response().errorBody()?.string()}")
                        AsyncResult.Error("Внутренняя ошибка сервера", e.code())
                    }
                    else -> {
                        Timber.w("Server error ${e.code()}")
                        val errorBody = e.response().errorBody()?.string()
                        if (errorBody.isNullOrBlank()) {
                            AsyncResult.Error("Сервер вернул ошибку ${e.code()}", e.code())
                        } else {
                            AsyncResult.Error(
                                ErrorResponse.print(
                                    errorBody,
                                    e.code()
                                ), e.code()
                            )
                        }
                    }
                }
            }
            is SocketTimeoutException -> {
                Timber.i("Request timeout")
                AsyncResult.Error("Сервер не отвечает")
            }
            is SSLHandshakeException -> {
                Timber.w("SSLHandshakeException: ${e.message}")
                AsyncResult.Error("Возникли проблемы с сертификатом")
            }
            is JsonParseException -> {
                Timber.w("JsonParseException: ${e.message}")
                AsyncResult.Error("Ошибка обработки запроса")
            }
            is ConnectException -> {
                Timber.i("ConnectException: ${e.message}")
                AsyncResult.Error("Проверьте подключение к интернету")
            }
            else -> {
                Timber.w(e, "Unhandled request exception")
                AsyncResult.Error("Ошибка: ${e.javaClass.simpleName} ${e.localizedMessage}")
            }
        }
    }
}