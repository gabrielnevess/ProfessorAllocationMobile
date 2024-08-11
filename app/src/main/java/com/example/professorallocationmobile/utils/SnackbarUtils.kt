package com.example.professorallocationmobile.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackbarUtils private constructor() {
    companion object {
        fun showSnackbar(context: View, message: String) {
            Snackbar.make(context, message, Snackbar.LENGTH_LONG).show()
        }
    }
}