package com.mutant.godutch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        setupFabDone()
    }

    private fun setupFabDone() {
        fab_done.setOnClickListener {
            var intent = Intent()
            intent.putExtra(NewEventActivity.BUNDLE_KEY_EXCHANGE_RATE,
                    (recycler_view_exchange_rate.adapter as Adapter).getSelectedItem())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun fetchExchangeRate() {
        WebAgent.fetchExchangeRate(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                var exchangeRates: MutableList<ExchangeRate> = mutableListOf()
                val jsonKeys = resources.getStringArray(R.array.countries_json_key)
                val countryNames = resources.getStringArray(R.array.countries_name)
                val jsonStr = response.body().string()
                val erJson = JSONObject(jsonStr)
                for ((index, element) in jsonKeys.withIndex()) {
                    // TO GMT+8
                    val lastUpdated = erJson.getJSONObject("USD" + element).optString("UTC")
                    val rate = calculateExchangeRate(jsonStr, "USD" + element)
                    exchangeRates.add(ExchangeRate(jsonKeys[index].toString(), rate, lastUpdated, countryNames[index]))
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

        var selectedPos: Int = 0

        override fun onBindViewHolder(holder: ViewHolderRate, position: Int) {
            holder.itemView.isSelected = selectedPos == position

            val exchangeRate = exchangeRates[position]
            holder.mTextViewCountry.text = exchangeRate.country
            holder.mTextViewRate.text = exchangeRate.rate.toString()
            holder.mTextViewLastUpdated.text = exchangeRate.lastUpdated
            holder.itemView.setOnClickListener {
                selectedPos = position
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolderRate {
            val view = LayoutInflater.from(activity).inflate(R.layout.list_item_exchange_rate, parent, false)
            val holder = ViewHolderRate(view)
            return holder
        }

        override fun getItemCount(): Int {
            return exchangeRates.size
        }

        fun getSelectedItem(): ExchangeRate {
            return exchangeRates[selectedPos]
        }

    }

    class ViewHolderRate(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mTextViewCountry: TextView = itemView.textView_country
        val mTextViewRate: TextView = itemView.textView_rate
        val mTextViewLastUpdated: TextView = itemView.textView_last_updated
    }
}
