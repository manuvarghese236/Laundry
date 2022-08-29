package com.blueskyprojects.newlaundry

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import co.whytecreations.famuzz.utils.defaultPrefs
import co.whytecreations.famuzz.utils.get
import com.blueskyprojects.eisa.data.api.RfClient
import com.blueskyprojects.newlaundry.adapter.SelectedItemAdapter
import com.blueskyprojects.newlaundry.adapter.ServiceAdapter
import com.blueskyprojects.newlaundry.api.POSPrinter
import com.blueskyprojects.newlaundry.api.TempPosPrinter
import com.blueskyprojects.newlaundry.data.model.SelectedItem
import com.blueskyprojects.newlaundry.data.model.Service
import com.blueskyprojects.newlaundry.interfaces.RcCallback
import com.blueskyprojects.newlaundry.models.Job
import com.blueskyprojects.newlaundry.models.JobHeader
import com.blueskyprojects.newlaundry.models.JobPrint
import com.blueskyprojects.newlaundry.models.JobResult
import com.google.gson.GsonBuilder
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.woosim.bt.WoosimPrinter
import kotlinx.android.synthetic.main.activity_job.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class jobActivity : AppCompatActivity(), RcCallback {
    val ServiceItemList = mutableListOf<Service>()
    val serviceAdapter = ServiceAdapter(ServiceItemList, this)
    val CART_TEXT = "Cart"
    var backPressedTime = 0L
    var selectItem = mutableListOf<SelectedItem>()
    val selectItemAdapter = SelectedItemAdapter(selectItem, this)
var created_by=""
    var selectedCustomerId = ""
    var selectedCustomerName = ""
    var selectedCustomerVNo = ""
    var selectedCustomerNo = ""
    val ITEM_RESULT = 100
    val CUSTOMER_RESULT = 200

    private lateinit var cardData: ByteArray
    private val extractdata = ByteArray(300)

    @SuppressLint("HandlerLeak")
    var acthandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 0x01) {
                Log.e("+++Activity+++", "******0x01")
                val obj1 = msg.obj
                cardData = obj1 as ByteArray
                //                ToastMessage();
            } else if (msg.what == 0x02) {
                //ardData[msg.arg1] = (byte) msg.arg2;
                Log.e("+++Activity+++", "MSRFAIL: [" + msg.arg1 + "]: ")
            } else if (msg.what == 0x03) {
                Log.e("+++Activity+++", "******EOT")
            } else if (msg.what == 0x04) {
                Log.e("+++Activity+++", "******ETX")
            } else if (msg.what == 0x05) {
                Log.e("+++Activity+++", "******NACK")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Printooth.init(this);
        setContentView(R.layout.activity_job)

        woosim!!.setHandle(acthandler)
        loadServices()
        rcview_cart_items.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcview_cart_items.adapter = selectItemAdapter
        UpdateDataChanged()
        selectedCustomerId = intent.getStringExtra("customerId").toString()
        selectedCustomerName = intent.getStringExtra("customerName").toString()
        selectedCustomerVNo = intent.getStringExtra("customerVNo").toString()
        selectedCustomerNo = intent.getStringExtra("customerNo").toString()

        created_by =  defaultPrefs(this)["user_id", "1"].toString()
        if (selectedCustomerId == "") {
            var i = Intent(this, CustomerList::class.java)
            // startActivityForResult(i, CUSTOMER_RESULT)
        } else {
            TextViewCustomerName.text =
                "$selectedCustomerName[$selectedCustomerVNo/$selectedCustomerNo]"

        }

        ButtonSave.setOnClickListener {
            saveNewJob()
        }
    }

    fun Date.getPrintFormat(): String {
      //  val format = SimpleDateFormat("dd-MM-YYYY HH:mm a")
      //  return format.format(this)
        return ""
    }

    lateinit var job: Job
    private fun saveNewJob() {
        var date = Calendar.getInstance().time

        var jobHeader = JobHeader(selectedCustomerId, selectedCustomerName, selectedCustomerNo, created_by, "")

        job = Job(jobHeader, selectItem)
        ButtonSave.isEnabled = false
        ButtonSave.visibility = View.INVISIBLE
        println("setting ButtonSave to false")
        RfClient.create().SaveNewJob(job).enqueue(object : Callback<JobResult> {
            override fun onFailure(call: Call<JobResult>, t: Throwable) {
                ButtonSave.isEnabled = true
                ButtonSave.visibility = View.VISIBLE
            }

            override fun onResponse(call: Call<JobResult>, response: Response<JobResult>) {
                ButtonSave.isEnabled = true
                ButtonSave.visibility = View.VISIBLE
                var jobResult = response.body()!!
                if (jobResult.status && jobResult.jobId > 0) {
                    jobSavedsuccess(jobResult)
                } else {

                }
            }

        })
    }

    private fun jobSavedsuccess(jobResult: JobResult) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Success")
        builder.setMessage("Job#${jobResult.jobId} Saved Successfully. Do you want Print?")
        builder.setNegativeButton(
            "Yes",
            DialogInterface.OnClickListener { dialog, id ->
                RfClient.create().LoadJob(jobResult.jobId.toString())
                    .enqueue(object : Callback<JobPrint> {
                        override fun onFailure(call: Call<JobPrint>, t: Throwable) {
                            //Toasty.error(this, "Error on sending to printer").show()
                        }

                        override fun onResponse(
                            call: Call<JobPrint>,
                            response: Response<JobPrint>
                        ) {
                            var _job = response.body()!!
                            var result = printBill(_job)
                            finish()
                        }

                    })

            })
        builder.setPositiveButton(
            "No",
            DialogInterface.OnClickListener { dialog, id ->
                // printBill()
                finish()
            })
        builder.show()

    }

    private var printing: Printing? = null
    private var woosim: WoosimPrinter = WoosimPrinter()
    private fun printBill(_job: JobPrint): Int {
        TempPosPrinter().printJob(woosim, this@jobActivity, _job)
        //  Toasty.info(this, "Start Printing").show()
        if (Printooth.hasPairedPrinter()) {
            if (woosim!!.BTConnection(Printooth.getPairedPrinter()!!.address, true) == 1) {
                // Toasty.info(this, "sending to printer").show()
                POSPrinter().printJob(woosim, this@jobActivity, _job)

            } else {

            }
        } else {
            startActivityForResult(
                Intent(this, ScanningActivity::class.java),
                ScanningActivity.SCANNING_FOR_PRINTER
            )
            //Toasty.error(this, "Printooth.hasPairedPrinter NOT FOUND ").show()
        }
        return 0

    }

    private fun UpdateDataChanged() {
        LayoutButton.visibility = if (selectItem.size > 0) View.VISIBLE else View.GONE
        selectItemAdapter.notifyDataSetChanged()
        updateCartText()
    }

    private fun loadServices() {
        recyleview_service.visibility = View.VISIBLE
        recyleview_service.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        RfClient.create().getService().enqueue(object : Callback<List<Service>> {
            override fun onFailure(call: Call<List<Service>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                var list = response.body()
                if (list != null) {
                    ServiceItemList.addAll(list)
                    recyleview_service.adapter = serviceAdapter
                    serviceAdapter.notifyDataSetChanged()

                }
            }

        })
        recyleview_service.visibility = View.VISIBLE
    }

    override fun onItemClick(position: Int, type: Int) {
        if (type == ItemSelectActivity.Event.onItemRemove.value) {
            selectItem.removeAt(position)
            UpdateDataChanged()
        } else if (type == ItemSelectActivity.Event.onUpdate.value) {
            UpdateDataChanged()
        } else {
            var intent = Intent(this@jobActivity, ItemSelectActivity::class.java)
            intent.putExtra("serviceId", ServiceItemList[position].id.toString())
            intent.putExtra("serviceName", ServiceItemList[position].name.toString())
            startActivityForResult(intent, ITEM_RESULT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((resultCode == Activity.RESULT_OK) && (requestCode == ITEM_RESULT)) {
            var fileData = data?.getStringExtra("listJson").toString()
            val gson = GsonBuilder().create()
            val packagesArray = gson.fromJson(fileData, Array<SelectedItem>::class.java).toList()
            selectItem.addAll(packagesArray)
            UpdateDataChanged()
        } else if (requestCode == CUSTOMER_RESULT) {
            var isCustomerSelected = false;
            // if return without any Customer from customer list. its must closed. we can cont. without a selected customer.
            if (resultCode == Activity.RESULT_OK) {
                selectedCustomerId = data?.getStringExtra("customerId").toString()
                selectedCustomerName = data?.getStringExtra("customerName").toString()
                if (selectedCustomerId != "") {
                    isCustomerSelected = true;
                }

            }
            if (!isCustomerSelected) {
                // if customer is not selected then cancel
                setResult(Activity.RESULT_CANCELED)
                finish()
            } else {
                loadServices()
            }
        }
    }

    private fun updateCartText() {
        var totalQty = 0
        for (_i in selectItem) {
            totalQty += _i.qty

        }
        setTitle(CART_TEXT + "(${totalQty})")
    }

    override fun onBackPressed() {
        val t = System.currentTimeMillis()
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t
            Toast.makeText(
                this, "Press back again to cancel JOB",
                Toast.LENGTH_SHORT
            ).show()
        } else {    // this guy is serious
            // clean up
            super.onBackPressed() // bye
        }

    }

}