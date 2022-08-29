package com.blueskyprojects.eisa.data.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blueskyprojects.newlaundry.R
import com.blueskyprojects.newlaundry.data.model.Customer
import com.blueskyprojects.newlaundry.interfaces.RcCallback

import kotlinx.android.synthetic.main.customer_item.view.*


class CustomerAdapter(private val customerItems: List<Customer>, val call: RcCallback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textCutomerName = view.textView_name
        val textView_IC = view.textView_IC
        val textView_mobile = view.textView_mobile
        val textView_vehicle_num = view.textView_vehicle_num
        val ItemCard = view.ItemCard


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.customer_item, parent, false)

        )

    }

    override fun getItemCount(): Int {
        return customerItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val myviewholder = holder as CustomerAdapter.ViewHolder
        var dr = ContextCompat.getDrawable(context, getBG(position));
        myviewholder.textView_IC.setBackground(dr)

        myviewholder.textCutomerName.text = customerItems[position].name
        if (customerItems[position].name.length > 0) {
            myviewholder.textView_IC.text = customerItems[position].name.substring(0, 1)
        } else {
            myviewholder.textView_IC.text = ""
        }
        myviewholder.textView_mobile.text = customerItems[position].mobile
        myviewholder.textView_vehicle_num.setText(customerItems[position].vehicle_no)
        myviewholder.ItemCard.setOnClickListener {
            call.onItemClick(position, 100)
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
