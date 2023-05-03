package com.example.uranus.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.uranus.R
import com.example.uranus.databinding.ActivityLoginBinding
import com.example.uranus.ui.general.PopUp
import com.example.uranus.ui.home_page.HomeActivity
import com.example.uranus.ui.home_page.HomeAuthData
import com.example.uranus.ui.signup.SignupActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loading: ProgressBar
    private lateinit var username: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val login = binding.login
        val signup = binding.signup
        val stayLogged = binding.stayLogged
        username = binding.username
        password = binding.password
        loading = binding.loading

        login.isEnabled = false

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                PopUp.onButtonShowPopupWindowClick(this,
                                                   this.findViewById(R.id.container),
                                                   loginResult.error,
                                                   "Failed to login")
            }
            else if (loginResult.success != null) {
                setResult(Activity.RESULT_OK)
                if (stayLogged.isEnabled) {
                    cacheLoginData(username.text.toString(), password.text.toString())
                }
                val authData = HomeAuthData(
                    public_key = loginViewModel.loginResult.value?.public_key,
                    auth_token = loginViewModel.loginResult.value?.auth_token,
                    refresh_token = loginViewModel.loginResult.value?.refresh_token,
                    user_id = loginViewModel.loginResult.value?.user_id
                )
                HomeActivity.startActivity(this, authData)
                finish()
            }
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }

            signup.setOnClickListener {
                SignupActivity.startActivity(this.context)
            }
        }

        tryLoginWithCachedCreds()
    }

    private fun tryLoginWithCachedCreds() {
        val prefs: SharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val usern: String? = prefs.getString("username", "")
        val pwd: String? = prefs.getString("password", "")
        if (usern != null && pwd != null) {
            if (usern.isNotEmpty() && pwd.isNotEmpty()) {
                username.setText(usern)
                password.setText(pwd)
                loading.visibility = View.VISIBLE
                loginViewModel.login(usern, pwd)
            }
        }
    }

    private fun cacheLoginData(username: String, password: String) {
        val prefs = getSharedPreferences("UserData", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.apply()
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
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
