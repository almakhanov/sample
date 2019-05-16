package kz.dodix.sample.data.remote

import com.google.gson.Gson

/**
 * Модель ошибок сервера
 * //todo оставить только нужные поля
 */
data class ErrorResponse(
    val status: Int?,
    val error: String?,
    val exception: String?,
    val message: String?
) {

    fun print(): String {
        return message ?: "ErrorResponse"
    }

    companion object {
        fun from(response: String): ErrorResponse {
            return Gson().fromJson(response, ErrorResponse::class.java)
        }

        fun print(response: String, code: Int) = try {
            from(response).print()
        } catch (e: Exception) {
            "$code Unable to parse ErrorResponse"
        }
    }
}