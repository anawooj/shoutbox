package pl.anawoj.psm_lab9_anawoj

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import pl.anawoj.psm_lab9_anawoj.networking.MyViewModel

class Common() : AppCompatActivity() {

    private lateinit var vm: MyViewModel
    var isInternetConnection: Boolean? = null


    fun startShoutboxActivity() {
        val intent = Intent(this, ShoutboxActivity::class.java)
        startActivity(intent)
    }

    fun showError(error: String, view: ConstraintLayout, duration: Int) {
        val mySnackbar = Snackbar.make(view, error, duration).show()
    }

    fun makeSnackbarNoInternet(layout: View): Snackbar {
        return Snackbar.make(layout, "Disconnected from the internet", LENGTH_INDEFINITE)
    }

    fun observeInternetConnection(
        owner: LifecycleOwner,
        onStatusChanged: ((Boolean) -> Unit)? = null,
        onDisconnectedUI: (() -> Unit)? = null,
        onConnectedUI: (() -> Unit)? = null
    ) {
        vm = ViewModelProvider(owner as ViewModelStoreOwner)[MyViewModel::class.java]

        vm.connected.observe(owner) { connected ->
            val isConnected = connected == true

            isInternetConnection = isConnected
            onStatusChanged?.invoke(isConnected)

            if (!isConnected) {
                onDisconnectedUI?.invoke()
            } else {
                onConnectedUI?.invoke()
            }
        }
    }


}