package com.example.uranus.ui.confirmation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast

import com.example.uranus.R
import com.example.uranus.databinding.ActvityEmailCofirmationBinding
import com.example.uranus.ui.confirmation.interfaces.ConfirmationData
import com.example.uranus.ui.general.PopUp
import com.example.uranus.ui.home_page.HomeActivity
import com.example.uranus.ui.home_page.HomeAuthData
import com.example.uranus.ui.login.LoggedInUserView

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var confirmationViewModel: ConfirmationViewModel
    private lateinit var binding: ActvityEmailCofirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActvityEmailCofirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clientId = intent.getIntExtra("client_id", -1)

        val token = binding.codeField
        val confirmButton = binding.confirmButton
        val loading = binding.loading

        confirmButton.isEnabled = false

        confirmationViewModel = ViewModelProvider(this, ConfirmationViewModelFactory())
            .get(ConfirmationViewModel::class.java)

        confirmationViewModel.confirmationFormState.observe(this@ConfirmationActivity, Observer {
            val confirmationState = it ?: return@Observer

            confirmButton.isEnabled = confirmationState.isDataValid

            if (confirmationState.tokenError != null) {
                token.error = getString(confirmationState.tokenError)
            }
        })

        confirmationViewModel.confirmationResult.observe(this@ConfirmationActivity, Observer {
            val confirmationResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (confirmationResult.error != null) {
                PopUp.onButtonShowPopupWindowClick(this,
                    this.findViewById(R.id.confirmation_container),
                    confirmationResult.error,
                    "Failed to confirm account")
            }
            if (confirmationResult.success != null) {
                setResult(Activity.RESULT_OK)
                val authData = HomeAuthData(
                    public_key = confirmationViewModel.confirmationResult.value?.public_key,
                    auth_token = confirmationViewModel.confirmationResult.value?.auth_token,
                    refresh_token = confirmationViewModel.confirmationResult.value?.refresh_token,
                    user_id = confirmationViewModel.confirmationResult.value?.user_id
                )
                HomeActivity.startActivity(this, authData)
            }
        })

        token.afterTextChanged {
            confirmationViewModel.confirmationDataChanged(
                ConfirmationData(token.text.toString(), clientId)
            )
        }

        confirmButton.setOnClickListener {
            loading.visibility = View.VISIBLE
            confirmationViewModel.confirm(
                ConfirmationData(
                    token=token.text.toString(),
                    id=clientId
            )
            )
        }
    }

    companion object {
        fun startActivity(context: Context, client_id: Int) {
            val intent = Intent(context, ConfirmationActivity::class.java)
            intent.putExtra("client_id", client_id)
            context.startActivity(intent)
        }
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}