package com.blueskyprojects.newlaundry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blueskyprojects.newlaundry.ItemSelectActivity
import com.blueskyprojects.newlaundry.R
import com.blueskyprojects.newlaundry.data.model.Item
import com.blueskyprojects.newlaundry.interfaces.RcCallback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item.view.*

class ItemAdapter(private val items: List<Item>, var call : RcCallback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName = view.textView2
        val imageView = view.imageView4
        val item_card = view.item_card
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)

        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myviewholder = holder as ItemAdapter.ViewHolder
        myviewholder.textViewName.text = items[position].name
        var imageURL = items[position].pic
        Picasso.get().load(imageURL).into(myviewholder.imageView)

        myviewholder.item_card.setOnClickListener {
            call.onItemClick(position, ItemSelectActivity.Event.onItemClick.value)
        }

    }
}