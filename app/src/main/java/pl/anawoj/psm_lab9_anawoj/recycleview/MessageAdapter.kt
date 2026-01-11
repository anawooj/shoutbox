package pl.anawoj.psm_lab9_anawoj.recycleview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.anawoj.psm_lab9_anawoj.R


open class MessageAdapter(private val messageItemList: ArrayList<MessageItem>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    open class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var login: TextView = itemView.findViewById(R.id.loginView)
        var date: TextView = itemView.findViewById(R.id.dateView)
        var content: TextView = itemView.findViewById(R.id.contentView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        val viewLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.shout_item, parent, false)
        return MessageViewHolder(viewLayout)
    }

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        i: Int
    ) {
        val currentItem = messageItemList[i]
        holder.login.text = currentItem.getLogin()
        holder.date.text = currentItem.getDate()
        holder.content.text = currentItem.getContent()
    }

    override fun getItemCount(): Int {
        return messageItemList.size
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

    fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {

    }
}