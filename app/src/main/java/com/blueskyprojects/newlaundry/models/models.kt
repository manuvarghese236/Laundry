package com.blueskyprojects.newlaundry.data.model

import com.google.gson.annotations.SerializedName


data class Customer(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("vehicle_no") val vehicle_no: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("address") val address: String,
    @SerializedName("email_id") val email: String
)

data class CustomerResponse(
    val status: String,
    val data: List<Customer>
)

data class ResponseStatus(
    val id: Int,
    val status: Boolean,
    val errorMessage: String

)

data class Service(
    val id: Int,
    val name: String,
    val pic: String
)

data class Category(
    val id: Int,
    val name: String,
    val pic: String
)

data class Item(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("pic") val pic: String,
    @SerializedName("rate") val rate: Float,
    @SerializedName("urgent_fees") val urgent_fees: Float,
    @SerializedName("fixed_price") val fixed_price: Boolean
)

data class SelectedItem(
    val itemName: String,
    val itemId: String,
    val serviceId: String,
    val serviceName: String,
    @SerializedName("fixed_price") val fixed_price: Boolean,
    @SerializedName("urgent_fees") val urgent_fees: Float,
    val pic: String,
    var urgent :String,
    var rate: Float,
    var qty: Int,
    var total: Float
)

data class LoginDataResponse(
    @SerializedName("user_id") val user_id: String,
    @SerializedName("name") val name: String
)


data class LoginResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("data") val data: LoginDataResponse
)

data class CustomerDue(
    @SerializedName("due") val due: Float
)

data class ReportBillItem(
    @SerializedName("id") val id: String,
    @SerializedName("customer_name") val CustomerName: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("date") val date: String
)