package pl.anawoj.psm_lab9_anawoj

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
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
import retrofit2.Retrofit

class EditMessageActivity : AppCompatActivity() {

    private lateinit var loginTextView : TextView
    private lateinit var dateTextView : TextView
    private lateinit var messageContentTextInput: TextInputEditText
    private lateinit var discardButton: Button
    private lateinit var editButton: Button
    private val retrofitClient = RetrofitClient()
    private lateinit var api: MessageAPI


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

    private fun renderActivity(){
        loginTextView = findViewById(R.id.login_view)
        dateTextView = findViewById(R.id.date_view)
        messageContentTextInput = findViewById(R.id.message_content)
        discardButton = findViewById(R.id.cancel_button)
        editButton = findViewById(R.id.edit_button)

        loginTextView.text = intent.extras?.getString("LOGIN")
        dateTextView.text = intent.extras?.getString("DATE")
        messageContentTextInput.setText(intent.extras?.getString("MESSAGE_CONTENT"))

        discardButton.setOnClickListener {
            startShoutboxActivity()
        }
        editButton.setOnClickListener {
            editMessage()
        }
    }
    private fun startShoutboxActivity() {
        val intent = Intent(this, ShoutboxActivity::class.java)
        startActivity(intent)
    }

    private fun editMessage(){

        val editedMessage = messageContentTextInput.text.toString()

        api = retrofitClient.getAPI()

        val call = api.editMessageInfo(editedMessage, loginTextView.text.toString())

        call.enqueue(object : retrofit2.Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse?>,
                response: Response<MessageResponse?>
            ) {
                val test: MessageResponse? = response.body()
                Log.d(
                    "edit successful",
                    test?.getLogin().toString() + " " + test?.getContent().toString()
                )
                startShoutboxActivity()
            }

            override fun onFailure(
                call: Call<MessageResponse?>,
                t: Throwable
            ) {
                showErrors("Disconnected from the internet")
            }
        })
    }
    private fun showErrors(error: String?) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }
}