package com.blueskyprojects.newlaundry

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blueskyprojects.eisa.data.api.RfClient
import com.blueskyprojects.newlaundry.adapter.CategoryAdapter
import com.blueskyprojects.newlaundry.adapter.ItemAdapter
import com.blueskyprojects.newlaundry.adapter.SelectedItemAdapter
import com.blueskyprojects.newlaundry.data.model.Category
import com.blueskyprojects.newlaundry.data.model.Item
import com.blueskyprojects.newlaundry.data.model.SelectedItem
import com.blueskyprojects.newlaundry.interfaces.RcCallback
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_item_select.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ItemSelectActivity : AppCompatActivity(), RcCallback {
    enum class Event(val value: Int) {
        onCategoryClick(1), onItemClick(2), onItemRemove(3), onUpdate(4)
    }

    val CART_TEXT = "Cart"

    val CategoryItemList = mutableListOf<Category>()
    val categoryAdapter = CategoryAdapter(CategoryItemList, this)

    val ItemList = mutableListOf<Item>()
    val itemAdapter = ItemAdapter(ItemList, this)

    var selectItem = mutableListOf<SelectedItem>()
    val selectItemAdapter = SelectedItemAdapter(selectItem, this)
    var backPressedTime = 0L
    var selectedServiceId = ""
    var selectedServiceName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_select)

        //
        selectedServiceId = intent.getStringExtra("serviceId").toString()
        selectedServiceName = intent.getStringExtra("serviceName").toString()
        var layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)

        rcview_items.layoutManager = layoutManager

        rcview_items.adapter = itemAdapter
        loadCategory()
        rcview_cart_items.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcview_cart_items.adapter = selectItemAdapter
        imageButtonNew.setOnClickListener {
            selectionCompleted()
        }
        imageButtonCart.setOnClickListener {
            toggleCart()
        }
        setTitle(selectedServiceName + " " + CART_TEXT)
    }

    private fun toggleCart() {
        rcview_cart_items.visibility =
            if (rcview_cart_items.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun selectionCompleted() {
        var returnIntent = Intent()
        var listJson = Gson().toJson(selectItem)
        returnIntent.putExtra("listJson", listJson)
        setResult(Activity.RESULT_OK, returnIntent);
        finish()
    }

    private fun loadCategory() {
        progress_bar.visibility = View.VISIBLE
        recyleview_category.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        RfClient.create().getCategory().enqueue(object : Callback<List<Category>> {
            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                progress_bar.visibility = View.GONE
            }

            override fun onResponse(
                call: Call<List<Category>>,
                response: Response<List<Category>>
            ) {
                var list = response.body()
                if (list != null) {
                    CategoryItemList.addAll(list)
                    recyleview_category.adapter = categoryAdapter
                }
                progress_bar.visibility = View.GONE
            }

        })

    }

    override fun onItemClick(position: Int, type: Int) {
        if (type == Event.onCategoryClick.value) {
            LoadItems(position)
        } else if (type == Event.onItemClick.value) {
            addSelectedItemToCardt(position)
        } else if (type == Event.onItemRemove.value) {
            removeSeletecedItemFromCart(position)

        }
        updateCartText()
    }

    private fun updateCartText() {
        var totalQty = 0
        for (_i in selectItem) {
            totalQty += _i.qty

        }
        setTitle(selectedServiceName + " " + CART_TEXT + "(${totalQty})")
    }

    private fun removeSeletecedItemFromCart(position: Int) {
        selectItem.removeAt(position)
        selectItemAdapter.notifyDataSetChanged()
        updateCartText();
    }

    private fun addSelectedItemToCardt(position: Int) {
        val _item = ItemList[position]
        val index = selectItem.indexOfFirst { it.itemId == _item.id.toString() }
        if (index < 0) {
            var selectitem =
                SelectedItem(
                    _item.name,
                    _item.id.toString(),
                    selectedServiceId,
                    selectedServiceName,
                    _item.fixed_price,
                    _item.urgent_fees,
                    _item.pic,
                    "N",
                    _item.rate,
                    1,
                    _item.rate
                )
            selectItem.add(selectItem.size, selectitem)
        } else {
            var urgent_addition = if (selectItem[position].urgent == "Y") selectItem[position].urgent_fees else 0F
            selectItem[index].qty++;
            selectItem[index].total =
                selectItem[index].qty * (selectItem[index].rate + urgent_addition)
        }
        selectItemAdapter.notifyDataSetChanged()
        updateCartText()

    }

    private fun LoadItems(position: Int) {
        progress_bar.visibility = View.VISIBLE
        var CategoryId = CategoryItemList[position].id.toString()
        ItemList.clear();
        itemAdapter.notifyDataSetChanged()
        RfClient.create().getItem(selectedServiceId, CategoryId)
            .enqueue(object : Callback<List<Item>> {
                override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                    progress_bar.visibility = View.GONE
                }

                override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                    ItemList.clear();
                    var _item = response.body()!!
                    ItemList.addAll(_item)
                    itemAdapter.notifyDataSetChanged()
                    progress_bar.visibility = View.GONE
                }
            })
    }

    private fun ClearAll() {
        recyleview_category.visibility = View.GONE
        //Itemlist.visibility = View.GONE
    }

    override fun onBackPressed() {
        val t = System.currentTimeMillis()
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t
            Toast.makeText(
                this, "Press back again to cancel",
                Toast.LENGTH_SHORT
            ).show()
        } else {    // this guy is serious
            // clean up
            super.onBackPressed() // bye
        }

    }
}