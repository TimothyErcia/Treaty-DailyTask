package com.treaty.dailytask.utility

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.treaty.dailytask.R

@SuppressLint("RestrictedApi")
class CustomSnackBar(private val message: String, parentView: View) {
    private lateinit var snackBarLayout: SnackbarLayout
    private var snackbar: Snackbar = Snackbar.make(parentView, "", Snackbar.LENGTH_LONG)
    private lateinit var context: Context
    private lateinit var snackbarView: View

    fun addCustomView(context: Context): CustomSnackBar {
        snackBarLayout = snackbar.view as SnackbarLayout
        snackbarView = View.inflate(context, R.layout.snackbar_layout, null)
        snackBarLayout.setBackgroundColor(Color.TRANSPARENT)
        val snackbarText = snackbarView.findViewById<TextView>(R.id.snackbarText)
        snackbarText.text = this.message

        snackBarLayout.addView(snackbarView, 0)
        this.context = context
        return this
    }

    fun isSuccessful(value: Boolean): CustomSnackBar {
        Log.d("TAG", "isSuccessful: $value ")
        val bgColor = if(value) {
            this.context.getColor(R.color.foodCategory)
        } else {
            this.context.getColor(R.color.personalCategory)
        }
        val layout = snackbarView.findViewById<View>(R.id.snackbarLayout)
        layout.background.setTint(bgColor)
        return this
    }

    fun show() {
        snackbar.show()
    }
}
