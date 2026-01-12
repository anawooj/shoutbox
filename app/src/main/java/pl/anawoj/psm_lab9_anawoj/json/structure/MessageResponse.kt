package pl.anawoj.psm_lab9_anawoj.json.structure

class MessageResponse {

    private var content: String? = null
    private var login: String? = null
    private var date: String? = null
    private var id : String? = null

    fun getContent(): String? {
        return content
    }

    fun getLogin(): String? {
        return login
    }

    fun getDate(): String? {
        return date
    }
    fun getId(): String? {
        return id
    }
}