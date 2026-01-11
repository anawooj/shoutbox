package pl.anawoj.psm_lab9_anawoj.recycleview

class Message {

    private lateinit var login: String
    private lateinit var date: String
    private lateinit var content: String

    constructor(login: String?, date: String?, content: String?) {
        if (login != null) {
            this.login = login
        }
        if (date != null) {
            this.date = date
        }
        if (content != null) {
            this.content = content
        }
    }

    fun getLogin(): String {
        return login
    }

    fun getDate(): String {
        return date
    }

    fun getContent(): String {
        return content
    }
}