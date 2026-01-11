package pl.anawoj.psm_lab9_anawoj.json

import pl.anawoj.psm_lab9_anawoj.json.structure.MessageContents
import pl.anawoj.psm_lab9_anawoj.recycleview.Message
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageAPI {
    @GET("shoutbox/messages?last=10")
    fun getMessageInfo(): Call<List<MessageContents?>>

    @POST("shoutbox/message")
    fun setMessageInfo(@Body message: Message): Response<Message>
//        “content”: “treść wiadomości”,
//        “login”: “nick użytkownika”
}