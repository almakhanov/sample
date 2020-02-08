package kz.dodix.sample.base

import android.widget.Toast
import androidx.lifecycle.Observer

/**
 * Класс-обёртка для событий, которые должны срабатывать только один раз
 * Например, события навигации или [Toast]/[Snackbar]
 *
 * взято из https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
 */
open class EventWrapper<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    /**
     * Returns the content and prevents its use again.
     */
    fun get(): T? = if (hasBeenHandled) {
        null
    } else {
        hasBeenHandled = true
        content
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peek(): T = content
}

/**
 * Can be used for no-data events
 */
class VoidEvent

/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been handled.
 *
 * [onEventUnhandledContent] is *only* called if the [Event]'s contents has not been handled.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) :
    Observer<EventWrapper<T>> {
    override fun onChanged(event: EventWrapper<T>?) {
        event?.get()?.let(onEventUnhandledContent)
    }
}