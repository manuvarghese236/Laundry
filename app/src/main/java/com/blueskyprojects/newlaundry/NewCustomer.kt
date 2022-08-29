package com.blueskyprojects.newlaundry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blueskyprojects.eisa.data.api.RfClient
import com.blueskyprojects.newlaundry.data.model.ResponseStatus

import es.dmoral.toasty.Toasty

import kotlinx.android.synthetic.main.activity_new_customer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewCustomer : AppCompatActivity() {
    var selectedCustomerId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_customer)
        new_customer_rest.setOnClickListener {
            resetCustomer()
        }
        new_customer_save.setOnClickListener {
            SaveCustomer()
        }
        selectedCustomerId = intent.getStringExtra("id").toString()
        if (selectedCustomerId != "") {
            new_customer_name.setText(intent.getStringExtra("name"))
            new_customer_phone.setText(intent.getStringExtra("mobile"))
            new_customer_vehicle.setText(intent.getStringExtra("vehicle_no"))
            new_customer_email.setText(intent.getStringExtra("email"))
            new_customer_address.setText(intent.getStringExtra("address"))

        }
    }

    private fun SaveCustomer() {
        if (validateCustomer()) {
            setButtonActive(false)
            RfClient.create().saveCustomer(
                selectedCustomerId,
                new_customer_name.text.toString(),
                new_customer_phone.text.toString(),
                new_customer_email.text.toString(),
                new_customer_vehicle.text.toString(),
                new_customer_address.text.toString(),
                "0"
            ).enqueue(object : Callback<ResponseStatus> {
                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    Toasty.error(
                        this@NewCustomer,
                        "Error on Connecting to server",
                        Toasty.LENGTH_SHORT
                    ).show()
                }

                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
                    var status = response.body()
                    if (status!!.status) {
                        selectedCustomerId = status.id.toString()
                        Toasty.success(
                            this@NewCustomer,
                            "Customer Saved",
                            Toasty.LENGTH_SHORT
                        ).show()
                        finish()

                    } else {
                        Toasty.error(
                            this@NewCustomer,
                            status.errorMessage.toString(),
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            })
            setButtonActive(true)
        }
    }

    fun validateCustomer(): Boolean {
        var isValidate = true
        var errorMessage = ""
//        if (new_customer_vehicle.text.length < 2) {
//            errorMessage += "Invalid Vehicle No. "
//            isValidate = false
//        }
        if (new_customer_name.text.length < 2) {
            errorMessage += "Invalid Name. "
            isValidate = false
        }
        if (!isValidate) {
            Toasty.error(this, errorMessage, Toasty.LENGTH_SHORT).show()
        }

        return isValidate
    }

    fun resetCustomer() {
        finish()
//        new_customer_address.text.clear()
//        new_customer_email.text.clear()
//        new_customer_address.text.clear()
//        new_customer_name.text.clear()
//        new_customer_phone.text.clear()
//        new_customer_vehicle.text.clear()
//        selectedCustomerId = ""
//        setButtonActive(true)
    }

    fun setButtonActive(status: Boolean) {
        new_customer_rest.setEnabled(status)
        new_customer_save.setEnabled(status)
    }

}