package pl.anawoj.psm_lab9_anawoj.json

import pl.anawoj.psm_lab9_anawoj.json.structure.MessageResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface MessageAPI {
    @GET("shoutbox/messages?last=10")
    fun getMessageInfo(): Call<List<MessageResponse?>>

    @FormUrlEncoded
    @POST("shoutbox/message")
    fun setMessageInfo(
        @Field("content") messageContent : String,
        @Field("login") login : String) : Call<MessageResponse>
//        “content”: “treść wiadomości”,
//        “login”: “nick użytkownika”
}