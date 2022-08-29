package com.blueskyprojects.newlaundry.api

import android.content.Context
import android.graphics.Bitmap
import co.whytecreations.famuzz.utils.defaultPrefs
import co.whytecreations.famuzz.utils.get
import com.blueskyprojects.newlaundry.models.Company
import com.blueskyprojects.newlaundry.models.InvoicePrint
import com.blueskyprojects.newlaundry.models.JobPrint
import com.woosim.bt.WoosimPrinter
import java.io.ByteArrayOutputStream

class POSPrinter {
    lateinit var companyDetail: Company
    private val UTF_8 = "UTF-8"
    fun getBytes(image: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        image.recycle()
        return byteArray
    }

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
//        byte[] init = {0, 0};
//        byte[] imageInit = {0x1B, 0x2A, 33, 255, 3};
        val init = byteArrayOf(0xffff.toByte(), 27, 64)
        //        byte[] init = {0x1b, '@'};
        woosim.controlCommand(init, init.size)
        val lf0 = byteArrayOf(27, 97, 1)
        woosim.controlCommand(lf0, lf0.size)
        woosim.saveSpool(UTF_8, "\r\n", 0, false)
        woosim.saveSpool(UTF_8, "\r\n", 0, false)
//
//        val mIcon =
//            BitmapFactory.decodeResource(context.resources, R.drawable.ic_eisa_logos)
//        val imageByteArray: ByteArray = getBytes(mIcon)
////        byte[] imageByteArray = CommonFunction.decodeBitmap(mIcon);
//
//        //        byte[] imageByteArray = CommonFunction.decodeBitmap(mIcon);
//        if (imageByteArray != null)
//            woosim.printBitmap(imageByteArray, imageByteArray.size)
        woosim.saveSpool(UTF_8, "\r\n", 0, false)
        woosim.saveSpool(UTF_8, companyDetail.name + "\r\n", 0x10, true)
        woosim.saveSpool(UTF_8, companyDetail.address + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, companyDetail.address2 + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, companyDetail.mobile + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, "\r\n", 0, false)
        woosim.saveSpool(UTF_8, "\r\n", 0, false)
        val lf01 = byteArrayOf(27, 97, 0)
        woosim.controlCommand(lf01, lf01.size)
        woosim.saveSpool(UTF_8, "Job No#  : " + job.id + "\r\n", 0x8, true)
        woosim.saveSpool(UTF_8, "Customer : " + job.CustomerName + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, "Mob      : " + job.CustomerMob + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, "Date     : " + job.JobDate + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, "Vehicle  : " + job.CustomerVNo + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, "--------------------------------\r\n", 0, false)
        val items = job.items
        var i = 1
        var lastServerId = "0"
        var heading = "Item" + space(8) + "Qty" + space(4) + "Rate" + space(3) + "Total\r\n";
        woosim.saveSpool(UTF_8, heading, 0x8, false)
        woosim.saveSpool(UTF_8, "--------------------------------\r\n", 0, false)
        var total_qty = 0;
        for (each in items) {
            if (lastServerId != each.ServiceId) {
                lastServerId = each.ServiceId
                woosim.saveSpool(UTF_8, each.ServiceName + "\r\n", 0x8, true)
                i = 1
            }
            var itemName = each.ItemName.toSmall()
            var line =
                itemName + space(12 - itemName.length) + space(4 - each.Qty.length) + each.Qty + space(
                    7 - each.Rate.length
                ) + each.Rate + space(8 - each.Total.length) + each.Total
            println(line)
            woosim.saveSpool(UTF_8, line + "\r\n", 0x8, false)

            total_qty += each.Qty.toInt()

        }

        woosim.saveSpool(UTF_8, "--------------------------------\r\n", 0, false)
        woosim.saveSpool(UTF_8, "Items  :" + total_qty.toString() + "\r\n", 0x8, true)
        woosim.saveSpool(UTF_8, "Total  : AED " + job.Total + "\r\n", 0x8, true)
        woosim.saveSpool(UTF_8, "\r\n", 0x10, true)
        woosim.saveSpool(UTF_8, "\r\n", 0x10, true)
        woosim.saveSpool(UTF_8, "\r\n", 0x10, true)
        val ff = byteArrayOf(0x0c)
        woosim.controlCommand(ff, ff.size)
        woosim.printSpool(true)
    }


    fun printInvoice(woosim: WoosimPrinter, context: Context, job: InvoicePrint) {
        setCompany(context)
//        byte[] init = {0, 0};
//        byte[] imageInit = {0x1B, 0x2A, 33, 255, 3};
        val init = byteArrayOf(0xffff.toByte(), 27, 64)
        //        byte[] init = {0x1b, '@'};
        woosim.controlCommand(init, init.size)
        val lf0 = byteArrayOf(27, 97, 1)
        woosim.controlCommand(lf0, lf0.size)
        woosim.saveSpool(UTF_8, "\r\n", 0, false)
        woosim.saveSpool(UTF_8, "\r\n", 0, false)
//
//        val mIcon =
//            BitmapFactory.decodeResource(context.resources, R.drawable.ic_eisa_logos)
//        val imageByteArray: ByteArray = getBytes(mIcon)
////        byte[] imageByteArray = CommonFunction.decodeBitmap(mIcon);
//
//        //        byte[] imageByteArray = CommonFunction.decodeBitmap(mIcon);
//        if (imageByteArray != null)
//            woosim.printBitmap(imageByteArray, imageByteArray.size)
        woosim.saveSpool(UTF_8, "\r\n", 0, false)
        woosim.saveSpool(UTF_8, companyDetail.name + "\r\n", 0x10, true)
        woosim.saveSpool(UTF_8, companyDetail.address + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, companyDetail.address2 + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, companyDetail.mobile + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, "\r\n", 0, false)
        woosim.saveSpool(UTF_8, "\r\n", 0, false)
        val lf01 = byteArrayOf(27, 97, 0)
        woosim.controlCommand(lf01, lf01.size)
        woosim.saveSpool(UTF_8, "Invoice# : " + job.id + "\r\n", 0x8, true)
        woosim.saveSpool(UTF_8, "Customer : " + job.CustomerName + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, "Mob      : " + job.CustomerMob + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, "Date     : " + job.JobDate + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, "Vehicle  : " + job.CustomerVNo + "\r\n", 0x8, false)
        woosim.saveSpool(UTF_8, "--------------------------------\r\n", 0, false)
        val items = job.items
        var i = 1
        var lastServerId = "0"
        var heading = "Item" + space(8) + "Qty" + space(4) + "Rate" + space(3) + "Total\r\n";
        woosim.saveSpool(UTF_8, heading, 0x8, false)
        woosim.saveSpool(UTF_8, "--------------------------------\r\n", 0, false)
        var total_qty = 0;
        for (each in items) {
            if (lastServerId != each.ServiceId) {
                lastServerId = each.ServiceId
                woosim.saveSpool(UTF_8, each.ServiceName + "\r\n", 0x8, true)
                i = 1
            }
            var itemName = each.ItemName.toSmall()
            var line =
                itemName + space(12 - itemName.length) + space(4 - each.Qty.length) + each.Qty + space(
                    7 - each.Rate.length
                ) + each.Rate + space(8 - each.Total.length) + each.Total
            println(line)
            woosim.saveSpool(UTF_8, line + "\r\n", 0x8, false)

            total_qty += each.Qty.toInt()

        }

        woosim.saveSpool(UTF_8, "--------------------------------\r\n", 0, false)
        woosim.saveSpool(UTF_8, "Items  :" + total_qty.toString() + "\r\n", 0x8, true)
        woosim.saveSpool(UTF_8, "Total  : AED " + job.Total + "\r\n", 0x8, true)
        woosim.saveSpool(UTF_8, "\r\n", 0x10, true)
        woosim.saveSpool(UTF_8, "\r\n", 0x10, true)
        woosim.saveSpool(UTF_8, "\r\n", 0x10, true)
        val ff = byteArrayOf(0x0c)
        woosim.controlCommand(ff, ff.size)
        woosim.printSpool(true)
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
        return if (this.length > len) this.substring(0, len - 3) + "..." else this
    }
}