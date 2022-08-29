package com.blueskyprojects.newlaundry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blueskyprojects.newlaundry.ItemSelectActivity
import com.blueskyprojects.newlaundry.R
import com.blueskyprojects.newlaundry.data.model.Category
import com.blueskyprojects.newlaundry.interfaces.RcCallback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.category_item.view.*
import kotlinx.android.synthetic.main.services_item.view.textViewName

class CategoryAdapter(private val categoryItems: List<Category>, var call: RcCallback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textViewName = view.textViewName
        val ImageView = view.ImageViewCategeory
        val CategoryCard = view.CategoryCard
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)

        )
    }

    override fun getItemCount(): Int {
        return categoryItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myviewholder = holder as CategoryAdapter.ViewHolder
        myviewholder.textViewName.text = categoryItems[position].name

        var imageURL = categoryItems[position].pic
        Picasso.get().load(imageURL).into(myviewholder.ImageView)
        myviewholder.CategoryCard.setOnClickListener {
            call.onItemClick(position, ItemSelectActivity.Event.onCategoryClick.value)
        }

    }
}