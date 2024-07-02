package dev.nomadicprogrammer.spendly.smsparser.common.usecases

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext

interface Source{
    fun parse() : Categories
}

class BundledCategories(
    @ApplicationContext val context: Context,
    private val filePath : String
) : Source{

    override fun parse() : Categories {
        // read from assets and parse the json using Gson and prepare list of categories
        val jsonString = context.assets.open(filePath).bufferedReader().use {
            val content = it.readText()
            it.close()
            content
        }

        return Gson().fromJson(jsonString, Categories::class.java)
    }

}