package com.mutant.godutch.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.SeekBar
import com.mutant.godutch.R

/**
 * Created by evanfang102 on 2017/7/28.
 */
class ThumbNumberSeekBar: SeekBar {

    constructor(context: Context) : super(context) {
        initializeSetting()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initializeSetting()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initializeSetting()
    }

    fun initializeSetting() {
    }

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        val thumb_x = (this.progress.toFloat() / this.max.toFloat()) * this.width
        val middle = this.height.toFloat()
        var paint = Paint()
        paint.color = R.color.primary_dark_material_dark
        paint.textSize = 60f
        c.drawText(this.progress.toString() + "%", thumb_x, middle, paint)
    }
}