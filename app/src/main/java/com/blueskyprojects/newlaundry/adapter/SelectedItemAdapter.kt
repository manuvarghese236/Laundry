package com.blueskyprojects.newlaundry.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.blueskyprojects.newlaundry.ItemSelectActivity
import com.blueskyprojects.newlaundry.R
import com.blueskyprojects.newlaundry.data.model.SelectedItem
import com.blueskyprojects.newlaundry.interfaces.RcCallback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.selected_item.view.*

class SelectedItemAdapter(private var items: List<SelectedItem>, var call: RcCallback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewItemName = view.textViewItemName
        val imageViewMinus = view.imageViewMinus
        val imageViewPlus = view.imageViewPlus
        val textViewRate = view.textViewRate
        val textViewTotal = view.textViewTotal
        val editTextQty = view.editTextQty
        val imageView = view.imageView5
        val switchurgent = view.switchurgent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.selected_item, parent, false)

        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var myviewholder = holder as SelectedItemAdapter.ViewHolder
        myviewholder.textViewItemName.text =
            items[position].itemName + "[" + items[position].serviceName + "]"
        if(items[position].urgent == "Y"){
            myviewholder.textViewRate.text = (items[position].rate +  items[position].urgent_fees ).toString()
            myviewholder.switchurgent.isChecked = true
        }else{
            myviewholder.textViewRate.text = items[position].rate.toString()
        }

        myviewholder.editTextQty.setText(items[position].qty.toString())
        myviewholder.textViewTotal.text = items[position].total.toString()
        var imageURL = items[position].pic
        Picasso.get().load(imageURL).into(myviewholder.imageView)
        myviewholder.imageViewMinus.setOnClickListener {
            if (items[position].qty > 1) {
                items[position].qty = items[position].qty - 1;
                updateTotal(position)
            } else {
                call.onItemClick(position, ItemSelectActivity.Event.onItemRemove.value)
            }
            call.onItemClick(position, ItemSelectActivity.Event.onUpdate.value)
            this.notifyDataSetChanged();
        }

        myviewholder.imageViewPlus.setOnClickListener {
            items[position].qty = items[position].qty + 1;
            updateTotal(position)
            this.notifyDataSetChanged();
        }
        if (!items[position].fixed_price) {
            myviewholder.textViewRate.setTypeface(null, Typeface.BOLD);
            myviewholder.textViewRate.setTextColor(Color.parseColor("#0800ff"))
            myviewholder.textViewRate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
            myviewholder.textViewRate.setOnClickListener {
                var dialog = Dialog(context)
                dialog.setContentView(R.layout.rate_input)
                val rateEdit = dialog.findViewById<EditText>(R.id.editTextNumberDecimal)
                rateEdit.setText(myviewholder.textViewRate.text)
                val buttonCancel = dialog.findViewById<Button>(R.id.buttonCancel)
                val buttonComplete = dialog.findViewById<Button>(R.id.buttonSave)
                buttonCancel.setOnClickListener {
                    dialog.hide()
                }
                buttonComplete.setOnClickListener {
                    items[position].rate = rateEdit.text.toString().toFloat();
                    updateTotal(position)
                    this.notifyDataSetChanged();
                    dialog.hide()
                }
                dialog.show()
            }
        }
        myviewholder.switchurgent.setOnCheckedChangeListener { compoundButton, b ->
            Log.d("U", "switchurgent.setOnCheckedChangeListener")
            items[position].urgent = if (b) "Y" else "N"
            updateTotal(position)
            this.notifyDataSetChanged();
        }

    }

    private fun updateTotal(position: Int) {
        var urgent_addition = if(items[position].urgent == "Y") items[position].urgent_fees else 0F
        items[position].total = (items[position].rate + urgent_addition) * items[position].qty
        call.onItemClick(position, ItemSelectActivity.Event.onUpdate.value)
    }
}