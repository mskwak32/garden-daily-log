package com.mskwak.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.mskwak.presentation.R

fun Context.showPermissionDeniedSnackbar(view: View, @StringRes stringId: Int) {
    Snackbar.make(view, getString(stringId), Snackbar.LENGTH_LONG)
        .setAction(getString(R.string.permission_setting_action)) {

            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${applicationContext.packageName}")
            }.also {
                startActivity(it)
            }
        }.setActionTextColor(resources.getColor(R.color.green_600, null))
        .show()
}