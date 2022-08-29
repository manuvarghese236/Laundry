package com.blueskyprojects.eisa.data.api


import com.blueskyprojects.newlaundry.data.model.*
import com.blueskyprojects.newlaundry.models.*
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Body

interface EndPoints {
    @FormUrlEncoded
    @POST("index.php?r=ApiUser/login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("index.php?r=ApiCustomer/search")
    fun loadCustomer(
        @Field("term") term: String
    ): Call<CustomerResponse>

    @FormUrlEncoded
    @POST("index.php?r=ApiCustomer/save")
    fun saveCustomer(
        @Field("id") id: String,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("email") email: String,
        @Field("vehicle") vehicle: String,
        @Field("address") address: String,
        @Field("created_by") created_by: String
    ): Call<ResponseStatus>

    @POST("index.php?r=Apiservice/getlist")
    fun getService(): Call<List<Service>>

    @POST("index.php?r=Apiservice/GetCategory")
    fun getCategory(): Call<List<Category>>

    @FormUrlEncoded
    @POST("index.php?r=Apiservice/getItem")
    fun getItem(
        @Field("service_id") service_id: String,
        @Field("category_id") category_id: String
    ): Call<List<Item>>


    @POST("index.php?r=Apijob/save")
    fun SaveNewJob(@Body job: Job): Call<JobResult>

    @FormUrlEncoded
    @POST("index.php?r=Apijob/Returnlist")
    fun RetunList(@Field("customerId") customerId: String): Call<List<JobReturnItem>>

    @POST("index.php?r=Apiinvoice/save")
    fun SaveBill(@Body invoice: Invoice): Call<InvoiceResult>

    @FormUrlEncoded
    @POST("index.php?r=apipayment/getBalance")
    fun getDue(@Field("customerId") customerId: String): Call<CustomerDue>

    @FormUrlEncoded
    @POST("index.php?r=apipayment/save")
    fun SavePayment(
        @Field("customerId") customerId: String,
        @Field("amount") amount: Float,
        @Field("created_by") created_by: String
    ): Call<Status>

    @FormUrlEncoded
    @POST("index.php?r=apireport/bill")
    fun LoadBillReport(
        @Field("customerId") customerId: String,
        @Field("from") from: String,
        @Field("to") to: String
    ): Call<List<ReportBillItem>>

    @FormUrlEncoded
    @POST("index.php?r=apireport/payment")
    fun LoadPaymentReport(
        @Field("customerId") customerId: String,
        @Field("from") from: String,
        @Field("to") to: String
    ): Call<List<ReportBillItem>>

    @FormUrlEncoded
    @POST("index.php?r=apireport/jobbyid")
    fun LoadJob(@Field("id") id: String): Call<JobPrint>

    @FormUrlEncoded
    @POST("index.php?r=apireport/InvoiceByPk")
    fun LoadInvoice(@Field("id") id: String): Call<InvoicePrint>

    @POST("index.php?r=ApiUser/company")
    fun loadCompany() :Call<Company>

}