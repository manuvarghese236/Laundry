package com.blueskyprojects.newlaundry


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager

import co.whytecreations.famuzz.utils.defaultPrefs
import co.whytecreations.famuzz.utils.get
import co.whytecreations.famuzz.utils.set
import com.blueskyprojects.eisa.data.api.RfClient
import com.blueskyprojects.eisa.data.model.MenuItemAdapter
import com.blueskyprojects.newlaundry.interfaces.MainMenuCallBack
import com.blueskyprojects.newlaundry.models.Company
import com.blueskyprojects.newlaundry.models.MainMenuItem
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), MainMenuCallBack {
    val MainMenuItemArray: MutableList<MainMenuItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         var titleName = defaultPrefs(this@MainActivity)["company", "Home"]
        if(titleName !=null && titleName.length>1){
            title =titleName
        }else{
            title = "Home"
        }
        LoadMenu()
        LoadCompany();
    }

    private fun LoadCompany() {
        RfClient.create().loadCompany().enqueue(object: Callback<Company>{
            override fun onFailure(call: Call<Company>, t: Throwable) {

            }

            override fun onResponse(call: Call<Company>, response: Response<Company>) {
                var companyDetail  = response.body()!!
                defaultPrefs(this@MainActivity)["company"] = companyDetail.name
                defaultPrefs(this@MainActivity)["address"] = companyDetail.address
                defaultPrefs(this@MainActivity)["address2"] = companyDetail.address2
                defaultPrefs(this@MainActivity)["mobile"] = companyDetail.mobile
                defaultPrefs(this@MainActivity)["website"] = companyDetail.website
                title = companyDetail.name;
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.barmenu, menu)

        return true
    }


    private fun LoadMenu() {

        MainMenuItemArray.add(MainMenuItem(1, "Customers", R.drawable.ic_customer_icon))
        MainMenuItemArray.add(MainMenuItem(2, "Receive", R.drawable.ic_collect_icon))
        MainMenuItemArray.add(MainMenuItem(3, "Bill", R.drawable.ic_bill_icon))
        //      MainMenuItemArray.add(MainMenuItem(4, "Payment", R.drawable.ic_creditcard_flat))

        MainMenuItemArray.add(MainMenuItem(5, "Bill History", R.drawable.ic_creditcard_flat))
        //  MainMenuItemArray.add(MainMenuItem(6, "Payment History", R.drawable.ic_creditcard_flat))
        println("MainMenuItemArray.size = " + MainMenuItemArray.size)
        val adapter = MenuItemAdapter(MainMenuItemArray, this)
        recyle_menu_list.layoutManager =
            GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false)
        recyle_menu_list.adapter = adapter

    }

    override fun onMainMenuClick(position: Int, type: Int) {
        if (MainMenuItemArray[position].id == 1) {
            // New Cutomer
            var i = Intent(this, CustomerList::class.java)
            startActivityForResult(i, MainMenuItemArray[position].id)
        } else if (MainMenuItemArray[position].id == 2) {
            //Collect or receive clothes
            var i = Intent(this, CustomerList::class.java)
            startActivityForResult(i, MainMenuItemArray[position].id)
        } else if (MainMenuItemArray[position].id == 3) {
            var i = Intent(this, CustomerList::class.java)
            startActivityForResult(i, MainMenuItemArray[position].id)
        } else if (MainMenuItemArray[position].id == 4) {
            var i = Intent(this, CustomerList::class.java)
            startActivityForResult(i, MainMenuItemArray[position].id)
        } else if (MainMenuItemArray[position].id == 5) {
            var i = Intent(this, CustomerList::class.java)
            startActivityForResult(i, MainMenuItemArray[position].id)
        } else if (MainMenuItemArray[position].id == 6) {
            var i = Intent(this, CustomerList::class.java)
            startActivityForResult(i, MainMenuItemArray[position].id)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode != null) && (resultCode == Activity.RESULT_OK)) {
            if (requestCode == 1) {
                // Customer
            } else if (requestCode == 2) {
                var customerId = data?.getStringExtra("customerId").toString()
                var customerName = data?.getStringExtra("customerName")
                var customerVNo = data?.getStringExtra("customerVNo")
                var customerNo = data?.getStringExtra("customerNo")
                if (customerId != "") {
                    var i = Intent(this@MainActivity, jobActivity::class.java)
                    i.putExtra("customerId", customerId)
                    i.putExtra("customerName", customerName)
                    i.putExtra("customerVNo", customerVNo)
                    i.putExtra("customerNo", customerNo)
                    startActivity(i)
                }
            } else if (requestCode == 3) {
                var customerId = data?.getStringExtra("customerId").toString()
                var customerName = data?.getStringExtra("customerName")
                var customerVNo = data?.getStringExtra("customerVNo")
                var customerNo = data?.getStringExtra("customerNo")
                if (customerId != "") {
                    var i = Intent(this@MainActivity, ReturnActivity::class.java)
                    i.putExtra("customerId", customerId)
                    i.putExtra("customerName", customerName)
                    i.putExtra("customerVNo", customerVNo)
                    i.putExtra("customerNo", customerNo)
                    startActivity(i)
                }
            } else if (requestCode == 4) {
                var customerId = data?.getStringExtra("customerId").toString()
                var customerName = data?.getStringExtra("customerName")
                var customerVNo = data?.getStringExtra("customerVNo")
                var customerNo = data?.getStringExtra("customerNo")
                if (customerId != "") {
                    var i = Intent(this@MainActivity, PaymentActivity::class.java)
                    i.putExtra("customerId", customerId)
                    i.putExtra("customerName", customerName)
                    i.putExtra("customerVNo", customerVNo)
                    i.putExtra("customerNo", customerNo)
                    startActivity(i)
                }
            } else if (requestCode == 5) {
                var customerId = data?.getStringExtra("customerId").toString()
                var customerName = data?.getStringExtra("customerName")
                var customerVNo = data?.getStringExtra("customerVNo")
                var customerNo = data?.getStringExtra("customerNo")
                if (customerId != "") {
                    var i = Intent(this@MainActivity, HistoryBillActivity::class.java)
                    i.putExtra("customerId", customerId)
                    i.putExtra("customerName", customerName)
                    i.putExtra("customerVNo", customerVNo)
                    i.putExtra("customerNo", customerNo)
                    startActivity(i)
                }
            } else if (requestCode == 6) {
                var customerId = data?.getStringExtra("customerId").toString()
                var customerName = data?.getStringExtra("customerName")
                var customerVNo = data?.getStringExtra("customerVNo")
                var customerNo = data?.getStringExtra("customerNo")
                if (customerId != "") {
                    var i = Intent(this@MainActivity, HistoryPaymentActivity::class.java)
                    i.putExtra("customerId", customerId)
                    i.putExtra("customerName", customerName)
                    i.putExtra("customerVNo", customerVNo)
                    i.putExtra("customerNo", customerNo)
                    startActivity(i)
                }
            }

        }

    }

    override fun onBackPressed() {

    }
}