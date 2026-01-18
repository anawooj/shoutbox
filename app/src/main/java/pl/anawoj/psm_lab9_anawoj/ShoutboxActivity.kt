package pl.anawoj.psm_lab9_anawoj

import android.R.attr.background
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import pl.anawoj.psm_lab9_anawoj.recycleview.MessageAdapter
import pl.anawoj.psm_lab9_anawoj.recycleview.MessageItem

class ShoutboxActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    SwipeRefreshLayout.OnRefreshListener {

    // initialized classes
    private val calls = Calls()
    private val handler: Handler = Handler()

    // view
    private lateinit var mAdapter: MessageAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var swipeToRefreshLayout: SwipeRefreshLayout
    private lateinit var menuButton: ImageButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sendMessageButton: ImageButton
    private lateinit var messageInput: TextInputEditText

    // values
    private lateinit var login: String
    private val refresh: Runnable = object : Runnable {
        override fun run() {
            onRefresh()
            handler.postDelayed(this, timeToRefresh)
        }
    }
    private var timeToRefresh: Long = 100000
    private var messagesList = ArrayList<MessageItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE)
        login = sharedPreferences.getString("LOGIN", "default")!!

        callGetMessageData()
        renderActivity()
        startAutoRefresher()
    }

    fun callGetMessageData(){
        calls.getMessageData { list ->
            messagesList = ArrayList(list)
            renderRecyclerView()
        }
    }
    fun renderActivity() {
        enableEdgeToEdge()
        setContentView(R.layout.activity_shoutbox)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        swipeToRefreshLayout = findViewById(R.id.swipe_to_refresh_layout)
        swipeToRefreshLayout.setOnRefreshListener(this)

        messageInput = findViewById(R.id.message_input)
        sendMessageButton = findViewById(R.id.send_button)

        sendMessageButton.setOnClickListener {
            val messageContent = messageInput.getText()?.trim().toString()
            calls.sendMessage(messageContent, login)
            messageInput.text = null
            onRefresh()
        }

        renderNavDrawer()
    }

    private fun renderRecyclerView(){

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        mAdapter = MessageAdapter(messagesList)
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

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            private val deleteIcon = ContextCompat.getDrawable(this@ShoutboxActivity, R.drawable.ic_delete_white_24)
            private val intrinsicWidth = deleteIcon.intrinsicWidth
            private val intrinsicHeight = deleteIcon.intrinsicHeight
            private val background = ColorDrawable()
            private val backgroundColor = Color.parseColor("#f44336")
            private val clearPaint = Paint().apply { xfermode =
                PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val isCanceled = dX == 0f && !isCurrentlyActive

                if (isCanceled) {
                    clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                    super.onChildDraw(c, mRecyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    return
                }

                // Draw the red delete background
                background.color = backgroundColor
                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background.draw(c)

                // Calculate position of delete icon
                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeight

                // Draw the delete icon
                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                deleteIcon.draw(c)

                super.onChildDraw(c, mRecyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
                c?.drawRect(left, top, right, bottom, clearPaint)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val position: MessageItem = messagesList[viewHolder.adapterPosition]

                return if(login != position.getLogin()){
                    0
                }
                else {
                    super.getSwipeDirs(recyclerView, viewHolder)
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedMessage: MessageItem = messagesList[viewHolder.adapterPosition]
                calls.deleteMessage(deletedMessage.getId())
                mAdapter.notifyItemRemoved(viewHolder.adapterPosition)
            }
        }).attachToRecyclerView(mRecyclerView)
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

    fun startLoginActivity(error: String?) {
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

    private fun destroySharedPrefs() {
        sharedPreferences.edit {
            clear()
        }
    }

    override fun onRefresh() {
        swipeToRefreshLayout.isRefreshing = true
        resetAutoRefresher()

        swipeToRefreshLayout.postDelayed({
            callGetMessageData()
        }, 1000)

        swipeToRefreshLayout.postDelayed({
            swipeToRefreshLayout.isRefreshing = false
        }, 3000)
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

    fun showErrors(error: String?) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }
}
