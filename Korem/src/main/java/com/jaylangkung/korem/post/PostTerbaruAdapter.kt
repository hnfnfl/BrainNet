package com.jaylangkung.korem.post

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jaylangkung.korem.R
import com.jaylangkung.korem.WebviewActivity
import com.jaylangkung.korem.dataClass.PostData
import com.jaylangkung.korem.databinding.ItemPostTerbaruBinding

class PostTerbaruAdapter : RecyclerView.Adapter<PostTerbaruAdapter.ItemHolder>() {

    private var list = ArrayList<PostData>()

    fun setItem(item: List<PostData>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemPostTerbaruBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostData) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(item.img)
                    .apply(RequestOptions().override(120))
                    .placeholder(R.drawable.ic_empty)
                    .error(R.drawable.ic_empty)
                    .into(imgPost)

                tvPostJudul.text = item.judul

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemPostTerbaruBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            WebviewActivity.webviewUrlPost = item.url
            WebviewActivity.webviewJudul = item.judul
            it.context.startActivity(Intent(it.context, WebviewActivity::class.java))
            (it.context as Activity).finish()
        }
    }

    override fun getItemCount(): Int = list.size
}