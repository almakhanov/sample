package kz.dodix.sample.base

import androidx.fragment.app.Fragment

/**
 * Интерфейс для перехода и возврата по fragment-ам
 * был создан для того чтобы не иметь жесткой связки с активити
 * Использовать только с Activity (MainActivity : RouterAction)
 */
interface RouterFragmentAction{
    fun showFragment(fragment: Fragment, tag: String = fragment.javaClass.name, isActivity  : Boolean = false)
    fun onBackPressed()

}