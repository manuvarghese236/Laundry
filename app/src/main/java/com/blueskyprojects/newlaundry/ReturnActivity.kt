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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.whytecreations.famuzz.utils.defaultPrefs
import co.whytecreations.famuzz.utils.get
import com.blueskyprojects.eisa.data.api.RfClient
import com.blueskyprojects.newlaundry.adapter.JobReturnItemAdapter
import com.blueskyprojects.newlaundry.api.POSPrinter
import com.blueskyprojects.newlaundry.interfaces.RcCallback
import com.blueskyprojects.newlaundry.models.*
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.ui.ScanningActivity
import com.woosim.bt.WoosimPrinter
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_return.*
import kotlinx.android.synthetic.main.activity_return.ButtonSave
import kotlinx.android.synthetic.main.activity_return.TextViewCustomerName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReturnActivity : AppCompatActivity(), RcCallback {
    var selectedCustomerId = ""
    var selectedCustomerName = ""
    var selectedCustomerVNo = ""
    var selectedCustomerNo = ""
    var selectItem = mutableListOf<JobReturnItem>()
    var created_by = ""
    var selectItemAdapter = JobReturnItemAdapter(selectItem, this)
    private lateinit var cardData: ByteArray
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
        setContentView(R.layout.activity_return)
        Printooth.init(this);
        woosim!!.setHandle(acthandler)
        rcv_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcv_list.adapter = selectItemAdapter
        selectedCustomerId = intent.getStringExtra("customerId").toString()
        selectedCustomerName = intent.getStringExtra("customerName").toString()
        selectedCustomerVNo = intent.getStringExtra("customerVNo").toString()
        selectedCustomerNo = intent.getStringExtra("customerNo").toString()
        created_by =  defaultPrefs(this)["user_id", "1"].toString()
        TextViewCustomerName.text =
            "$selectedCustomerName[$selectedCustomerVNo/$selectedCustomerNo]"
        loadReturnList(selectedCustomerId)
        ButtonBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        ButtonSave.setOnClickListener {
            SaveBill()
        }
        val simpleItemTouchCallback: SimpleCallback = object :
            SimpleCallback(
                0,
                LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP
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
                selectItem.removeAt(position)
                selectItemAdapter.notifyDataSetChanged()
                updateTotal()
            }
        }


        val helper = ItemTouchHelper(simpleItemTouchCallback)
        helper.attachToRecyclerView(rcv_list)
    }

    private fun SaveBill() {
        ButtonSave.isEnabled = false
        ButtonSave.visibility = View.INVISIBLE
        var invoice = Invoice(selectedCustomerId, created_by, selectItem)
        RfClient.create().SaveBill(invoice).enqueue(object : Callback<InvoiceResult> {
            override fun onFailure(call: Call<InvoiceResult>, t: Throwable) {
                var errorMessage = "Error in connecting server. Try again."
                Toasty.error(this@ReturnActivity, errorMessage, Toasty.LENGTH_LONG).show()
                ButtonSave.isEnabled = true
                ButtonSave.visibility = View.VISIBLE
            }

            override fun onResponse(call: Call<InvoiceResult>, response: Response<InvoiceResult>) {
                ButtonSave.isEnabled = true
                ButtonSave.visibility = View.VISIBLE
                var result = response.body()!!
                if (result.status && result.Id > 0) {
                    savedSuccess(result)
                } else {

                }
            }

        })
    }

    private fun savedSuccess(jobResult: InvoiceResult) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Success")
        builder.setMessage("Invoice #:${jobResult.Id} Saved Successfully. Do you want Print?")
        builder.setNegativeButton(
            "Yes",
            DialogInterface.OnClickListener { dialog, id ->
                RfClient.create().LoadInvoice(jobResult.Id.toString())
                    .enqueue(object : Callback<InvoicePrint> {
                        override fun onFailure(call: Call<InvoicePrint>, t: Throwable) {
                            //Toasty.error(this, "Error on sending to printer").show()
                        }

                        override fun onResponse(
                            call: Call<InvoicePrint>,
                            response: Response<InvoicePrint>
                        ) {
                            var _job = response.body()!!
                            var result = printBill(_job)
                            finish()
                        }

                    })
                finish()
            })
        builder.setPositiveButton(
            "No",
            DialogInterface.OnClickListener { dialog, id -> finish() })
        builder.show()

    }

    private var woosim: WoosimPrinter = WoosimPrinter()
    private fun printBill(_job: InvoicePrint): Int {

        //  Toasty.info(this, "Start Printing").show()
        if (Printooth.hasPairedPrinter()) {
            if (woosim!!.BTConnection(Printooth.getPairedPrinter()!!.address, true) == 1) {
                // Toasty.info(this, "sending to printer").show()
                POSPrinter().printInvoice(woosim, this@ReturnActivity, _job)

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

    private fun loadReturnList(_selectedCustomerId: String) {
        RfClient.create().RetunList(_selectedCustomerId)
            .enqueue(object : Callback<List<JobReturnItem>> {
                override fun onFailure(call: Call<List<JobReturnItem>>, t: Throwable) {
                    var errorMessage = "Error while Loading"
                    Toasty.error(this@ReturnActivity, errorMessage, Toasty.LENGTH_LONG).show()
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }

                override fun onResponse(
                    call: Call<List<JobReturnItem>>,
                    response: Response<List<JobReturnItem>>
                ) {
                    selectItem.clear()
                    selectItem.addAll(response.body()!!)


                    ButtonSave.setEnabled((selectItem.size > 0))

                    selectItemAdapter.notifyDataSetChanged()
                    updateTotal()
                }

            })
    }

    override fun onItemClick(position: Int, type: Int) {
        if (ItemSelectActivity.Event.onItemRemove.value == type) {
            selectItem.removeAt(position)
            ButtonSave.setEnabled((selectItem.size > 0))
            selectItemAdapter.notifyDataSetChanged()

        }
        updateTotal()
    }

    private fun updateTotal() {
        var total = 0F;
        for (_item in selectItem) {
            total += (_item.returned_qty * _item.rate)
        }
        textViewNetTotal.text = total.toString();
        NoofItems.text = selectItem.size.toString()
    }
}