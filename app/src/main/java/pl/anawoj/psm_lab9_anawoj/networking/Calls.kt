package pl.anawoj.psm_lab9_anawoj.networking

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import pl.anawoj.psm_lab9_anawoj.Common
import pl.anawoj.psm_lab9_anawoj.json.structure.MessageResponse
import pl.anawoj.psm_lab9_anawoj.recycleview.MessageItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Calls : AppCompatActivity() {

    private val rc = RetrofitClient()
    private val common = Common()
    private val api = rc.getAPI()
    var messageItemList = ArrayList<MessageItem>()

    fun getMessageData(onResult: (List<MessageItem>) -> Unit) {

        api.getMessageInfo().enqueue(object : Callback<List<MessageResponse?>> {
            override fun onResponse(
                call: Call<List<MessageResponse?>>,
                response: Response<List<MessageResponse?>>
            ) {
                val list = response.body()?.map {
                    MessageItem(
                        it?.getLogin(),
                        it?.getDate(),
                        it?.getContent(),
                        it?.getId()
                    )
                }?.reversed() ?: emptyList()

                messageItemList = ArrayList(list)
                onResult(messageItemList)
            }

            override fun onFailure(call: Call<List<MessageResponse?>>, t: Throwable) {

            }
        })
    }

    fun editMessage(id: String, editedMessage: String, login: String) {

        val call = api.editMessageInfo(id, editedMessage, login)

        call.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse?>,
                response: Response<MessageResponse?>
            ) {}

            override fun onFailure(
                call: Call<MessageResponse?>,
                t: Throwable
            ) {
                println("error")
            }
        })
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

    fun sendMessage(messageContent: String, login: String) {

        val error = checkMessageValidity(messageContent)

        if (error.equals(null)) {

            val call = api.setMessageInfo(messageContent, login)

            call.enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse?>,
                    response: Response<MessageResponse?>
                ) {
                    val test: MessageResponse? = response.body()
                    Log.d(
                        "post successful",
                        test?.getLogin().toString() + " " + test?.getId().toString()
                    )
                }

                override fun onFailure(
                    call: Call<MessageResponse?>,
                    t: Throwable
                ) {
                }
            })
        } else {
        }
    }

    fun deleteMessage(id: String) {
        val call = api.deleteMessageInfo(id)

        call.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse?>,
                response: Response<MessageResponse?>
            ) {
                val test: MessageResponse? = response.body()
                Log.d(
                    "delete successful",
                    test?.getLogin().toString() + " " + test?.getContent().toString()
                )
            }

            override fun onFailure(
                call: Call<MessageResponse?>,
                t: Throwable
            ) {
            }
        })
    }
}