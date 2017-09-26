package com.mutant.godutch

import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.mutant.godutch.model.Event
import com.mutant.godutch.model.Friend
import kotlinx.android.synthetic.main.activity_checkout.*




/**
 * Created by Mutant on 2017/6/11.
 */

class CheckActivity : BaseActivity() {

    lateinit var mEvents: ArrayList<Event>

    companion object {
        private val BUNDLE_KEY_LIST_EVENTS = "BUNDLE_KEY_LIST_EVENTS"

        fun getIntent(context: Context, events: ArrayList<Event>?): Intent {
            val intent = Intent(context, CheckActivity::class.java)
            intent.putParcelableArrayListExtra(BUNDLE_KEY_LIST_EVENTS, events)
            return intent
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_checkout

    override fun setup() {
        mEvents = intent.getParcelableArrayListExtra<Event>(BUNDLE_KEY_LIST_EVENTS)
        setupTotal()
        setupCheckout()
    }

    private fun setupTotal() {
        var totalMap: HashMap<String, Double> = hashMapOf()
        mEvents.forEach {
            val exchangeRate = it.exchangeRate
            val v = totalMap[exchangeRate!!.jsonKey]
            if(v == null) {
                totalMap.put(exchangeRate!!.jsonKey, it.total)
            } else {
                totalMap.put(exchangeRate!!.jsonKey, v + it.total)
            }
        }

        totalMap.forEach { (k, v) ->
            textView_total.append("$k $$v\n")
        }
    }

    private fun setupCheckout() {
        val friendsShared = ArrayList<Friend>()
        for (event in mEvents) {
            for (friendInEvent in event.friendsShared) {
                var isExists = false
                for (friendInShared in friendsShared) {
                    if (friendInShared.uid == friendInEvent.uid) {
                        friendInShared.debt = friendInShared.debt + friendInEvent.debt
                        isExists = true
                        break
                    }
                }

                if (!isExists) {
                    friendsShared.add(friendInEvent)
                }
            }
        }

        var mListResult: ArrayList<Integer> = arrayListOf()
        var mHashMap: HashMap<Integer, List<Integer>> = hashMapOf()
        mHashMap.forEach{(k, v) ->
            if(mListResult.size < v.size) {
                mListResult = ArrayList(v)
            }
        }

        // TODO
        recycler_view_paid.adapter = AdapterPaidCheck(this, friendsShared, arrayListOf(), mEvents[0].exchangeRate, null)
        recycler_view_paid.layoutManager = LinearLayoutManager(this)
        // TODO
        recycler_view_shared.adapter = AdapterPaidCheck(this, friendsShared, arrayListOf(), mEvents[0].exchangeRate, null)
        recycler_view_shared.layoutManager = LinearLayoutManager(this)
    }

}
