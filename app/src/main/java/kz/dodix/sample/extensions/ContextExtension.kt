package kz.dodix.sample.extensions

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.content.ContextCompat
import kz.dodix.sample.R
import kotlin.reflect.KClass

const val PREF_NAME_FIELD = "stateFiled"

fun Context.toast(message: String, longToast: Boolean = true) {
    Toast.makeText(this, message, if (longToast) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

enum class MessageType {
    ALERT,
    TOAST
}

fun Context.alert(
    title: String = "Ошибка",
    message: String? = "Что-то пошло не так!",
    positiveButton: (() -> Unit?)? = null,
    cancelable: Boolean = true,
    type: MessageType = MessageType.ALERT
) {
    when (type) {
        MessageType.TOAST -> {
            this.toast(message ?: "Что-то пошло не так!")
        }
        MessageType.ALERT -> {
            val dialog = AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes) { dialog, _ ->
                    dialog.dismiss()
                    positiveButton?.invoke()
                }
                .setCancelable(cancelable)
                .create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.colorTransparent))
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.colorTransparent))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            }
            dialog.show()
        }
    }
}

fun Context.alertWithActions(
    title: String,
    message: String,
    cancelable: Boolean = true,
    positiveButtonCallback: () -> Unit,
    negativeButtonCallback: () -> Unit,
    positiveText: String? = resources.getString(android.R.string.yes),
    negativeText: String? = resources.getString(android.R.string.no)
) {

    val dialog = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveText) { dialog, _ ->
            positiveButtonCallback()
            dialog.dismiss()
        }
        .setNegativeButton(negativeText) { dialog, _ ->
            negativeButtonCallback()
            dialog.dismiss()
        }
        .setCancelable(cancelable)
        .create()

    dialog.setOnShowListener {
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setBackgroundColor(ContextCompat.getColor(this, R.color.colorTransparent))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setBackgroundColor(ContextCompat.getColor(this, R.color.colorTransparent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
    }
    dialog.show()
}

/**
 * В параметр [className] передаем обект класса
 * для того чтобы система понимала в каком окне
 * вы хотите сохранить сосояние
 */
fun <T : Any> Context.saveStateField(className: KClass<T>, keyField: String?, valueField: String) {
    val sharedPreferences: SharedPreferences = this.getSharedPreferences(
        PREF_NAME_FIELD + className.simpleName,
        Context.MODE_PRIVATE
    )
    sharedPreferences.edit().putString(keyField, valueField).apply()
}

/**
 * Получение сохраненного состояние поля
 * Для получения необходжимо передать тип класса и ключь получаемого значения
 */
fun <T : Any> Context.getStateField(className: KClass<T>, keyField: String?): String {
    val sharedPreferences: SharedPreferences = this.getSharedPreferences(
        PREF_NAME_FIELD + className.simpleName,
        Context.MODE_PRIVATE
    )
    return sharedPreferences.getString(keyField, "").orEmpty()
}

/**
 * Обязательная вызывать в onDestroy()
 * Для избежания получения ложного значения в фильтре
 */
fun <T : Any> Context.clearStateField(className: KClass<T>) {
    val sharedPreferences: SharedPreferences = this.getSharedPreferences(
        PREF_NAME_FIELD + className.simpleName,
        Context.MODE_PRIVATE
    )
    sharedPreferences.edit().clear().apply()
}
