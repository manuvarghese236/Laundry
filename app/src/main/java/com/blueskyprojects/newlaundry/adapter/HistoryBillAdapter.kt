package com.blueskyprojects.newlaundry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blueskyprojects.newlaundry.R
import com.blueskyprojects.newlaundry.data.model.ReportBillItem
import com.blueskyprojects.newlaundry.interfaces.RcCallback
import kotlinx.android.synthetic.main.bill_item.view.*

class HistoryBillAdapter(private val items: List<ReportBillItem>, var call: RcCallback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bill_item_id = view.bill_item_id
        val textCutomerName = view.textViewCustosmer
        val textView_date = view.textViewDate
        val textView_amount = view.textViewAmount
        val textView_inv_num = view.textViewInv_num


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        return HistoryBillAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.bill_item, parent, false)

        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myviewholder = holder as HistoryBillAdapter.ViewHolder
        myviewholder.textCutomerName.text = items[position].CustomerName
        myviewholder.textView_amount.text = items[position].amount
        myviewholder.textView_date.text = items[position].date
        myviewholder.textView_inv_num.text = "#" + items[position].id
        myviewholder.bill_item_id.setOnLongClickListener{
            call.onItemClick(position, 100)
            return@setOnLongClickListener true;
        }
    }
}