package pl.anawoj.psm_lab9_anawoj

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.anawoj.psm_lab9_anawoj.R.id
import pl.anawoj.psm_lab9_anawoj.R.layout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class LoginActivity : AppCompatActivity() {

    // ic
    private val common = Common()

    //view
    private lateinit var setLoginButton: Button
    private lateinit var loginInput: TextInputEditText
    private lateinit var cLayout: ConstraintLayout

    //values
    private lateinit var login: String
    private lateinit var sharedPreferences: SharedPreferences
    private var keepSplash = true

    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE)

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplash }

        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            delay(3000)
            keepSplash = false
        }

        getError()
        autoLogin()
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
        common.observeInternetConnection(owner = this, onStatusChanged = { isConnected ->
                if (sharedPreferences.contains("LOGIN") && isConnected)
                    loadData()
                else
                    destroySharedPrefs()
                    renderActivity()
            }
        )
    }

    private fun renderActivity() {
        enableEdgeToEdge()
        setContentView(layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cLayout = findViewById(id.clayout)
        val mySnackbar = common.makeSnackbarNoInternet(cLayout)

        setLoginButton = findViewById(id.setLogin)
        loginInput = findViewById(id.loginInput)

        common.observeInternetConnection(owner = this,
            onDisconnectedUI = { mySnackbar.show() },
            onConnectedUI = { mySnackbar.dismiss() })

        setLoginButton.setOnClickListener {
            login = loginInput.getText().toString()

            if (common.isInternetConnection == true) {
                saveData(login)
                startShoutboxActivity()
            } else {
                mySnackbar.show()
            }
            if (login.isEmpty() || login.length > 256) {
                common.showError("Invalid login", cLayout, 3000)
            }
        }
    }

    private fun startShoutboxActivity() {
        val intent = Intent(this, ShoutboxActivity::class.java)
        startActivity(intent)
    }

    private fun loadData() {
        login = sharedPreferences.getString("LOGIN", "default")!!
        startShoutboxActivity()
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