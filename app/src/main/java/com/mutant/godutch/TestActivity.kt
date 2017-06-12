package com.mutant.godutch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatTextView
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by evanfang102 on 2017/6/9.
 */

class TestActivity : BaseActivity() {

    override val layoutId: Int
        get() = R.layout.activity_test //To change initializer of created properties use File | Settings | File Templates.

    override fun findViews() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setup() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myObservable = Observable.create(ObservableOnSubscribe<String> { observableEmitter ->
            observableEmitter.onNext("1")
            observableEmitter.onNext("2")
            observableEmitter.onComplete()
        })

        val myObserver = object : Observer<String> {
            override fun onSubscribe(disposable: Disposable) {

            }

            override fun onNext(s: String) {
                val textView = findViewById(R.id.textView_observer) as AppCompatTextView
                textView.append(s)
            }

            override fun onError(throwable: Throwable) {

            }

            override fun onComplete() {

            }
        }

        myObservable.subscribe(myObserver)
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, TestActivity::class.java)
        }
    }
}
