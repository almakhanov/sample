package kz.dodix.sample.extensions

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kz.dodix.sample.base.RouterFragmentAction

fun Activity?.back() {
    (this as? RouterFragmentAction)?.onBackPressed()
}

fun FragmentActivity?.pop() = this?.supportFragmentManager?.popBackStack()

fun Activity?.showFragment(fragment: Fragment, tag: String = fragment.javaClass.name, fromActivity: Boolean = false) {
    (this as? RouterFragmentAction)?.showFragment(fragment, tag, fromActivity)
}

fun Activity?.showKeyboard(view: View) {
    val inputMethodManager = this?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager?
    inputMethodManager?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun Activity?.hideKeyboard() {
    val inputMethodManager = this?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager?
    inputMethodManager?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    inputMethodManager?.hideSoftInputFromWindow(this?.currentFocus?.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Activity?.getRecourseText(resId: Int) = this?.resources?.getText(resId).toString()


