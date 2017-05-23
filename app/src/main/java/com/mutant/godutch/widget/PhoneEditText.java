package com.mutant.godutch.widget;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Patterns;

import com.mutant.godutch.utils.Utility;

public class PhoneEditText extends android.support.v7.widget.AppCompatEditText implements CustomEditTextInterface {
	private final int maxLength = 10;
	
	public PhoneEditText(Context context) {
		super(context);
		initializeSetting();
	}
	
	public PhoneEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeSetting();
	}
	
	public PhoneEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeSetting();
	}
	
	@Override
	public void initializeSetting() {
		this.setInputType(InputType.TYPE_CLASS_PHONE);
		Utility.setMaxLength(this, maxLength);
	}
	
	public boolean isValid() {
		return Patterns.PHONE.matcher(this.getText().toString().trim()).matches();
	}
	
	public boolean isEmpty() {
		return TextUtils.isEmpty(this.getText().toString().trim());
	}
	
	@Override
	public void setError(CharSequence error) {
		this.requestFocus();
		super.setError(error);
	}
}