package com.blueskyprojects.newlaundry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import co.whytecreations.famuzz.utils.defaultPrefs
import co.whytecreations.famuzz.utils.get
import com.blueskyprojects.eisa.data.api.RfClient
import com.blueskyprojects.newlaundry.data.model.CustomerDue
import com.blueskyprojects.newlaundry.models.Status
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_job.TextViewCustomerName
import kotlinx.android.synthetic.main.activity_payment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : AppCompatActivity() {
    var selectedCustomerId = ""
    var selectedCustomerName = ""
    var selectedCustomerVNo = ""
    var selectedCustomerNo = ""
    var customerDue = 0F
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        selectedCustomerId = intent.getStringExtra("customerId").toString()
        selectedCustomerName = intent.getStringExtra("customerName").toString()
        selectedCustomerVNo = intent.getStringExtra("customerVNo").toString()
        selectedCustomerNo = intent.getStringExtra("customerNo").toString()
        if (selectedCustomerId == "") {
            finish()
        } else {
            loadBalance(selectedCustomerId)
            TextViewCustomerName.text =
                "$selectedCustomerName[$selectedCustomerVNo/$selectedCustomerNo]"

        }
        buttonBack.setOnClickListener {
            finish()
        }
        buttonPay.setOnClickListener {
            SavePayement()
        }
        editTextNumberBal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val a = editTextNumberBal.text.toString().toFloatOrNull()

                if (a != null) {
                    var bal = customerDue - a!!;
                    textViewBal.text = bal.toString()
                } else {
                    textViewBal.text = customerDue.toString()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
    }

    private fun SavePayement() {
        buttonPay.setEnabled(false);
        var amount = editTextNumberBal.text.toString().toFloat()
        var created_by = "1";
        var isValidPayment = (amount > 0F)
        created_by = defaultPrefs(this@PaymentActivity)["user_id"]!!
        if (isValidPayment) {
            RfClient.create().SavePayment(selectedCustomerId, amount, created_by)
                .enqueue(object : Callback<Status> {
                    override fun onFailure(call: Call<Status>, t: Throwable) {
                        var errorMessage = "Error in connecting server"
                        Toasty.error(this@PaymentActivity, errorMessage, Toasty.LENGTH_LONG).show()
                        buttonPay.setEnabled(true);
                    }

                    override fun onResponse(call: Call<Status>, response: Response<Status>) {
                        var result = response.body()!!
                        if (result.status) {
                            var successMessage = "Receipt ID ${result.id} Saved"
                            Toasty.success(this@PaymentActivity, successMessage, Toasty.LENGTH_LONG)
                                .show()
                            finish()
                        } else {
                            var errorMessage = "Error in proccessing"
                            Toasty.error(this@PaymentActivity, errorMessage, Toasty.LENGTH_LONG)
                                .show()
                            buttonPay.setEnabled(true);
                        }
                    }

                })
        } else {
            var errorMessage = "Invalid Data"
            Toasty.error(this@PaymentActivity, errorMessage, Toasty.LENGTH_LONG).show()
            buttonPay.setEnabled(true);
        }

    }

    private fun loadBalance(selectedCustomerId: String) {
        RfClient.create().getDue(selectedCustomerId).enqueue(object : Callback<CustomerDue> {
            override fun onFailure(call: Call<CustomerDue>, t: Throwable) {
                var errorMessage = "Error in connecting Server"
                Toasty.error(this@PaymentActivity, errorMessage, Toasty.LENGTH_LONG).show()
                finish()
            }

            override fun onResponse(call: Call<CustomerDue>, response: Response<CustomerDue>) {
                var due = response.body()!!
                customerDue = due.due
                editTextNumberBal.setText(customerDue.toString());
                textViewDue.text = customerDue.toString()
            }

        })
    }
}