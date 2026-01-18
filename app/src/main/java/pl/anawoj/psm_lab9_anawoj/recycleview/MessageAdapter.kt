package pl.anawoj.psm_lab9_anawoj.recycleview

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import pl.anawoj.psm_lab9_anawoj.EditMessageActivity
import pl.anawoj.psm_lab9_anawoj.R
import pl.anawoj.psm_lab9_anawoj.ShoutboxActivity


open class MessageAdapter(private val messageItemList: List<MessageItem>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var onClickListener: OnClickListener? = null

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

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(i, currentItem)
        }
    }

    override fun getItemCount(): Int {
        return messageItemList.size
    }

    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: MessageItem)
    }
}