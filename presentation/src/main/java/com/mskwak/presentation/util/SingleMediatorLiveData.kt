package com.mskwak.presentation.util

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.orhanobut.logger.Logger
import java.util.concurrent.atomic.AtomicBoolean

class SingleMediatorLiveData<T> : MediatorLiveData<T>() {
    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Logger.w("Multiple observers registered but only one will be notified of changes.")
        }

        super.observe(owner) { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }

    @MainThread
    override fun setValue(value: T) {
        pending.set(true)
        super.setValue(value)
    }
}