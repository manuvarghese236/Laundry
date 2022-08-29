package com.blueskyprojects.newlaundry

import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.blueskyprojects.eisa.data.api.RfClient
import com.blueskyprojects.newlaundry.adapter.HistoryBillAdapter
import com.blueskyprojects.newlaundry.data.model.ReportBillItem
import com.blueskyprojects.newlaundry.interfaces.RcCallback
import kotlinx.android.synthetic.main.activity_history_bill.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HistoryPaymentActivity : AppCompatActivity(), RcCallback {
    private enum class CallType {
        FromDate, ToDate
    }

    var selectedCustomerId = ""
    var selectedCustomerName = ""
    var selectedCustomerVNo = ""
    var selectedCustomerNo = ""
    private var calltype = CallType.FromDate
    val ItemList = mutableListOf<ReportBillItem>()
    val adapterList = HistoryBillAdapter(ItemList, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_payment)
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
        RfClient.create().LoadPaymentReport(
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

    private fun showCalender(_callType: CallType) {
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

    }
}