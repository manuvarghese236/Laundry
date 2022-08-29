package com.blueskyprojects.newlaundry.models

import com.blueskyprojects.newlaundry.data.model.SelectedItem
import com.google.gson.annotations.SerializedName

data class JobHeader(
    val customerID: String,
    var CustomerName: String,
    var CustomerMob: String,
    var created_by :String,
    var _date: String
)

data class Job(
    val head: JobHeader,
    val Item: List<SelectedItem>
)

data class JobResult(
    val status: Boolean,
    val errorMessage: String,
    val jobId: Int
)

data class InvoiceResult(
    val status: Boolean,
    val errorMessage: String,
    val Id: Int
)

data class JobReturnItem(
    val id: String,
    val pic: String,
    val serviceName: String,
    val serviceId: Int,
    val productId: Int,
    val productName: String,
    val rate: Float,
    val qty: Int,
    var total: Float,
    val total_return_qty: Int,
    var returned_qty: Int,
    val job_date: String,
    val job_id: Int
)

data class Invoice(
    val customerID: String,
    var created_by :String,
    val item: List<JobReturnItem>
)

data class Status(
    val id: String,
    var created_by :String,
    val status: Boolean
)

data class JobItemPrint(
    @SerializedName("id") val id: String,
    @SerializedName("ItemName") val ItemName: String,
    @SerializedName("ItemId") val ItemId: String,
    @SerializedName("ServiceName") val ServiceName: String,
    @SerializedName("ServiceId") val ServiceId: String,
    @SerializedName("Qty") val Qty: String,
    @SerializedName("Rate") val Rate: String,
    @SerializedName("Total") val Total: String
)

data class JobPrint(
    @SerializedName("id") val id: String,
    @SerializedName("CustomerName") val CustomerName: String,
    @SerializedName("CustomerMob") val CustomerMob: String,
    @SerializedName("CustomerId") val CustomerId: String,
    @SerializedName("CustomerVNo") val CustomerVNo: String,
    @SerializedName("JobDate") val JobDate: String,
    @SerializedName("Total") val Total: String,

    val items: List<JobItemPrint>
)
data class InvoiceItemPrint(
    @SerializedName("id") val id: String,
    @SerializedName("ItemName") val ItemName: String,
    @SerializedName("ItemId") val ItemId: String,
    @SerializedName("ServiceName") val ServiceName: String,
    @SerializedName("ServiceId") val ServiceId: String,
    @SerializedName("Qty") val Qty: String,
    @SerializedName("Rate") val Rate: String,
    @SerializedName("Total") val Total: String
)

data class InvoicePrint(
    @SerializedName("id") val id: String,
    @SerializedName("CustomerName") val CustomerName: String,
    @SerializedName("CustomerMob") val CustomerMob: String,
    @SerializedName("CustomerId") val CustomerId: String,
    @SerializedName("CustomerVNo") val CustomerVNo: String,
    @SerializedName("JobDate") val JobDate: String,
    @SerializedName("Total") val Total: String,

    val items: List<InvoiceItemPrint>
)
data class Company(
    @SerializedName("name") var name: String,
    @SerializedName("address") var address: String,
    @SerializedName("address2") var address2: String,
    @SerializedName("mobile") var mobile: String,
    @SerializedName("website") var website: String

)