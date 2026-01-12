package pl.anawoj.psm_lab9_anawoj.recycleview

class MessageItem {

    private lateinit var login: String
    private lateinit var date: String
    private lateinit var content: String
    private lateinit var id: String

    constructor(login: String?, date: String?, content: String?, id: String?) {
        if (login != null) {
            this.login = login
        }
        if (date != null) {
            this.date = date
        }
        if (content != null) {
            this.content = content
        }
        if (id != null) {
            this.id = id
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

    fun getId(): String {
        return id
    }
}