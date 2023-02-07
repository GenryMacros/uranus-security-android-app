package com.example.uranus.ui.general

import com.example.uranus.R
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService


object PopUp {

    fun onButtonShowPopupWindowClick(context: Context, view: View?, popUpMessage: String,
                                     failed_to: String) {
        val inflater = getSystemService(context, LayoutInflater::class.java)

        val pw = PopupWindow(inflater?.inflate(com.example.uranus.R.layout.login_popup,
            null, false), 1080, 1920, true)
        val okButton = pw.contentView.findViewById<Button>(R.id.popup_ok)
        val popUpText = pw.contentView.findViewById<TextView>(R.id.login_popup_body)
        val headerText = pw.contentView.findViewById<TextView>(R.id.failed_to)
        headerText.text = failed_to
        popUpText.text = popUpMessage
        okButton.setOnClickListener {
            pw.dismiss()
        }
        pw.showAtLocation(view, Gravity.CENTER, 0, -100)
    }
}