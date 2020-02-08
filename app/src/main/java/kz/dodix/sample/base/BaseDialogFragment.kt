package kz.dodix.sample.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import kz.dodix.sample.extensions.alert

abstract class BaseDialogFragment: DialogFragment(){

    abstract fun layoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Currenty can't test, so I left it as comment
        //setStyle(STYLE_NO_TITLE, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(layoutId(), container, false)


    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    protected val statusObserver = Observer<Status> {
        it?.let {
            when (it) {
                Status.SHOW_LOADING -> showProgress()
                Status.HIDE_LOADING -> hideProgress()
                Status.SUCCESS -> success()
            }
        }
    }

    protected val errorMessageObserver = EventObserver<String> {
        context?.alert(message = it)
        onError(it)
    }

    open fun showProgress() {
        // do nothing
    }

    open fun hideProgress() {
        // do nothing
    }

    open fun success() {
        // do nothing
    }

    open fun onError(message: String) {
        // do nothing
    }
}