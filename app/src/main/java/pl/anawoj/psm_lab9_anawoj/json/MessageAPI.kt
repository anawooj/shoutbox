package pl.anawoj.psm_lab9_anawoj.json

import pl.anawoj.psm_lab9_anawoj.json.pojo.MessageResponse
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MessageAPI {
    @GET("shoutbox/messages?last=10")
    fun getMessageInfo(): Call<List<MessageResponse?>>

    @FormUrlEncoded
    @POST("shoutbox/message")
    fun setMessageInfo(
        @Field("content") messageContent: String,
        @Field("login") login: String
    ): Call<MessageResponse>
//        “content”: “treść wiadomości”,
//        “login”: “nick użytkownika”

    @FormUrlEncoded
    @PUT("shoutbox/message/{id}")
    fun editMessageInfo(
        @Path("id") id: String,
        @Field("content") messageContent: String,
        @Field("login") login: String
    ): Call<MessageResponse>

    @DELETE("shoutbox/message/{id}")
    fun deleteMessageInfo(
        @Path("id") id: String
    ): Call<MessageResponse>
}