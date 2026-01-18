package pl.anawoj.psm_lab9_anawoj.networking

import pl.anawoj.psm_lab9_anawoj.json.MessageAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    private val retrofitURL: Retrofit = Retrofit.Builder()
        .baseUrl("https://tgryl.pl/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofitURL.create(MessageAPI::class.java)

    fun getAPI(): MessageAPI {
        return api
    }
}