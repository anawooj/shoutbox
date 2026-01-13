package pl.anawoj.psm_lab9_anawoj

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import pl.anawoj.psm_lab9_anawoj.json.MessageAPI
import pl.anawoj.psm_lab9_anawoj.json.structure.MessageResponse
import pl.anawoj.psm_lab9_anawoj.recycleview.MessageAdapter
import pl.anawoj.psm_lab9_anawoj.recycleview.MessageItem
import retrofit2.Call
import retrofit2.Response

class ShoutboxActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MessageAdapter
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navView: NavigationView
    private lateinit var sharedPreferences: SharedPreferences
    private var messageItemList = ArrayList<MessageItem>()
    private lateinit var sendMessageButton: ImageButton
    private lateinit var messageInput: TextInputEditText
    private lateinit var api: MessageAPI
    private lateinit var login: String
    private lateinit var swipeToRefreshLayout: SwipeRefreshLayout

    private val handler: Handler = Handler()
    private val refresh: Runnable = object : Runnable {
        override fun run() {
            onRefresh()
            handler.postDelayed(this, timeToRefresh)
        }
    }
    private var timeToRefresh: Long = 10000
    private val retrofitClient = RetrofitClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE)
        login = sharedPreferences.getString("LOGIN", "default")!!

        api = retrofitClient.getAPI()

        getMessageData()
        startAutoRefresher()
    }

    private fun getMessageData() {

        val call = api.getMessageInfo()

        call.enqueue(object : retrofit2.Callback<List<MessageResponse?>> {
            override fun onResponse(
                call: Call<List<MessageResponse?>>,
                response: Response<List<MessageResponse?>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        for (field in body) {
                            messageItemList.add(
                                MessageItem(
                                    field?.getLogin(),
                                    field?.getDate(),
                                    field?.getContent(),
                                    field?.getId()
                                )
                            )
                        }
                    }

                    renderActivity()

                } else {
                    startLoginActivity("Unknown error")
                }
            }

            override fun onFailure(call: Call<List<MessageResponse?>>, t: Throwable) {
                startLoginActivity("Disconnected from the internet")
                println(t.toString())
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
        swipeToRefreshLayout = findViewById(R.id.swipe_to_refresh_layout)
        swipeToRefreshLayout.setOnRefreshListener(this)

        renderRecyclerView()
        renderNavDrawer()
    }

    private fun renderRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        mAdapter = MessageAdapter(messageItemList.reversed().toCollection(ArrayList()))
        mRecyclerView.setLayoutManager(mLayoutManager)
        mRecyclerView.setAdapter(mAdapter)


        mAdapter.setOnClickListener(object : MessageAdapter.OnClickListener {
            override fun onClick(position: Int, model: MessageItem) {
                if (login == model.getLogin()) {
                    startEditMessageActivity(
                        model.getLogin(),
                        model.getDate(),
                        model.getContent(),
                        model.getId()
                    )
                }
            }
        })

        swipeToDelete(model.getLogin())
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

    private fun checkMessageValidity(messageContent: String): String? {

        if (messageContent.isEmpty()) {
            return "Brevity is the soul of wit, but your message does have to be at least 1 character long."
        } else if (messageContent.length > 256) {
            return "Message is too long."
        }
        // in theory always returns error, but if there are none, error is just null
        return null
    }

    private fun sendMessage() {

        val messageContent = messageInput.getText()?.trim().toString()
        val error = checkMessageValidity(messageContent)

        if (error.equals(null)) {

            val call = api.setMessageInfo(messageContent, login)

            call.enqueue(object : retrofit2.Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse?>,
                    response: Response<MessageResponse?>
                ) {
                    val test: MessageResponse? = response.body()
                    Log.d(
                        "post successful",
                        test?.getLogin().toString() + " " + test?.getId().toString()
                    )
                    onRefresh()
                }

                override fun onFailure(
                    call: Call<MessageResponse?>,
                    t: Throwable
                ) {
                    showErrors("Disconnected from the internet")
                }
            })
        } else {
            showErrors(error)
        }
    }

    private fun showErrors(error: String?) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun startLoginActivity(error: String?) {
        killAutoRefresher()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("ERROR", error)
        startActivity(intent)
    }

    private fun startEditMessageActivity(
        login: String,
        date: String,
        messageContent: String,
        id: String
    ) {
        killAutoRefresher()
        val intent = Intent(this, EditMessageActivity::class.java)
        intent.putExtra("LOGIN", login)
        intent.putExtra("DATE", date)
        intent.putExtra("MESSAGE_CONTENT", messageContent)
        intent.putExtra("ID", id)
        startActivity(intent)
    }

    private fun swipeToDelete(messageLogin : String){

        if (login == messageLogin){
            val itemTouchHelper = ItemTouchHelper(mAdapter.getTouchCallback())
            itemTouchHelper.attachToRecyclerView(mRecyclerView);
        }
    }

    private fun destroySharedPrefs() {
        sharedPreferences.edit {
            clear()
        }
    }

    override fun onRefresh() {
        swipeToRefreshLayout.isRefreshing = true
        resetAutoRefresher()
        swipeToRefreshLayout.postDelayed(this::getMessageData, 1000)
        swipeToRefreshLayout.postDelayed({ swipeToRefreshLayout.isRefreshing = false }, 3000)
    }

    private fun startAutoRefresher() {
        handler.postDelayed(refresh, timeToRefresh)
    }

    private fun resetAutoRefresher() {
        handler.removeCallbacks(refresh)
        handler.postDelayed(refresh, timeToRefresh)
    }

    private fun killAutoRefresher() {
        handler.removeCallbacks(refresh)
    }
}