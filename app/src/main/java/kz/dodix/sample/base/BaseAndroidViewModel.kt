package kz.dodix.sample.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kz.dodix.sample.data.remote.AsyncResult
import kz.dodix.sample.data.remote.CoroutineProvider
import org.koin.standalone.KoinComponent

abstract class BaseAndroidViewModel(
    app: Application,
    private val scopeProvider: CoroutineProvider = CoroutineProvider(),
    private val coroutineJob: Job = Job(),
    protected val scope: CoroutineScope = CoroutineScope(coroutineJob + scopeProvider.IO),
    _statusLiveData: MutableLiveData<Status> = MutableLiveData(),
    _errorLiveData: MutableLiveData<EventWrapper<String>> = MutableLiveData()
) : AndroidViewModel(app), KoinComponent, UiCaller {
    private val disposable = CompositeDisposable()

    internal val uiCaller: UiCaller =
        UiCallerImpl(scope, scopeProvider, _statusLiveData, _errorLiveData)

    override val statusLiveData: LiveData<Status> = uiCaller.statusLiveData
    override val errorLiveData: LiveData<EventWrapper<String>> = uiCaller.errorLiveData

    override fun <T> makeRequest(
        call: suspend CoroutineScope.() -> T,
        resultBlock: ((T) -> Unit)?
    ) = uiCaller.makeRequest(call, resultBlock)

    override fun <T> unwrap(
        result: AsyncResult<T>,
        errorBlock: ((String) -> Unit)?,
        successBlock: (T) -> Unit
    ) = uiCaller.unwrap(result, errorBlock, successBlock)

    override fun set(status: Status) = uiCaller.set(status)

    override fun setError(error: String?) {
        uiCaller.setError(error)
    }

    fun launch(job: () -> Disposable) {
        disposable.add(job())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
        coroutineJob.cancel()
    }
}


inline fun <T> BaseAndroidViewModel.launchCoroutine(
    noinline call: suspend () -> AsyncResult<T>,
    crossinline block: (T) -> Unit
) {
    makeRequest({
        call.invoke()
    }) {
        unwrap(it) {
            block(it)
        }
    }

}