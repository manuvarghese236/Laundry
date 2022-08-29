package com.blueskyprojects.newlaundry

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.*
import android.util.Log
import android.view.View
import android.widget.CalendarView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blueskyprojects.eisa.data.api.RfClient
import com.blueskyprojects.newlaundry.adapter.HistoryBillAdapter
import com.blueskyprojects.newlaundry.api.POSPrinter
import com.blueskyprojects.newlaundry.api.TempPosPrinter
import com.blueskyprojects.newlaundry.data.model.ReportBillItem
import com.blueskyprojects.newlaundry.interfaces.RcCallback
import com.blueskyprojects.newlaundry.models.InvoicePrint
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.ui.ScanningActivity
import com.woosim.bt.WoosimPrinter
import kotlinx.android.synthetic.main.activity_history_bill.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HistoryBillActivity : AppCompatActivity(), RcCallback {
    private enum class CallType {
        FromDate, ToDate
    }

    var selectedCustomerId = ""
    var selectedCustomerName = ""
    var selectedCustomerVNo = ""
    var selectedCustomerNo = ""
    private var calltype = HistoryBillActivity.CallType.FromDate
    val ItemList = mutableListOf<ReportBillItem>()
    val adapterList = HistoryBillAdapter(ItemList, this)
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
        setContentView(R.layout.activity_history_bill)
        Printooth.init(this);
        woosim!!.setHandle(acthandler)
        setDefaultDate()
        selectedCustomerId = intent.getStringExtra("customerId").toString()
        selectedCustomerName = intent.getStringExtra("customerName").toString()

        TextViewCustomerName.text = selectedCustomerName

        RCV_report.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        RCV_report.adapter = adapterList
        textViewDateto.setOnClickListener {
            showCalender(CallType.ToDate)
        }
        textViewDateFrom.setOnClickListener {
            showCalender(CallType.FromDate)
        }
        calendarView2.setOnDateChangeListener { calendarView: CalendarView, year: Int, month: Int, day: Int ->

            setDate("${day}/${month + 1}/${year}")

        }
        buttonFilter.setOnClickListener {
            loadReport()
        }
    }

    private fun loadReport() {
        calendarView2.visibility = View.GONE
        RfClient.create().LoadBillReport(
            selectedCustomerId,
            textViewDateFrom.text.toString(),
            textViewDateto.text.toString()
        ).enqueue(object : Callback<List<ReportBillItem>> {
            override fun onFailure(call: Call<List<ReportBillItem>>, t: Throwable) {
                // TODO("Not yet implemented")
            }

            override fun onResponse(
                call: Call<List<ReportBillItem>>,
                response: Response<List<ReportBillItem>>
            ) {
                ItemList.clear()
                var data = response.body()!!
                ItemList.addAll(data)

                adapterList.notifyDataSetChanged()
            }

        })
    }

    private fun setDate(_date: String) {
        if (calltype == CallType.ToDate) {
            textViewDateto.text = _date
        } else {
            textViewDateFrom.text = _date
        }
        calendarView2.visibility = View.GONE
    }

    private fun showCalender(_callType: HistoryBillActivity.CallType) {
        calltype = _callType
        calendarView2.visibility = View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setDefaultDate() {
        val date: Date = Calendar.getInstance().time
        //
        // Display a date in day, month, year format
        //
        //
        // Display a date in day, month, year format
        //
        val formatter: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        val today: String = formatter.format(date)
        println(today)
        textViewDateFrom.text = today
        textViewDateto.text = today
    }

    override fun onItemClick(position: Int, type: Int) {
        var jobResult = ItemList[position]
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Success")
        builder.setMessage("Do you want Print the Invoice #${jobResult.id}?")
        builder.setNegativeButton(
            "Yes",
            DialogInterface.OnClickListener { dialog, id ->
                RfClient.create().LoadInvoice(jobResult.id.toString())
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

                        }

                    })

            })
        builder.setPositiveButton(
            "No",
            DialogInterface.OnClickListener { dialog, id -> })
        builder.show()


    }

    private var woosim: WoosimPrinter = WoosimPrinter()
    private fun printBill(_job: InvoicePrint): Int {
        TempPosPrinter().printInvoice(woosim, this@HistoryBillActivity, _job)
        //  Toasty.info(this, "Start Printing").show()
        if (Printooth.hasPairedPrinter()) {
            if (woosim!!.BTConnection(Printooth.getPairedPrinter()!!.address, true) == 1) {
                // Toasty.info(this, "sending to printer").show()
                POSPrinter().printInvoice(woosim, this@HistoryBillActivity, _job)

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
}