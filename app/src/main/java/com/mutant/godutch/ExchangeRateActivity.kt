package com.mutant.godutch

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mutant.godutch.model.ExchangeRate
import com.mutant.godutch.server.WebAgent
import com.mutant.godutch.utils.Utility.Companion.calculateExchangeRate
import kotlinx.android.synthetic.main.activity_exchange_rate.*
import kotlinx.android.synthetic.main.list_item_exchange_rate.view.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException



class ExchangeRateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_rate)
        fetchExchangeRate()
    }

    private fun fetchExchangeRate() {
        WebAgent.fetchExchangeRate(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                var exchangeRates: MutableList<ExchangeRate> = mutableListOf()
                val countries = resources.getStringArray(R.array.countries)
                val countriesChinese = resources.getStringArray(R.array.countries_chinese)
                val jsonStr = response.body().string()
                val erJson = JSONObject(jsonStr)
                for((index, element) in countries.withIndex()) {
                    val lastUpdated = erJson.getJSONObject(element).optString("UTC")
                    val rate = calculateExchangeRate(jsonStr, element)
                    exchangeRates.add(ExchangeRate(countriesChinese[index].toString(), rate, lastUpdated))
                }

                runOnUiThread { setupExchangeRateListView(exchangeRates) }
            }

            override fun onFailure(call: Call, e: IOException) {
                // TODO
            }
        })
    }

    private fun setupExchangeRateListView(exchangeRates: List<ExchangeRate>) {
        val MyLayoutManager = LinearLayoutManager(this@ExchangeRateActivity)
        MyLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler_view_exchange_rate.adapter = Adapter(this@ExchangeRateActivity, exchangeRates)
        recycler_view_exchange_rate.layoutManager = MyLayoutManager
    }

    class Adapter(val activity: Activity, val exchangeRates: List<ExchangeRate>) : RecyclerView.Adapter<ViewHolderRate>() {

        override fun onBindViewHolder(holder: ViewHolderRate, position: Int) {
            val exchangeRate = exchangeRates[position]
            holder.mTextViewCountry.text = exchangeRate.country
            holder.mTextViewRate.text = exchangeRate.rate.toString()
            holder.mTextViewLastUpdated.text = exchangeRate.lastUpdated
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolderRate {
            val view = LayoutInflater.from(activity).inflate(R.layout.list_item_exchange_rate, parent, false)
            val holder = ViewHolderRate(view)
            return holder
        }

        override fun getItemCount(): Int {
            return exchangeRates.size
        }

    }

    class ViewHolderRate(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mTextViewCountry = itemView.textView_country
        val mTextViewRate = itemView.textView_rate
        val mTextViewLastUpdated = itemView.textView_last_updated
    }
}
