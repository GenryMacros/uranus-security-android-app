package com.example.uranus.ui.signup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.uranus.R
import com.example.uranus.databinding.ActivitySignupBinding
import com.example.uranus.ui.general.PopUp
import com.example.uranus.ui.login.LoginActivity
import com.example.uranus.ui.signup.interfaces.SignupData


class SignupActivity : AppCompatActivity() {

    private lateinit var signupViewModel: SignupViewModel
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val firstName = binding.firstName
        val lastName = binding.lastName
        val email = binding.email
        val telegram = binding.telegram
        val phone = binding.phone
        val terms = binding.terms
        val signup = binding.signup
        val loading = binding.loading

        signup.isEnabled = false

        signupViewModel = ViewModelProvider(this, SignupViewModelFactory())
            .get(SignupViewModel::class.java)

        signupViewModel.signupFormState.observe(this@SignupActivity, Observer {
            val signupState = it ?: return@Observer

            // disable login button unless both username / password is valid
            signup.isEnabled = signupState.isDataValid && terms.isChecked

            if (signupState.usernameError != null) {
                username.error = getString(signupState.usernameError!!)
            }
            if (signupState.passwordError != null) {
                password.error = getString(signupState.passwordError!!)
            }
            if (signupState.firstNameError != null) {
                firstName.error = getString(signupState.firstNameError!!)
            }
            if (signupState.lastNameError != null) {
                lastName.error = getString(signupState.lastNameError!!)
            }
            if (signupState.emailError != null) {
                email.error = getString(signupState.emailError!!)
            }
            if (signupState.telegramError != null) {
                telegram.error = getString(signupState.telegramError!!)
            }
            if (signupState.phoneError != null) {
                phone.error = getString(signupState.phoneError!!)
            }
        })

        signupViewModel.signupResult.observe(this@SignupActivity, Observer {
            val signupResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (signupResult.error != null) {
                val error = signupResult.error
                PopUp.onButtonShowPopupWindowClick(this,
                                                        this.findViewById(R.id.container),
                                                         error,
                                                "Failed to signup")
            }
            if (signupResult.error == null) {
                goBackToLogin()
            }
            setResult(Activity.RESULT_OK)
        })

        username.afterTextChanged {
            signupViewModel.signupDataChanged(
                SignupData(
                    username.text.toString(),
                    password.text.toString(),
                    firstName.text.toString(),
                    lastName.text.toString(),
                    email.text.toString(),
                    phone.text.toString(),
                    telegram.text.toString())
            )
        }

        password.apply {
            afterTextChanged {
                signupViewModel.signupDataChanged(
                    SignupData(
                    username.text.toString(),
                    password.text.toString(),
                    firstName.text.toString(),
                    lastName.text.toString(),
                    email.text.toString(),
                    phone.text.toString(),
                    telegram.text.toString())
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        signupViewModel.signup(
                            SignupData(
                                username.text.toString(),
                                password.text.toString(),
                                firstName.text.toString(),
                                lastName.text.toString(),
                                email.text.toString(),
                                phone.text.toString(),
                                telegram.text.toString())
                        )
                }
                false
            }

            signup.setOnClickListener {
                loading.visibility = View.VISIBLE
                signupViewModel.signup(SignupData(
                    username.text.toString(),
                    password.text.toString(),
                    firstName.text.toString(),
                    lastName.text.toString(),
                    email.text.toString(),
                    phone.text.toString(),
                    telegram.text.toString())
                )
            }

            terms.setOnClickListener {
                signup.isEnabled = signupViewModel.signupFormState.value?.isDataValid ?: false && terms.isEnabled
            }
        }
    }

    private fun goBackToLogin() {
        Toast.makeText(
            applicationContext,
            "Signup was successful",
            Toast.LENGTH_LONG
        ).show()
        LoginActivity.startActivity(this)
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, SignupActivity::class.java)
            context.startActivity(intent)
        }
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}