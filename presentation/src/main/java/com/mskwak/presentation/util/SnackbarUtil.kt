package com.mskwak.presentation.util

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(text: String, timeLength: Int) {
    Snackbar.make(this, text, timeLength).show()
}

fun View.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Int>,
    timeLength: Int
) {
    snackbarEvent.observe(lifecycleOwner) { stringId ->
        showSnackbar(context.getString(stringId), timeLength)
    }
}