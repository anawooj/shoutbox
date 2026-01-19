package pl.anawoj.psm_lab9_anawoj

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import pl.anawoj.psm_lab9_anawoj.networking.Calls
import pl.anawoj.psm_lab9_anawoj.networking.MyViewModel

class EditMessageActivity : AppCompatActivity() {

    // initialized classes
    private var calls = Calls()
    private var common = Common()

    // view
    private lateinit var conLayout : ConstraintLayout
    private lateinit var loginTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var messageContentTextInput: TextInputEditText
    private lateinit var discardButton: Button
    private lateinit var editButton: Button
    private lateinit var menuButton: ImageButton
    private lateinit var deleteButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_message)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.clayout_edit)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        renderActivity()
    }

    private fun renderActivity() {
        conLayout = findViewById(R.id.clayout_edit)
        loginTextView = findViewById(R.id.login_view)
        dateTextView = findViewById(R.id.date_view)
        messageContentTextInput = findViewById(R.id.message_content)
        discardButton = findViewById(R.id.cancel_button)
        editButton = findViewById(R.id.edit_button)
        menuButton = findViewById(R.id.menu_button2)
        deleteButton = findViewById(R.id.delete_button)

        loginTextView.text = intent.extras?.getString("LOGIN")
        dateTextView.text = intent.extras?.getString("DATE")
        messageContentTextInput.setText(intent.extras?.getString("MESSAGE_CONTENT"))
        val id = intent.extras?.getString("ID").toString()

        val mySnackbar = common.makeSnackbarNoInternet(conLayout)

        common.observeInternetConnection(owner = this,
            onDisconnectedUI = { mySnackbar.show() },
            onConnectedUI = { mySnackbar.dismiss() })

        discardButton.setOnClickListener {
            startShoutboxActivity()
        }
        editButton.setOnClickListener {
            if(common.isInternetConnection == true){
                val editedMessage = messageContentTextInput.text.toString()
                val login = loginTextView.text.toString()
                calls.editMessage(id, editedMessage, login)
                startShoutboxActivity()
            }
        }
        deleteButton.setOnClickListener {
            if(common.isInternetConnection == true){
                calls.deleteMessage(id)
                startShoutboxActivity()
            }
        }
    }

    fun startShoutboxActivity() {
        val intent = Intent(this, ShoutboxActivity::class.java)
        startActivity(intent)
    }
}