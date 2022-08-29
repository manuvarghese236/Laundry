package com.blueskyprojects.newlaundry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blueskyprojects.newlaundry.ItemSelectActivity
import com.blueskyprojects.newlaundry.R

import com.blueskyprojects.newlaundry.interfaces.RcCallback
import com.blueskyprojects.newlaundry.models.JobReturnItem
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.return_item.view.*
import kotlinx.android.synthetic.main.return_item.view.imageViewMinus
import kotlinx.android.synthetic.main.return_item.view.imageViewPlus
import kotlinx.android.synthetic.main.return_item.view.textViewItemName
import kotlinx.android.synthetic.main.return_item.view.textViewRate


class JobReturnItemAdapter(private var items: List<JobReturnItem>, var call: RcCallback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewItemName = view.textViewItemName
        val textViewRate = view.textViewRate
        val editTextNumberQty = view.editTextNumberQty
        val imageView = view.imageView7
        val textViewSubText = view.textViewService
        val textViewsubTotal = view.textViewsubTotal
        val imageViewMinus = view.imageViewMinus
        val imageViewPlus = view.imageViewPlus
        val textViewAvailQty = view.textViewAvailQty
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.return_item, parent, false)

        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var myviewholder = holder as JobReturnItemAdapter.ViewHolder
        myviewholder.textViewItemName.text = items[position].productName
        myviewholder.textViewSubText.text = items[position].serviceName
        myviewholder.textViewRate.text = items[position].rate.toString()


        myviewholder.textViewAvailQty.text = "In Store " +
            (items[position].qty - items[position].total_return_qty).toString()
        myviewholder.editTextNumberQty.setText(items[position].returned_qty.toString())
        var imageURL = items[position].pic

        myviewholder.textViewsubTotal.text = items[position].total.toString()
        Picasso.get().load(imageURL).into(myviewholder.imageView)

        myviewholder.imageViewMinus.setOnClickListener {
            if (items[position].returned_qty > 1) {
                items[position].returned_qty--;
                updateTotal(position)
                call.onItemClick(position, ItemSelectActivity.Event.onUpdate.value)
            } else {
                call.onItemClick(position, ItemSelectActivity.Event.onItemRemove.value)
            }
            this.notifyDataSetChanged();
        }
        myviewholder.imageViewPlus.setOnClickListener {
            var remainQty = (items[position].qty - items[position].total_return_qty)
            if (items[position].returned_qty < remainQty) {
                items[position].returned_qty++
                updateTotal(position)
                call.onItemClick(position, ItemSelectActivity.Event.onUpdate.value)
                this.notifyDataSetChanged();
            } else {
                var errorMessage = "Qty must be less than ${remainQty}"
                Toasty.error(context, errorMessage, Toasty.LENGTH_SHORT).show()
            }
        }


    }

    private fun updateTotal(position: Int) {
        items[position].total = (items[position].returned_qty * items[position].rate)

    }
}