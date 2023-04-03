package com.example.uranus.ui.home_page.utility

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.uranus.R
import com.example.uranus.ui.home_page.data.CamData


class CamsHandler(
    private val contx: Context,
    private val layout: LinearLayout
) {
    private var camerasData: List<CamData> = emptyList();
    private val _isNeedRefresh = MutableLiveData<Boolean>()
    val isNeedRefresh: LiveData<Boolean> = _isNeedRefresh;

    fun setCamData(camerasData: List<CamData>) {
        this.camerasData = camerasData
        this._isNeedRefresh.postValue(true);
    }

    fun generateButtons() {
        if (camerasData.isEmpty()) {
            return
        }
        for (camData in camerasData) {
            val button = Button(contx);
            configureButton(camData, button);
            layout.addView(button)
        }
        this._isNeedRefresh.postValue(false);
    }

    private fun configureButton(camData: CamData, button: Button) {
        button.layoutParams = LinearLayout.LayoutParams(180, 150);
        button.setBackgroundResource(R.drawable.cam_background);
        button.gravity = Gravity.RIGHT or Gravity.BOTTOM
        button.setTextColor(ContextCompat.getColor(contx, R.color.login_text_color))
        button.text = camData.camId.toString()
    }

}