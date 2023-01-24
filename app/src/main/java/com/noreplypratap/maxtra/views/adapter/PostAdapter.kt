package com.noreplypratap.maxtra.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noreplypratap.maxtra.R
import com.noreplypratap.maxtra.model.response.Data

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var data : List<Data>? = null
    fun setPostData(item : List<Data>){
        data = item
    }

    class PostViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var userName: TextView = itemView.findViewById(R.id.tvName)
        var desc: TextView = itemView.findViewById(R.id.tvDesc)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.post_item,parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.userName.setText(data?.get(position)?.name)
        holder.desc.setText(data?.get(position)?.discription)
        val item = data!![position]
        holder.itemView.setOnClickListener {
            onItemClicked.let {
                if (it != null) {
                    it(item)
                }
            }
        }
    }

    override fun getItemCount() = data?.count() ?: 0

    private var onItemClicked : ((Data) -> Unit)? = null

    fun setOnClickListener(listener : (Data) -> Unit){
        onItemClicked = listener
    }
}