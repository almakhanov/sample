package kz.dodix.sample.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kz.dodix.sample.extensions.alert


abstract class BaseFragment : Fragment() {

    abstract fun layoutId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(layoutId(), container, false)

    /**
     * Answer
     * https://medium.com/@elye.project/handling-illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-d4ee8b630066
     * https://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-wit?source=post_page-----d4ee8b630066----------------------
     * */
    override fun onSaveInstanceState(outState: Bundle) {
        //No call for super(). Bug on API Level > 11.
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

    protected open val statusObserver = Observer<Status> {
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
}
