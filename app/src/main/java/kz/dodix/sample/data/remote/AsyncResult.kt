package kz.dodix.sample.data.remote

/**
 * Презентация ответов сервера для `Presentation layer`
 * должно возвращаться репозиториями, использующими [CoroutineCaller]
 * //todo implement [ResourceString] для [Error]
 */

sealed class AsyncResult<out T : Any?> {
    data class Success<out T : Any?>(val result: T? = null) : AsyncResult<T>()
    data class Error(val error: String, val code: Int = 0) : AsyncResult<Nothing>()
}