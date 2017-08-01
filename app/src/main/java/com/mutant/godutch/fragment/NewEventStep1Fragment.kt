package com.mutant.godutch.fragment

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mutant.godutch.NewEventActivity
import com.mutant.godutch.NewGroupActivity
import com.mutant.godutch.R
import com.mutant.godutch.widget.EventTypeWidget
import com.mutant.godutch.widget.EventTypeWidget.TYPE
import kotlinx.android.synthetic.main.fragment_new_event_step_1.*
import kotlinx.android.synthetic.main.fragment_new_event_step_1.view.*

/**
 * Created by evanfang102 on 2017/7/27.
 */

class NewEventStep1Fragment : Fragment(), EventTypeWidget.Companion.OnSelectionChanged {

    lateinit var mActivity: NewEventActivity
    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mActivity = (activity as NewEventActivity)
        rootView = inflater!!.inflate(R.layout.fragment_new_event_step_1, container, false)
        return rootView
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPhotoOnClickListener(view)
        setupFabNextStep()
        setupEventTypeWidget()
    }

    private fun setupEventTypeWidget() {
        eventTypeWidget.onSelectionChanged = this
    }

    override fun onChanged(type: TYPE) {
        when(type) {
            TYPE.FOOD -> imageView_photo.setImageResource(R.drawable.food_default_640)
            TYPE.SHOPPING -> imageView_photo.setImageResource(R.drawable.shopping_default_640)
            TYPE.HOTEL -> imageView_photo.setImageResource(R.drawable.hotel_default_640)
            TYPE.TICKET -> imageView_photo.setImageResource(R.drawable.ticket_default_640)
        }
    }

    fun setupPhotoOnClickListener(rootView: View) {
        rootView.imageView_photo.setOnClickListener { onClickTakeAPhoto() }
    }

    fun onClickTakeAPhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            startActivityForResult(takePictureIntent, NewGroupActivity.REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NewGroupActivity.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            val imageBitmap = extras?.get("data") as Bitmap
            imageView_photo.setImageBitmap(imageBitmap)
            mActivity.isTakePhoto = true
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setupFabNextStep() {
        fab_next_step.setOnClickListener {
            mActivity.nextStep()
        }
    }

}
