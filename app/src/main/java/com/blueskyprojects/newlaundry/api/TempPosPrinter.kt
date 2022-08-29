package com.blueskyprojects.newlaundry.api

import android.content.Context
import co.whytecreations.famuzz.utils.defaultPrefs
import co.whytecreations.famuzz.utils.get
import com.blueskyprojects.newlaundry.models.Company
import com.blueskyprojects.newlaundry.models.InvoicePrint
import com.blueskyprojects.newlaundry.models.JobPrint
import com.woosim.bt.WoosimPrinter

class TempPosPrinter {
    lateinit var companyDetail: Company
    fun setCompany(context: Context) {
        companyDetail = Company("","","","","")
        companyDetail.name = defaultPrefs(context)["company", "1"].toString()
        companyDetail.address = defaultPrefs(context)["address", "1"].toString()
        companyDetail.address2 = defaultPrefs(context)["address2", "1"].toString()
        companyDetail.mobile = defaultPrefs(context)["mobile", "1"].toString()
        companyDetail.website = defaultPrefs(context)["website", "1"].toString()
    }
    fun printJob(woosim: WoosimPrinter, context: Context, job: JobPrint) {
        setCompany(context)
        val items = job.items
        var i = 1
        var lastServerId = "0"
        var heading = "Item" + space(8) + "Qty" + space(7) + "Rate" + space(6) + "Total\r\n";
        println(heading)
        for (each in items) {
            if (lastServerId != each.ServiceId) {
                lastServerId = each.ServiceId
                println(each.ServiceName)
            }
            var itemName = each.ItemName.toSmall()
            var line =
                itemName + space(12 - itemName.length) + space(4 - each.Qty.length) + each.Qty + space(
                    10 - each.Rate.length
                ) + each.Rate + space(11 - each.Total.length) + each.Total
            println(line)

        }

    }

    fun space(n: Int): String {
        var str = "";
        if (n > 0) {
            for (i in 1..n) {
                str += " "
            }
        }
        return str;
    }

    private fun String.toSmall(): String {
        val len = 12
        return if (this.length > len) this.substring(0, len - 3) + ".." else this
    }

    fun printInvoice(woosim: WoosimPrinter, context: Context,_job: InvoicePrint) {
        setCompany(context)
        val items = _job.items
        var i = 1
        var lastServerId = "0"
        var heading = "Item" + space(8) + "Qty" + space(7) + "Rate" + space(6) + "Total\r\n";
        println(heading)
        for (each in items) {
            if (lastServerId != each.ServiceId) {
                lastServerId = each.ServiceId
                println(each.ServiceName)
            }
            var itemName = each.ItemName.toSmall()
            var line =
                itemName + space(12 - itemName.length) + space(4 - each.Qty.length) + each.Qty + space(
                    10 - each.Rate.length
                ) + each.Rate + space(11 - each.Total.length) + each.Total
            println(line)

        }
    }

}