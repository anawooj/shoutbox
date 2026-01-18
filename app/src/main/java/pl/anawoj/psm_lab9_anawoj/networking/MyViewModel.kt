package pl.anawoj.psm_lab9_anawoj.networking

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

// Source - https://stackoverflow.com/questions/25678216/android-internet-connectivity-change-listener
// Posted by Alexander Farber, modified by community. See post 'Timeline' for change history
// Retrieved 2025-12-14, License - CC BY-SA 4.0

class MyViewModel(app: Application) : AndroidViewModel(app) {
    val connected: MutableLiveData<Boolean?> = MutableLiveData<Boolean?>()

    init {
        val manager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        if (manager == null) {
            connected.value = true
        }

        connected.value = isCurrentlyConnected(manager)

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        manager?.registerNetworkCallback(networkRequest, object : NetworkCallback() {
            val availableNetworks: MutableSet<Network?> = HashSet<Network?>()

            override fun onAvailable(network: Network) {
                availableNetworks.add(network)
                connected.postValue(true)
            }

            override fun onLost(network: Network) {
                availableNetworks.remove(network)
                connected.postValue(!availableNetworks.isEmpty())
            }

            override fun onUnavailable() {
                availableNetworks.clear()
                connected.postValue(false)
            }
        })
    }

    private fun isCurrentlyConnected(manager: ConnectivityManager?): Boolean {
        val network = manager?.activeNetwork
        if (network == null) return false

        val caps = manager.getNetworkCapabilities(network)
        return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}