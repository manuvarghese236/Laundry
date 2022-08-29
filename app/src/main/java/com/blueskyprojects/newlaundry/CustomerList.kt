package com.blueskyprojects.newlaundry

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blueskyprojects.eisa.data.api.RfClient

import com.blueskyprojects.eisa.data.model.CustomerAdapter
import com.blueskyprojects.newlaundry.data.model.Customer
import com.blueskyprojects.newlaundry.data.model.CustomerResponse
import com.blueskyprojects.newlaundry.interfaces.RcCallback

import kotlinx.android.synthetic.main.activity_customer_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerList : AppCompatActivity(), RcCallback {
    val CustomerItemList = mutableListOf<Customer>()
    val customerAdapter = CustomerAdapter(CustomerItemList, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_list)
        rc_customerlist.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rc_customerlist.adapter = customerAdapter
        LoadingCL.visibility = View.GONE
        imageButtonSearch.setOnClickListener {
            LoadingCL.visibility = View.VISIBLE
            loadCustomer()
        }
        imageButtonNew.setOnClickListener {
            loadNewCustomer(-1)
        }
        // Car
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {

                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                loadNewCustomer(position)
            }
        }


        val helper = ItemTouchHelper(simpleItemTouchCallback)
        helper.attachToRecyclerView(rc_customerlist)
        loadCustomer()
    }

    override fun onResume() {
        super.onResume()
        loadCustomer();
    }
    private fun loadNewCustomer(id: Int) {
        var i = Intent(this, NewCustomer::class.java)
        if (id >= 0) {
            i.putExtra("id", CustomerItemList[id].id)
            i.putExtra("name", CustomerItemList[id].name)
            i.putExtra("mobile", CustomerItemList[id].mobile)
            i.putExtra("vehicle_no", CustomerItemList[id].vehicle_no)
            i.putExtra("email", CustomerItemList[id].email)
            i.putExtra("address", CustomerItemList[id].address)
        }
        startActivity(i)
        customerAdapter.notifyDataSetChanged()
    }

    private fun loadCustomer() {
        var term = Edittext_Search.text.toString()
        RfClient.create().loadCustomer(term).enqueue(object : Callback<CustomerResponse> {
            override fun onFailure(call: Call<CustomerResponse>, t: Throwable) {
                LoadingCL.visibility = View.GONE
            }

            override fun onResponse(
                call: Call<CustomerResponse>,
                response: Response<CustomerResponse>
            ) {
                CustomerItemList.clear()
                var data = response.body()!!.data
                CustomerItemList.addAll(data)
                customerAdapter.notifyDataSetChanged()
                LoadingCL.visibility = View.GONE
            }

        })
    }

    override fun onItemClick(position: Int, type: Int) {
        var i = Intent()
        i.putExtra("customerId", CustomerItemList[position].id)
        i.putExtra("customerName", CustomerItemList[position].name)
        i.putExtra("customerVNo", CustomerItemList[position].vehicle_no)
        i.putExtra("customerNo", CustomerItemList[position].mobile.toString())
        setResult(Activity.RESULT_OK, i)
        finish()
    }
}