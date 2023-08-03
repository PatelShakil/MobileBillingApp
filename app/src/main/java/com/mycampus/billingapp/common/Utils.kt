package com.mycampus.billingapp.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class Utils {
    companion object{
    fun generateRandomValue(char : Int): String {
        val characterSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        val random = Random(System.currentTimeMillis())
        val randomValue = StringBuilder()

        repeat(char) {
            val randomIndex = random.nextInt(characterSet.length)
            randomValue.append(characterSet[randomIndex])
        }

        return randomValue.toString()
    }
    fun convertLongToDate(timestamp: Long, format: String): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(date)
    }
    }
}