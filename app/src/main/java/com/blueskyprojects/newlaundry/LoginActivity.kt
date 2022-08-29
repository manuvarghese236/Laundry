package com.blueskyprojects.newlaundry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import co.whytecreations.famuzz.utils.defaultPrefs
import co.whytecreations.famuzz.utils.set
import com.blueskyprojects.eisa.data.api.BASE_URL
import com.blueskyprojects.eisa.data.api.RfClient
import com.blueskyprojects.newlaundry.data.model.LoginResponse
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    var logo_url = "${BASE_URL}uploads/icon/logo.png";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        try {
            Picasso.get().load(logo_url).into(loginLogo)
        }catch (ex: Exception){

        }
        button_process.setOnClickListener {

            progressBar.visibility = View.VISIBLE
            RfClient.create()
                .login(editTextTextUserName.text.toString(), editTextTextPassword.text.toString())
                .enqueue(object :
                    Callback<LoginResponse> {
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        progressBar.visibility = View.GONE
                        t.printStackTrace()
                    }

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val status: Boolean? = response.body()?.status;
                        var name = response.body()?.data?.name;


                        if (status!!) {
                            var user_id = response.body()?.data?.user_id;
                            defaultPrefs(this@LoginActivity)["user_id"] = user_id.toString()
                            defaultPrefs(this@LoginActivity)["name"] = name
                            defaultPrefs(this@LoginActivity)["username"] =
                                editTextTextUserName.text.toString()
                            Toasty.success(
                                applicationContext,
                                "Login Success",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))

                        } else {
                            Toasty.error(
                                applicationContext,
                                "Login Failed. Incorrect Username/Password",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        progressBar.visibility = View.GONE
                    }

                })


        }

    }

    override fun onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}