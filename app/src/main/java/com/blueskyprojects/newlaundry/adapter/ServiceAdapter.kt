package com.blueskyprojects.newlaundry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blueskyprojects.newlaundry.R
import com.blueskyprojects.newlaundry.data.model.Service
import com.blueskyprojects.newlaundry.interfaces.RcCallback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.services_item.view.*

class ServiceAdapter(private val serviceItems: List<Service>, var call: RcCallback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textViewName = view.textViewName
        val layoutID = view.layoutID
        val ImgaeViewIcon = view.ImgaeViewIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.services_item, parent, false)

        )
    }

    override fun getItemCount(): Int {
        return serviceItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myviewholder = holder as ServiceAdapter.ViewHolder
        var imageURL = serviceItems[position].pic
        Picasso.get().load(imageURL).into(myviewholder.ImgaeViewIcon)
        myviewholder.textViewName.text = serviceItems[position].name
        myviewholder.layoutID.setOnClickListener {
            call.onItemClick(position, 0)
        }

    }

    private fun getBG(position: Int): Int {
        var index = position % 4
        var a = R.drawable.ic_circle_4
        when (index) {
            0 -> a = R.drawable.ic_circle_0
            1 -> a = R.drawable.ic_circle_1
            2 -> a = R.drawable.ic_circle_2
            3 -> a = R.drawable.ic_circle_3

        }
        return a
    }
}