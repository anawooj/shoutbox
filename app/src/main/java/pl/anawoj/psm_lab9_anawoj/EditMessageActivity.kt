package pl.anawoj.psm_lab9_anawoj

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import pl.anawoj.psm_lab9_anawoj.json.MessageAPI
import pl.anawoj.psm_lab9_anawoj.json.structure.MessageResponse
import retrofit2.Call
import retrofit2.Response
import kotlin.toString

class EditMessageActivity : AppCompatActivity() {

    // initialized classes
    private var calls = Calls()

    // view
    private lateinit var loginTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var messageContentTextInput: TextInputEditText
    private lateinit var discardButton: Button
    private lateinit var editButton: Button
    private lateinit var menuButton: ImageButton
    private lateinit var deleteButton : ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_message)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        renderActivity()
    }

    private fun renderActivity() {
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

        discardButton.setOnClickListener {
            startShoutboxActivity()
        }
        editButton.setOnClickListener {
            val editedMessage = messageContentTextInput.text.toString()
            val login = loginTextView.text.toString()
            calls.editMessage(id, editedMessage, login)
            startShoutboxActivity()
        }
        deleteButton.setOnClickListener {
            calls.deleteMessage(id)
            startShoutboxActivity()
        }
    }

    fun startShoutboxActivity() {
        val intent = Intent(this, ShoutboxActivity::class.java)
        startActivity(intent)
    }
}