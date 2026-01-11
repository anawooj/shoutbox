package pl.anawoj.psm_lab9_anawoj

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import pl.anawoj.psm_lab9_anawoj.R.id
import pl.anawoj.psm_lab9_anawoj.R.layout


class LoginActivity : AppCompatActivity() {

    private lateinit var setLoginButton: Button
    private lateinit var loginInput: TextInputEditText
    private lateinit var login: String
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        getError()
        autoLogin()
    }

    private fun init() {
        sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE)
    }

    private fun getError() {
        val intent: Intent = getIntent()
        val errorMessage: String? = intent.getStringExtra("ERROR")

        if (!errorMessage.equals(null)) {
            destroySharedPrefs()
            renderActivity()
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun autoLogin() {
        if (sharedPreferences.contains("LOGIN")) {
            loadData()
        } else {
            renderActivity()
        }
    }

    private fun renderActivity() {
        enableEdgeToEdge()
        setContentView(layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setLoginButton = findViewById(id.setLogin)
        loginInput = findViewById(id.loginInput)

        setLoginButton.setOnClickListener {

            login = loginInput.getText().toString()
            saveData(login)
            startShoutboxActivity(login)
        }
    }

    private fun startShoutboxActivity(login: String) {
        val intent = Intent(this, ShoutboxActivity::class.java)
        intent.putExtra("LOGIN", login)
        startActivity(intent)
    }

    private fun loadData() {
        val login: String = sharedPreferences.getString("LOGIN", "default")!!
        startShoutboxActivity(login)
    }

    private fun saveData(login: String?) {
        sharedPreferences.edit {
            putString("LOGIN", login)
        }
    }

    private fun destroySharedPrefs() {
        sharedPreferences.edit {
            clear()
        }
    }
}