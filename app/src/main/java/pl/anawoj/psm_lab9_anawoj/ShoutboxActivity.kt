package pl.anawoj.psm_lab9_anawoj

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import pl.anawoj.psm_lab9_anawoj.json.MessageAPI
import pl.anawoj.psm_lab9_anawoj.json.structure.MessageContents
import pl.anawoj.psm_lab9_anawoj.recycleview.Message
import pl.anawoj.psm_lab9_anawoj.recycleview.MessageAdapter
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate

class ShoutboxActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navView: NavigationView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var messageList: ArrayList<Message>
    private lateinit var sendMessageButton : ImageButton
    private lateinit var messageInput : TextInputEditText
    private lateinit var retrofitURL : Retrofit
    private lateinit var api : MessageAPI
    private lateinit var login : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        getMessageData()
    }

    private fun init() {
        sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE)
        login = sharedPreferences.getString("LOGIN", "default")!!

        messageList = ArrayList<Message>()

        retrofitURL = Retrofit.Builder()
            .baseUrl("https://tgryl.pl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofitURL.create<MessageAPI>(MessageAPI::class.java)
    }

    private fun getMessageData() {

        val call: Call<List<MessageContents?>> = api.getMessageInfo()

        call.enqueue(object : retrofit2.Callback<List<MessageContents?>> {
            override fun onResponse(
                call: Call<List<MessageContents?>>,
                response: Response<List<MessageContents?>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        for (field in body) {
                            messageList.add(
                                Message(
                                    field?.getLogin(),
                                    field?.getDate(),
                                    field?.getContent()
                                )
                            )
                        }
                    }

                    renderActivity()

                } else {
                    startLoginActivity("Unknown error")
                }
            }

            override fun onFailure(call: Call<List<MessageContents?>>, t: Throwable) {
                startLoginActivity("Disconnected from the internet")
            }
        })
    }

    private fun renderActivity() {
        enableEdgeToEdge()
        setContentView(R.layout.activity_shoutbox)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        messageInput = findViewById(R.id.message_input)
        sendMessageButton = findViewById(R.id.send_button)
        sendMessageButton.setOnClickListener {
            sendMessage()
        }

        renderRecyclerView()
        renderNavDrawer()
    }

    private fun renderRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        mAdapter = MessageAdapter(messageList)
        mRecyclerView.setLayoutManager(mLayoutManager)
        mRecyclerView.setAdapter(mAdapter)
    }

    private fun renderNavDrawer() {
        drawerLayout = findViewById(R.id.main)
        menuButton = findViewById(R.id.menu_button)
        navView = findViewById(R.id.drawerView)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        setNavigationViewListener()
    }

    private fun setNavigationViewListener() {
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_shoutbox -> drawerLayout.closeDrawer(GravityCompat.START)
            R.id.nav_settings -> {
                destroySharedPrefs()
                drawerLayout.closeDrawer(GravityCompat.START)
                startLoginActivity(null)
            }
        }

        return true
    }

    private fun sendMessage(): String? {

        val messageContent = messageInput.getText()?.trim().toString()
        var error = ""

        if (messageContent.isEmpty()) { error =  "Brevity is the soul of wit, but your message does have to be at least 1 character long." }
        else if(messageContent.length > 256){ error =  "Message is too long." }
        else{

            val messageToBeSent = Message(login, LocalDate.now().toString(), messageContent)

            val call: Response<Message> = api.setMessageInfo(messageToBeSent)

            call.enqueue(object : retrofit2.Callback<Message> {
                override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                    Log.i("upload","is success:" +response.body());
                }

                override fun onFailure(call: Call<Message?>, t: Throwable) {
                    error = "Disconnected from the internet"
                }

            })
        }

        // in theory always returns error, but if call was successful, error is null
        return error
    }

    private fun startLoginActivity(error: String?) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("ERROR", error)
        startActivity(intent)
    }

    private fun destroySharedPrefs() {
        sharedPreferences.edit {
            clear()
        }
    }
}