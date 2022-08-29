package com.blueskyprojects.eisa.data.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.blueskyprojects.newlaundry.R
import com.blueskyprojects.newlaundry.interfaces.MainMenuCallBack
import com.blueskyprojects.newlaundry.models.MainMenuItem

import kotlinx.android.synthetic.main.main_item.view.*


class MenuItemAdapter(private val MenuItems: List<MainMenuItem>, var callback: MainMenuCallBack) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textView = view.textView
        val card_id = view.card_id
        val imageView = view.imageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.main_item, parent, false)

        )
    }

    override fun getItemCount(): Int {
        return MenuItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myviewholder = holder as MenuItemAdapter.ViewHolder
        myviewholder.textView.text = MenuItems[position].name
        myviewholder.imageView.setImageResource(MenuItems[position].img)
        myviewholder.card_id.setOnClickListener {
            callback.onMainMenuClick(position,1)
        }

    }
}
